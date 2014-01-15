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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import be.maximvdw.warp.Warp;
import be.maximvdw.warp.WarpLink;
import be.maximvdw.warp.WarpPlugin;
import be.maximvdw.warp.config.Configuration;
import be.maximvdw.warp.ui.SendConsole;
import be.maximvdw.warp.utils.PlayerUtils;

public class WarpDatabase {
	static Database db = null; // Database connection
	static boolean flagWarpsLoaded = false;
	static WarpPlugin wp = null; // Warp Plugin

	/**
	 * Initialize the warpdatabase
	 * 
	 * @param plugin
	 *            WarpPlugin
	 */
	public WarpDatabase(WarpPlugin plugin) {
		wp = plugin;
	}

	/**
	 * Initialize and load the database
	 */
	public static void initDatabase() {
		// Thread the database connection
		Bukkit.getScheduler().runTaskAsynchronously(wp, new Runnable() {
			public void run() {
				/* Initialize a new database connection */
				if (Configuration.useMySQL){
				db = new MySQL(WarpPlugin.getInstance().getLogger(),
						Configuration.prefix, Configuration.hostname,
						Configuration.portnmbr, Configuration.database,
						Configuration.username, Configuration.password);
				}else{
					// Use SQLite
					db = new SQLite(WarpPlugin.getInstance().getLogger(),
							Configuration.prefix, "warpDatabase",
							wp.getDataFolder().toString());
				}
				db.open();
				// Check if warps table exists
				if (db != null) {
					SendConsole.info("Checking tables ...");
					if (!db.checkTable(Configuration.prefix + "warps")) {
						// Warps table does not exist
						SendConsole.info("Creating table '"
								+ Configuration.prefix + "warps'...");
						String query = getQuery("WARPS_CREATE").replace(
								"{PREFIX}", Configuration.prefix);
						db.createTable(query);
					}
					if (!db.checkTable(Configuration.prefix + "warplinks")) {
						// Warp link table does not exist
						SendConsole.info("Creating table '"
								+ Configuration.prefix + "warplinks'...");
						String query = getQuery("WARPLINKS_CREATE").replace(
								"{PREFIX}", Configuration.prefix);
						db.createTable(query);
					}
					if (!db.checkTable(Configuration.prefix + "warpcfg")) {
						// Warp cfg table does not exist
						SendConsole.info("Creating table '"
								+ Configuration.prefix + "warpcfg'...");
						String query = getQuery("WARPCFG_CREATE").replace(
								"{PREFIX}", Configuration.prefix);
						db.createTable(query);
					}
					
					/* Get warps */
					getWarps();
					/* Get warp links */
					getWarpLinks();
				}
			}
		});
	}

	/**
	 * Delete a warplink from the database
	 * 
	 * @param warp
	 */
	public static void deleteWarpLink(final WarpLink link) {
		// Thread the database connection
		Bukkit.getScheduler().runTaskAsynchronously(WarpPlugin.getInstance(),
				new Runnable() {
					public void run() {
						// Check if a connection has been made
						String query = getQuery("WARPLINKS_DELETE");
						query = query.replace("{PREFIX}", Configuration.prefix);
						query = query.replace("{ID}", "" + link.getID());

						// Execute query
						SendConsole.info("Removing warplink '"
								+ link.getWarp().getName()
								+ "' from the database ...");
						db.query(query);
					}
				});
	}

	/**
	 * Delete a warp from the database
	 * 
	 * @param warp
	 *            Warp
	 */
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
						query = getQuery("WARPCFG_DELETE");
						query = query.replace("{PREFIX}", Configuration.prefix);
						query = query.replace("{ID}", "" + warp.getID());

