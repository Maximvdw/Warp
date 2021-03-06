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

package be.maximvdw.warp;

import java.util.LinkedList;
import java.util.Random;
import java.util.TreeMap;

import org.bukkit.Bukkit;
/* Bukkit Imports */
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import be.maximvdw.warp.config.Configuration;
import be.maximvdw.warp.database.WarpDatabase;
import be.maximvdw.warp.hooks.WarpHook;
import be.maximvdw.warp.ui.SendConsole;
import be.maximvdw.warp.ui.SendGame;
import be.maximvdw.warp.utils.Permissions;

/**
 * Warp Plugin
 * 
 * @author Maximvdw
 * @version 1.0.0
 */
public class Warp {
	String name = ""; // A unique name for the warp
	Player owner = null; // The owner of the warp
	Location warpLocation = null; // Warp location
	String customNode = ""; // Custom permission node
	public boolean ignorePitch = false; // Ignore pitch on teleport
	public boolean ignoreYaw = false; // Ignore yaw on teleport
	public boolean loadChunk = true; // Load chunk on teleport
	public boolean playSound = true; // Play a sound on teleport
	public boolean isRandom = false; // Random teleport
	public boolean broadcastMessage = false; // Broadcast message
	public boolean cmdsOnCmd = true; // Perform commands when using /warp
	public boolean msgOnCmd = true; // Show message when using /warp
	public boolean circleRadius = false; // Min-Max radius is a circle instead of
									// square
	int minRange = -1; // Minimum radius
	int maxRange = -1; // Maximum radius
	int searchSize = 16; // Search size on random teleport
	int cacheSize = 10; // Cache size
	int dbID = 0; // Database Identifier
	static int lastID = 0; // Last id
	LinkedList<Biome> excludedBiomes = new LinkedList<Biome>(); // Excluded
																// biomes
	LinkedList<Biome> includedBiomes = new LinkedList<Biome>(); // Included
																// biomes
	LinkedList<Block> excludedBlocks = new LinkedList<Block>(); // Excluded
																// blocks
	LinkedList<Location> cachedLocations = new LinkedList<Location>(); // Cached
																		// random
																		// locations
	LinkedList<WarpHook> warpHooks = new LinkedList<WarpHook>(); // Warp plugin
																	// hooks
	LinkedList<String> commands = new LinkedList<String>(); // Commands
	Object commandExecutor = null; // Default command executor
	String message = ""; // Message to show on teleport
	Sound sound = Sound.ENDERMAN_TELEPORT; // Sound to play on teleport

	/**
	 * Create a new warp
	 */
	public Warp() {
	}

	/**
	 * Create a new warp
	 * 
	 * @param name
	 *            Warp name
	 */
	public Warp(String name) {
		this.name = name; // Save arguments
	}

	/**
	 * Create a new warp
	 * 
	 * @param name
	 *            Warp name
	 * @param owner
	 *            Warp owner
	 */
	public Warp(String name, Player owner) {
		this.name = name; // Save arguments
		this.owner = owner;
		this.warpLocation = owner.getLocation(); // Get location
	}

	/**
	 * Create a new warp
	 * 
	 * @param name
	 *            Warp name
	 * @param owner
	 *            Warp owner
	 * @param location
	 *            Warp Location
	 * @param id
	 *            Database ID
	 */
	public Warp(String name, Player owner, Location location, int id) {
		this.name = name; // Save arguments
		this.owner = owner;
		this.warpLocation = location;
		this.dbID = id;
	}

	/**
	 * Get the warp database ID
	 * 
	 * @return Database ID
	 */
	public int getID() {
		return dbID;
	}

	/**
	 * Replace variables in a message or command
	 * 
	 * @param string
	 *            String to replace variables
	 * @param player
	 *            Player Teleport
	 * @return Filtered string
	 */
	private String replaceVariables(String string, Player player) {
		String output = string;
		output = output.replace("<player>", player.getName());
		output = output.replace("<playerdisplay>", player.getDisplayName());
		return output; // Return output
	}

