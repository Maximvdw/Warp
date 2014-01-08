/*
 * Warp Bukkit Plugin
 * Copyright (C) 2013 Maxim Van de Wynckel <Maximvdw> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package be.maximvdw.warp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class MySQL extends Database {
	private String hostname = "localhost"; // MySQL Hostname
	private String portnmbr = "3306"; // MySQL port
	private String username = "minecraft"; // MySQL username
	private String password = ""; // MySQL password
	private String database = "minecraft"; // MySQL database

	public MySQL(Logger log, String prefix, String hostname, String portnmbr,
			String database, String username, String password) {
		super(log, prefix, "[MySQL] ");
		this.hostname = hostname;
		this.portnmbr = portnmbr;
		this.database = database;
		this.username = username;
		this.password = password;
	}

	@Override
	protected boolean initialize() {
		try {
			Class.forName("com.mysql.jdbc.Driver"); // Check that server's Java
													// has MySQL support.
			return true;
		} catch (ClassNotFoundException e) {
			this.writeError("Class Not Found Exception: " + e.getMessage()
					+ ".", true);
			return false;
		}
	}

	@Override
	public Connection open() {
		this.open(true);
		return null;
	}

	public Connection open(boolean showError) {
		if (initialize()) {
			String url = "";
			try {
				url = "jdbc:mysql://" + this.hostname + ":" + this.portnmbr
						+ "/" + this.database;
				this.connection = DriverManager.getConnection(url,
						this.username, this.password);

			} catch (SQLException e) {
				if (showError) {
					this.writeError(url, true);
					this.writeError(
							"Could not be resolved because of an SQL Exception: "
									+ e.getMessage() + ".", true);
				}
			}
		}
		return null;
	}

	@Override
	public void close() {
		// Connection connection = open();
		try {
			if (connection != null)
				connection.close();
		} catch (Exception e) {
			this.writeError(
					"Failed to close database connection: " + e.getMessage(),
					true);
		}
	}

	@Override
	public Connection getConnection() {
		// if (this.connection == null)
		// return open();
		return this.connection;
	}

	@Override
	public boolean checkConnection() {
		if (connection != null)
			return true;
		return false;
	}

	@Override
	public ResultSet query(String query) {
		// Connection connection = null;
		Statement statement = null;
		ResultSet result = null/* new JdbcRowSetImpl() */;
		try {
			// connection = open();
			// if (checkConnection())
			for (int counter = 0; counter < 5 && result == null; counter++) {
				try {
					statement = this.connection.createStatement();
					result = statement.executeQuery("SELECT CURTIME()");
				} catch (SQLException e) {
					if (counter == 4) {
						throw e;
					} else {
						if (e.getMessage().contains("connection closed")) {
							this.writeError(
									"Error in SQL query. Attempting to reestablish connection. Attempt #"
											+ Integer.toString(counter + 1)
											+ "!", false);
							this.open(false);
						} else {
							throw e;
						}
					}
				}
			}

			switch (this.getStatement(query)) {
			case SELECT:
				result = statement.executeQuery(query);
				break;

			default:
				statement.executeUpdate(query);
			}
			return result;
		} catch (SQLException e) {
			this.writeError("Error in SQL query: " + e.getMessage(), false);
		}
		return result;
	}

	@Override
	public PreparedStatement prepare(String query) {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(query);
			return ps;
		} catch (SQLException e) {
			if (!e.toString().contains("not return ResultSet"))
				this.writeError(
						"Error in SQL prepare() query: " + e.getMessage(),
						false);
		}
		return ps;
	}

	@Override
	public boolean createTable(String query) {
		Statement statement = null;
		try {
			if (query.equals("") || query == null) {
				this.writeError("SQL query empty: createTable(" + query + ")",
						true);
				return false;
			}

			statement = connection.createStatement();
			statement.execute(query);
			return true;
		} catch (SQLException e) {
			this.writeError(e.getMessage(), true);
			return false;
		} catch (Exception e) {
			this.writeError(e.getMessage(), true);
			return false;
		}
	}

	@Override
	public boolean checkTable(String table) {
		try {
			Statement statement = connection.createStatement();

			ResultSet result = statement.executeQuery("SELECT * FROM " + table);

			if (result == null)
				return false;
			if (result != null)
				return true;
		} catch (SQLException e) {
			if (e.getMessage().contains("exist")) {
				return false;
			} else {
				this.writeError("Error in SQL query: " + e.getMessage(), false);
			}
		}

		if (query("SELECT * FROM " + table) == null)
			return true;
		return false;
	}

	@Override
	public boolean wipeTable(String table) {
		Statement statement = null;
		String query = null;
		try {
			if (!this.checkTable(table)) {
				this.writeError("Error wiping table: \"" + table
						+ "\" does not exist.", true);
				return false;
			}
			statement = this.connection.createStatement();
			query = "DELETE FROM " + table + ";";
			statement.executeUpdate(query);

			return true;
		} catch (SQLException e) {
			if (!e.toString().contains("not return ResultSet"))
				return false;
		}
		return false;
	}
}