						// Execute query
						db.query(query);
					}
				});
	}

	/**
	 * Add a warplink to the databse
	 * 
	 * @param link
	 *            Warp Link
	 */
	public static void addWarpLink(final WarpLink link) {
		// Thread the database connection
		Bukkit.getScheduler().runTaskAsynchronously(WarpPlugin.getInstance(),
				new Runnable() {
					public void run() {
						// Check if a connection has been made
						Warp warp = link.getWarp();
						String query = getQuery("WARPLINKS_INSERT");
						query = query.replace("{PREFIX}", Configuration.prefix);
						query = query.replace("{WARPID}", "" + warp.getID());
						if (warp.getOwner() != null)
							query = query.replace("{OWNER}", ""
									+ warp.getOwner().getUniqueId());
						Location location = link.getBlock().getLocation();
						query = query.replace("{XPOS}",
								"" + (int) location.getX());
						query = query.replace("{YPOS}",
								"" + (int) location.getY());
						query = query.replace("{ZPOS}",
								"" + (int) location.getZ());
						query = query.replace("{WORLD}", ""
								+ location.getWorld().getName());

						// Execute query
						SendConsole.info("Adding warplink '" + warp.getName()
								+ "' to the database ...");
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
						if (warp.getOwner() != null)
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
						query = getQuery("WARPCFG_INSERT");
						query = query.replace("{PREFIX}", Configuration.prefix);
						query = query.replace("{ID}", "" + warp.getID());
						query = query.replace("{ISRANDOM}",
								"" + (warp.isRandom() ? 1 : 0));
						query = query.replace("{PLAYSOUND}", ""
								+ (warp.playSound ? 1 : 0));
						query = query.replace("{IGNOREPITCH}", ""
								+ (warp.ignorePitch ? 1 : 0));
						query = query.replace("{IGNOREYAW}", ""
								+ (warp.ignoreYaw ? 1 : 0));
						query = query.replace("{LOADCHUNK}", ""
								+ (warp.loadChunk ? 1 : 0));
						query = query.replace("{CMDSONCMD}", ""
								+ (warp.cmdsOnCmd ? 1 : 0));
						query = query.replace("{MSGONCMD}", ""
								+ (warp.msgOnCmd ? 1 : 0));
						query = query.replace("{CIRCLERADIUS}", ""
								+ (warp.circleRadius ? 1 : 0));
						query = query.replace("{BROADCASTMESSAGE}", ""
								+ (warp.broadcastMessage ? 1 : 0));
						query = query.replace("{MESSAGE}", warp.getMessage());
						query = query.replace("{COMMANDS}", warp.getCommands()
								.toString());
						query = query.replace("{MINRANGE}",
								"" + warp.getMinRadius());
						query = query.replace("{MAXRANGE}",
								"" + warp.getMaxRadius());
						query = query.replace("{EXCLUDEDBIOMES}", warp
								.getExcludedBiomes().toString());
						query = query.replace("{INCLUDEDBIOMES}", warp
								.getIncludedBiomes().toString());
						query = query.replace("{CUSTOMNODE}",
								"" + warp.getCustomNode());
						query = query.replace("{SEARCHSIZE}",
								"" + warp.getSearchSize());
						query = query.replace("{CACHESIZE}",
								"" + warp.getCacheSize());
						DateFormat dateFormat = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						Date date = new Date();
						query = query.replace("{CREATED}",
								"" + dateFormat.format(date));
						// Execute query
						db.query(query);
					}
				});
	}

	/**
	 * Get all warps from the database
	 */
	public static void getWarps() {
		String query = getQuery("WARPS_SELECT");
		query = query.replace("{PREFIX}", Configuration.prefix);
		ResultSet result = db.query(query);
		SendConsole.info("Loading warps from database ..."); // Log to
																// console
		// Get the result
		try {
			while (result.next()) {
				try {
					String name = result.getString("name");
					String uuid = result.getString("owner");
					Player owner = PlayerUtils.getPlayerByUUID(UUID
							.fromString(uuid));
					int id = result.getInt("id");
					float x = result.getFloat("x");
					float y = result.getFloat("y");
					float z = result.getFloat("z");
					float pitch = result.getFloat("pitch");
					float yaw = result.getFloat("yaw");
					World world = wp.getServer().getWorld(
							result.getString("world"));
					Location location = new Location(world, x, y, z, yaw, pitch);
					Warp warp = new Warp(name, owner, location, id);
					warp.saveWarpCache();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get all warp links from the database
	 */
	public static void getWarpLinks() {
		String query = getQuery("WARPLINKS_SELECT");
		query = query.replace("{PREFIX}", Configuration.prefix);
		ResultSet result = db.query(query);
		SendConsole.info("Loading warp links from database ..."); // Log
																	// to
																	// console
		// Get the result
		try {
			while (result.next()) {
				try {
					int warpid = result.getInt("warpid");
					String uuid = result.getString("owner");
					Player owner = PlayerUtils.getPlayerByUUID(UUID
							.fromString(uuid));
					int id = result.getInt("id");
					int x = result.getInt("x");
					int y = result.getInt("y");
					int z = result.getInt("z");
					World world = wp.getServer().getWorld(
							result.getString("world"));
					Location location = new Location(world, x, y, z);
					Warp warp = Warp.getWarp(warpid);
					if (warp != null) {
						Block block = world.getBlockAt(location);
						try {
							warp.linkWarpCache(block, id);
						} catch (Exception ex) {
							// Warp is an invalid block
							SendConsole.warning("Warplink '" + warp.getName()
									+ "' @ " + x + "," + y + "," + z
									+ " is missing!");
						}
					} else {
						// Warp does not exists anymore
						SendConsole.warning("Warplink @ " + x + "," + y + ","
								+ z + " has an invalid warp!");
						/* Debugging Information */
						if (Configuration.debug) {
							SendConsole
									.info("action: load warplinks from database");
							SendConsole
									.info("result: Warplink has invalid warp.");
							SendConsole.info("warpid: " + warpid);
						}
					}
				} catch (Exception ex) {
					// Warp can not be linked
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
				return "ERROR: Could not convert stream to string!";
			}
		} else {
			return "ERROR: Stream could not be made!";
		}
	}

	/**
	 * Convert an input stream to string
	 * 
	 * @param is
	 *            Input Stream
	 * @return String
	 * @throws IOException
	 */
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