	/**
	 * Send the teleport message
	 * 
	 * @param player
	 *            Player that used the teleport
	 */
	public void sendMessage(Player player) {
		// Filter message
		message = replaceVariables(message, player);
		if (broadcastMessage) {
			SendGame.toServer(message); // Broadcast message
		} else {
			SendGame.toPlayer(message, player); // Private message
		}
	}

	/**
	 * Set the message that is display on teleport
	 * 
	 * @param message
	 *            Message to display
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Get the message that is displayed on teleport
	 * 
	 * @return Message
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * Get the custom permission node
	 * 
	 * @return Permission node as string
	 */
	public String getCustomNode() {
		return this.customNode;
	}

	/**
	 * Set the custom permission node
	 * 
	 * @param permission
	 *            Node as string
	 */
	public void setCustomNode(String permission) {
		this.customNode = permission;
	}

	/**
	 * Get warp name
	 * 
	 * @return Warp name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Check if a player has permission to use this warp
	 * 
	 * @param player
	 *            Player
	 * @return Has permission
	 */
	public boolean hasPermission(Player player) {
		return Permissions.hasPermissions(this, player);
	}

	/**
	 * Set the minimum and maximum radius of the random warp
	 * 
	 * @param min
	 *            Minimum radius
	 * @param max
	 *            Maximum radius
	 */
	public void setRadius(int min, int max) {
		this.minRange = min;
		this.maxRange = max;
		this.isRandom = true;
	}

	/**
	 * Set the radius of the random warp
	 * 
	 * @param max
	 *            Maximum radius
	 */
	public void setRadius(int max) {
		this.minRange = 0;
		this.maxRange = max;
		this.isRandom = true;
	}

	/**
	 * Get the minimum radius of the random warp
	 * 
	 * @return Minimum radius
	 */
	public int getMinRadius() {
		return this.minRange;
	}

	/**
	 * Get the maximum radius of the random warp
	 * 
	 * @return Maximum radius
	 */
	public int getMaxRadius() {
		return this.maxRange;
	}

	/**
	 * Set the minimum radius of the random warp
	 * 
	 * @param min
	 *            Minimum radius
	 */
	public void setMinRadius(int min) {
		this.minRange = min;
		this.isRandom = true;
	}

	/**
	 * Set the maximum radius of the random warp
	 * 
	 * @param max
	 *            Maximum radius
	 */
	public void setMaxRadius(int max) {
		this.maxRange = max;
		this.isRandom = true;
	}

	/**
	 * Set the warp location
	 * 
	 * @param location
	 *            Location of the warp
	 */
	public void setLocation(Location location) {
		this.warpLocation = location;
	}

	/**
	 * Get the warp location
	 * 
	 * @return Location of the warp
	 */
	public Location getLocation() {
		return this.warpLocation;
	}

	/**
	 * Set the warp location
	 * 
	 * @param player
	 *            Player to set location
	 */
	public void setLocation(Player player) {
		this.warpLocation = player.getLocation();
	}

	/**
	 * Check if the warp is random
	 * 
	 * @return random (true) or false
	 */
	public boolean isRandom() {
		return this.isRandom;
	}

	/**
	 * Set the sound on teleport
	 * 
	 * @param sound
	 *            Teleportation sound
	 */
	public void setSound(Sound sound) {
		this.sound = sound;
	}

	/**
	 * Get the current sound on teleport
	 * 
	 * @return Teleportation sound
	 */
	public Sound getSound() {
		return this.sound;
	}

	/**
	 * Get excluded biomes in random teleport
	 * 
	 * @return Excluded biomes
	 */
	public LinkedList<Biome> getExcludedBiomes() {
		return this.excludedBiomes;
	}

	/**
	 * Get included biomes in random teleport
	 * 
	 * @return Included biomes
	 */
	public LinkedList<Biome> getIncludedBiomes() {
		return this.includedBiomes;
	}

	/**
	 * Set the owner of the warp
	 * 
	 * @param player
	 *            Warp owner
	 */
	public void setOwner(Player player) {
		this.owner = player;
	}

	/**
	 * Get the owner of the warp
	 * 
	 * @return Warp owner
	 */
	public Player getOwner() {
		return this.owner;
	}

