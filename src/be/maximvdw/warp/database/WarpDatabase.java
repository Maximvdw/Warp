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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import be.maximvdw.warp.Warp;
import be.maximvdw.warp.WarpPlugin;
import be.maximvdw.warp.config.Configuration;
import be.maximvdw.warp.ui.SendConsole;

public class WarpDatabase {
	static MySQL db = null; // Database connection

	public static void initDatabase() {
		/* Initialize a new database connection */
		db = new MySQL(WarpPlugin.getInstance().getLogger(),
				Configuration.prefix, Configuration.hostname,
				Configuration.portnmbr, Configuration.database,
				Configuration.username, Configuration.password);
		db.open();
		// Check if warps table exists
		if (db != null) {
			SendConsole.info("Checking tables ...");
			if (!db.checkTable(Configuration.prefix + "warps")) {
				// Warps table does not exist
				SendConsole.info("Creating table '" + Configuration.prefix
						+ "warps'...");
				String query = getQuery("WARPS_CREATE").replace("{PREFIX}",
						Configuration.prefix);
				db.createTable(query);
			}
		}
	}

	public static void deleteWarp(final Warp warp) {
		// Thread the database connection
		Bukkit.getScheduler().runTaskAsynchronously(WarpPlugin.getInstance(),
				new Runnable() {
					public void run() {
						// Check if a connection has been made
						String query = getQuery("WARPS_DELETE");
						query = query.replace("{PREFIX}", Configuration.prefix);
						query = query.replace("{ID}", "" + warp.getID());

						// Execute query
						SendConsole.info("Removing warp '" + warp.getName()
								+ "' from the database ...");
						db.query(query);
					}
				});
	}

	/**
	 * Add a warp to the database
	 * 
	 * @param warp
	 *            Warp to add to the database
	 */
	public static void addWarp(final Warp warp) {
		// Thread the database connection
		Bukkit.getScheduler().runTaskAsynchronously(WarpPlugin.getInstance(),
				new Runnable() {
					public void run() {
						// Check if a connection has been made
						String query = getQuery("WARPS_INSERT");
						query = query.replace("{PREFIX}", Configuration.prefix);
						query = query.replace("{NAME}", warp.getName());
						query = query.replace("{OWNER}", ""
								+ warp.getOwner().getUniqueId());
						Location location = warp.getLocation();
						query = query.replace("{XPOS}", "" + location.getX());
						query = query.replace("{YPOS}", "" + location.getY());
						query = query.replace("{ZPOS}", "" + location.getZ());
						query = query.replace("{PITCH}",
								"" + location.getPitch());
						query = query.replace("{YAW}", "" + location.getYaw());
						query = query.replace("{WORLD}", ""
								+ location.getWorld().getName());

						// Execute query
						SendConsole.info("Adding warp '" + warp.getName()
								+ "' to the database ...");
						db.query(query);
					}
				});
	}

	/**
	 * Get all warps from the database
	 */
	public static void getWarps() {
		final WarpPlugin wp = WarpPlugin.getInstance();
		// Thread the database connection
		Bukkit.getScheduler().runTaskAsynchronously(wp,
				new Runnable() {
					public void run() {
						String query = getQuery("WARPS_SELECT");
						query = query.replace("{PREFIX}", Configuration.prefix);
						ResultSet result = db.query(query);
						SendConsole.info("Loading warps from database ..."); // Log to console
						// Get the result
						try {
							while (result.next()) {
								try {
									String name = result.getString("name");
									Player owner = null;
									int id = result.getInt("id");
									float x = result.getFloat("x");
									float y = result.getFloat("y");
									float z = result.getFloat("z");
									float pitch = result.getFloat("pitch");
									float yaw = result.getFloat("yaw");
									World world = wp.getServer().getWorld(
											result.getString("world"));
									Location location = new Location(world, x,
											y, z, yaw, pitch);
									Warp warp = new Warp(name, owner, location,
											id);
									warp.saveWarpCache();
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				});
	}

	/**
	 * Get the query string from sql/ resource
	 * 
	 * @param queryFile
	 *            Query file to load
	 * @return Query string
	 */
	private static String getQuery(String queryFile) {
		InputStream stream = WarpPlugin.getInstance().getResource(
				"sql/" + queryFile + ".txt");
		if (stream != null) {
			try {
				return convertStreamToString(stream);
			} catch (IOException e) {
				SendConsole.severe("Could not load the SQL query! '"
						+ queryFile + "'");
				return "";
			}
		} else {
			return "";
		}
	}

	private static String convertStreamToString(InputStream is)
			throws IOException {
		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is,
						"UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}

			return writer.toString().replaceAll("\\r", "");
		} else {
			return "ERROR WHILE LOADING EXAMPLE!";
		}
	}
}