	/**
	 * Warp a player to the warp location
	 * 
	 * @param player
	 *            Player to warp
	 */
	public void warpPlayer(final Player player, boolean command) {
		// Teleport the player (async)
		Bukkit.getScheduler().runTaskAsynchronously(WarpPlugin.getInstance(),
				new Runnable() {
					public void run() {
						if (isRandom) {
							randomTeleport(player); // Random Teleport
						} else {
							teleport(player); // Teleport
						}
					}
				});
		// Show message
		if ((!command || (command && this.cmdsOnCmd)) && message != "") {
			sendMessage(player); // Send message
		}
	}

	/**
	 * Warp the owner to the warp location
	 */
	public void warpOwner(boolean command) {
		warpPlayer(owner, command);
	}

	/**
	 * Teleport a player to the warp location
	 * 
	 * @param player
	 *            Player to teleport
	 */
	public void teleport(Player player) {
		// Check if the yaw or pitch need to be ignored
		warpLocation.setYaw(ignoreYaw ? player.getLocation().getYaw()
				: warpLocation.getYaw());
		warpLocation.setPitch(ignorePitch ? player.getLocation().getPitch()
				: warpLocation.getPitch());

		// Ensure that the Chunk is loaded
		Chunk chunk = warpLocation.getChunk();
		if (!chunk.isLoaded() && loadChunk) {
			chunk.load();
		}

		// Teleport the player
		player.teleport(warpLocation);

		// Play a sound if enabled
		if (playSound) {
			player.playSound(warpLocation, sound, 0.8F, 0.075F);
		}
	}

	/**
	 * Randomly teleport a player
	 * 
	 * @param player
	 *            Player to teleport
	 */
	public void randomTeleport(Player player) {
		// Check cache
		Location randomLocation = null;
		if (cachedLocations.size() != 0) {
			randomLocation = cachedLocations.get(0);
			cachedLocations.remove(0); // Remove location
		} else {
			// Generate a random location
			randomLocation = getRandomLocation(false);
		}

		if (randomLocation != null) {
			// Check if the yaw or pitch need to be ignored
			randomLocation.setYaw(ignoreYaw ? player.getLocation().getYaw()
					: warpLocation.getYaw());
			randomLocation.setPitch(ignorePitch ? player.getLocation()
					.getPitch() : warpLocation.getPitch());

			// Ensure that the Chunk is loaded
			Chunk chunk = randomLocation.getChunk();
			if (!chunk.isLoaded() && loadChunk) {
				chunk.load();
			}

			// Teleport the player
			player.teleport(randomLocation);

			// Play a sound if enabled
			if (playSound) {
				player.playSound(randomLocation, sound, 0.8F, 0.075F);
			}
		} else {
			// An error occured while creating the warp (cache empty?)
		}
	}

	/**
	 * Get a random location with the warp data
	 * 
	 * @return Valid location to teleport to
	 */
	private Location getRandomLocation(boolean background) {
		// Get a random location in a square
		Location location = null;
		int flood = 0;
		try {
			final Biome[] biome = { null }; // Biome
			do {
				location = getRandomSquare(this.minRange, this.maxRange); // Random
				final Location loc = location;
				int waitfortask = Bukkit.getScheduler()
						.scheduleSyncDelayedTask(WarpPlugin.getInstance(),
								new Runnable() {
									public void run() {
										biome[0] = loc.getBlock().getBiome();
									}
								});
				while (Bukkit.getScheduler().isCurrentlyRunning(waitfortask)
						|| Bukkit.getScheduler().isQueued(waitfortask)) {
					try {
						if (background)
							Thread.sleep(500);
					} catch (InterruptedException e) {
					}
				} // Square

				location = loc; // Set random location

				if (this.includedBiomes.size() != 0) {
					for (Biome b : this.includedBiomes) {
						if (biome[0] != b) {
							location = null;
						} else {
							break;
						}
					}
				} else if (this.excludedBiomes.size() != 0) {
					for (Biome b : this.excludedBiomes) {
						if (biome[0] == b) {
							location = null;
						}
					}
				} else if (this.warpHooks.size() != 0) {
					for (WarpHook hook : warpHooks) {
						if (!hook.allowRandomWarp(location)) {
							location = null;
						}
					}
				}
				flood++;
				if (background)
					Thread.sleep(500);
			} while (location == null && flood < 2000);
		} catch (Exception ex) {
			// Error
			SendConsole.severe("Warp '" + this.name
					+ "' was unable to generate a random warp!");
			ex.printStackTrace();
			return null;
		}

		return location; // Return the result
	}

	/**
	 * Get random location in a min-max square
	 * 
	 * @param min
	 *            Minimum radius
	 * @param max
	 *            Maximum radius
	 * @return Location with X , Z
	 */
	private Location getRandomSquare(int min, int max) {
		try {
			Random rndGen = new Random();
			Location location = null;
			int r = rndGen.nextInt(max - min);
			int x = rndGen.nextInt(r) + min;
			int z = rndGen.nextInt(r) + min;
			if (rndGen.nextBoolean())
				x *= -1;
			if (rndGen.nextBoolean())
				z *= -1;
			x += warpLocation.getX();
			z += warpLocation.getZ();
			location = new Location(warpLocation.getWorld(), x, 0, z);
			return location;
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Get a specific warp
	 * 
	 * @param name
	 *            Warp name
	 * @return Warp
	 */
	public static Warp getWarp(String name) {
		WarpPlugin wp = WarpPlugin.getInstance(); // Get instance
		if (wp.warps.containsKey(name.toLowerCase())) {
			/* Debugging Information */
			if (Configuration.debug) {
				SendConsole.info("action: getWarp");
				SendConsole.info("result: Warp '" + name + "' found");
				SendConsole.info("warpCacheSize: " + wp.warps.size());
			}

			return wp.warps.get(name.toLowerCase()); // Return warp
		}

		/* Debugging Information */
		if (Configuration.debug) {
			SendConsole.info("action: getWarp");
			SendConsole.info("result: warp '" + name
					+ "' does not exist in name cache!");
			SendConsole.info("warpCacheSize: " + wp.warps.size());
		}

		return null; // No warp found
	}

	/**
	 * Get a list of all warps
	 * 
	 * @return Warp list
	 */
	public static TreeMap<String, Warp> getWarps() {
		WarpPlugin wp = WarpPlugin.getInstance(); // Get instance
		return wp.warps; // Return list of all warps
	}

	/**
	 * Save the current warp
	 * 
	 * @throws Exception
	 */
	public void saveWarp() throws Exception {
		WarpPlugin wp = WarpPlugin.getInstance(); // Get instance
		if (!(wp.warps.containsKey(this.name.toLowerCase()))) {
			saveWarpCache();
			// Save to database
			WarpDatabase.addWarp(this);
		} else {
			// Error
			throw new Exception("The given warp '" + name + "' already exist!");
		}
	}

	/**
	 * Save the current warp in cache
	 * 
	 * @throws Exception
	 */
	public void saveWarpCache() throws Exception {
		WarpPlugin wp = WarpPlugin.getInstance(); // Get instance
		if (!(wp.warps.containsKey(this.name.toLowerCase()))) {
			wp.warps.put(this.name.toLowerCase(), this);
		} else {
			// Error
			throw new Exception("The given warp '" + name + "' already exist!");
		}
	}

	/**
	 * Delete the current warp
	 * 
	 * @throws Exception
	 */
	public void deleteWarp() throws Exception {
		WarpPlugin wp = WarpPlugin.getInstance(); // Get instance
		if (!(wp.warps.containsKey(this.name))) {
			deleteWarpCache();
			// Delete from database
			WarpDatabase.deleteWarp(this);
		} else {
			// Warp does not exist in name cache

			/* Debugging Information */
			if (Configuration.debug) {
				SendConsole.info("action: deleteWarp");
				SendConsole.info("result: warp '" + this.name
						+ "' does not exist in name cache!");
				SendConsole.info("warpCacheSize: " + wp.warps.size());
			}

			// Error
			throw new Exception("The given warp '" + name + "' does not exist!");
		}
	}

	/**
	 * Delete the current warp from cache
	 * 
	 * @throws Exception
	 */
	public void deleteWarpCache() throws Exception {
		WarpPlugin wp = WarpPlugin.getInstance(); // Get instance
		if (!(wp.warps.containsKey(this.name))) {
			wp.warps.remove(this.name.toLowerCase()); // Delete warp
		} else {
			// Warp does not exist in name cache

			/* Debugging Information */
			if (Configuration.debug) {
				SendConsole.info("action: deleteWarp");
				SendConsole.info("result: warp '" + this.name
						+ "' does not exist in name cache!");
				SendConsole.info("warpCacheSize: " + wp.warps.size());
			}

			// Error
			throw new Exception("The given warp '" + name + "' does not exist!");
		}
	}

	/**
	 * Link a warp to a button
	 * 
	 * @param block
	 *            Block
	 * @throws Exception
	 */
	public void linkWarp(Block block) throws Exception {
		WarpPlugin wp = WarpPlugin.getInstance(); // Get instance
		if (!wp.warpLinks.containsKey(block.getLocation().toString())) {
			// Create a new link
			linkWarpCache(block);
			// Add link to database
			WarpLink link = new WarpLink(this, block);
			WarpDatabase.addWarpLink(link);
		}
	}

	/**
	 * Link a warp to a button
	 * 
	 * @param block
	 *            Block
	 * @param id
	 *            Database id
	 * @throws Exception
	 */
	public void linkWarpCache(Block block, int id) throws Exception {
		WarpPlugin wp = WarpPlugin.getInstance(); // Get instance
		if (!wp.warpLinks.containsKey(block.getLocation().toString())) {
			// Create a new link
			WarpLink link = new WarpLink(this, block);
			link.setID(id);
			wp.warpLinks.put(block.getLocation().toString(), link); // Add link
			
			/* Debugging Information */
			if (Configuration.debug) {
				SendConsole.info("action: linkWarpCache");
				SendConsole.info("result: warplink has been made");
				SendConsole.info("warplink size: " + wp.warpLinks.size());
			}
		}
	}

	/**
	 * Link a warp to a button
	 * 
	 * @param block
	 *            Block
	 * @throws Exception
	 */
	public void linkWarpCache(Block block) throws Exception {
		linkWarpCache(block, -1);
	}

	/**
	 * Unlink a warp
	 * 
	 * @param link
	 *            Warp Link
	 */
	public void unlinkWarp(WarpLink link) {
		WarpPlugin wp = WarpPlugin.getInstance(); // Get instance
		if (wp.warpLinks.containsKey(link.getBlock().getLocation().toString())) {
			// Unlink the warp
			unlinkWarpCache(link);
			WarpDatabase.deleteWarpLink(link);
		}
	}

	/**
	 * Unlink a warp in cache
	 * 
	 * @param link
	 *            Warp Link
	 */
	public void unlinkWarpCache(WarpLink link) {
		WarpPlugin wp = WarpPlugin.getInstance(); // Get instance
		if (wp.warpLinks.containsKey(link.getBlock().getLocation().toString())) {
			// Unlink the warp
			wp.warpLinks.remove(link.getBlock().getLocation().toString()); // Remove
																			// link
		}
	}

	/**
	 * Get warp by id
	 * 
	 * @param id
	 *            Database id
	 * @return Warp
	 */
	public static Warp getWarp(int id) {
		WarpPlugin wp = WarpPlugin.getInstance(); // Get instance
		for (Warp warp : wp.warps.values()) {
			if (warp.getID() == id) {
				return warp;
			}
		}
		return null; // No warp found
	}
	
	/**
	 * Get a list of all the commands
	 * 
	 * @return LinkedList String
	 */
	public LinkedList<String> getCommands(){
		return this.commands;
	}
	
	/**
	 * Get biome search size
	 * 
	 * @return Biome search size
	 */
	public int getSearchSize(){
		return searchSize;
	}
	
	/**
	 * Set the biome search size
	 * 
	 * @param size Biome search size
	 */
	public void setSearchSize(int size){
		this.searchSize = size;
	}
	
	/**
	 * Get biome search size
	 * 
	 * @return cache size
	 */
	public int getCacheSize(){
		return this.cacheSize;
	}
	
	/**
	 * Set the cache size
	 * 
	 * @param size cache size
	 */
	public void setCacheSize(int size){
		this.cacheSize = size;
	}
}
