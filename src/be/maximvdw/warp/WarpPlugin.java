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

import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import be.maximvdw.warp.commands.SetRndWarpCommand;
import be.maximvdw.warp.commands.SetWarpCommand;
import be.maximvdw.warp.commands.WarpCommand;
import be.maximvdw.warp.commands.WarpsCommand;
import be.maximvdw.warp.config.Configuration;
import be.maximvdw.warp.database.WarpDatabase;
import be.maximvdw.warp.hooks.TownyHook;
import be.maximvdw.warp.hooks.WarpHook;
import be.maximvdw.warp.listeners.PlayerListener;
import be.maximvdw.warp.ui.SendConsole;
import be.maximvdw.warp.updater.Updater;
import be.maximvdw.warp.updater.Updater.UpdateResult;

/**
 * Warp Plugin
 * 
 * @author Maximvdw
 * @version 1.0.0
 */
public class WarpPlugin extends JavaPlugin {
	int pluginID = 0; // dev.bukkit.org Plugin ID
	static WarpPlugin plugin = null; // Plugin instance
	PluginManager pm = null; // Bukkit Plugin manager
	TreeMap<String, Warp> warps = new TreeMap<String, Warp>(); // Loaded warps
	TreeMap<Location, WarpLink> warpLinks = new TreeMap<Location, WarpLink>(); // Warp
																				// links

	@Override
	public void onEnable() {
		// Enable the Warp Plugin
		plugin = this; // Save plugin instance
		pm = Bukkit.getPluginManager();

		// Load Configuration
		try {
			SendConsole.info("Loading configuration...");
			Configuration.loadConfig(); // Load configuration from file
		} catch (Exception ex) {

		}

		// Check for updates
		Update(false, null); // Rely on config (no force)

		// Connect to database
		try {
			SendConsole.info("Loading database...");
			WarpDatabase.initDatabase();
			
			WarpDatabase.getWarps();
		} catch (Exception ex) {
			// Unknown error
			ex.printStackTrace();
		}

		// Register commands
		try {
			SendConsole.info("Registrating commands...");
			getCommand("warp").setExecutor(new WarpCommand());
			getCommand("setwarp").setExecutor(new SetWarpCommand());
			getCommand("warps").setExecutor(new WarpsCommand());
			getCommand("setrndwarp").setExecutor(new SetRndWarpCommand());
		} catch (Exception ex) {
			// Unable to register all commands
		}

		// Register listeners
		try {
			SendConsole.info("Registrating listeners...");
			pm.registerEvents(new PlayerListener(this), this);
		} catch (Exception ex) {

		}

		// Load hooks
		SendConsole.info("Registrating plugin hooks...");
		try {
			Plugin plugin = pm.getPlugin("Towny");
			if (plugin.isEnabled()) {
				new TownyHook(plugin); // Init hook
				SendConsole.info("Hooked into Towny!");
			}
		} catch (Exception ex) {
			// Error while hooking into towny
		}
		try {
			Plugin plugin = pm.getPlugin("WorldGuard");
			if (plugin.isEnabled()) {
				new TownyHook(plugin); // Init hook
				SendConsole.info("Hooked into Towny!");
			}
		} catch (Exception ex) {
			// Error while hooking into towny
		}
	}

	@Override
	public void onDisable() {
		// Disable the Warp Plugin

	}

	/**
	 * Get available warps
	 * 
	 * @return Warps
	 */
	public TreeMap<String, Warp> getWarps() {
		return this.warps;
	}

	/**
	 * Get an instance of the plugin
	 * 
	 * @return WarpPlugin
	 */
	public static WarpPlugin getInstance() {
		return plugin;
	}

	/**
	 * Update the Warp Plugin
	 * 
	 * @param force
	 *            Force a plugin update if available (override config)
	 * @param sender
	 *            Sender of the update action (command only)
	 */
	public void Update(boolean force, Object sender) {
		// Check if update checking is enabled
		if (Configuration.checkUpdate || force) {
			try {
				Updater updater = new Updater(this, pluginID, this.getFile(),
						Updater.UpdateType.NO_DOWNLOAD, false);
				if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE) {
					// An update is available, check if updating is enabled
					SendConsole.info("An update for Warp is available! ["
							+ updater.getLatestName() + "]");
					if (Configuration.autoUpdate || force) {
						// Download latest update
						SendConsole.info("Updating plugin ...");
						updater = new Updater(this, pluginID, this.getFile(),
								Updater.UpdateType.DEFAULT, false);
						if (updater.getResult() == UpdateResult.SUCCESS) {
							// Success reload plugin
							SendConsole
									.info("Update for Warp downloaded! Restart the server to apply the update!");
						} else {
							// Failed
							SendConsole.warning("Unable to update the plugin!");
						}
					}
				} else if (updater.getResult() == UpdateResult.DISABLED) {
					// Updater disabled
					SendConsole
							.warning("Updating is globally disabled! Unable to check for updates!");
				} else {
					// Failed
					SendConsole.warning("Unable to update the plugin!");
				}
			} catch (Exception ex) {
				// Error
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Add a new warp
	 * 
	 * @param warp
	 *            Warp to add
	 * @throws Exception
	 */
	public void addWarp(Warp warp) throws Exception {
		warp.saveWarp(); // Save warp
	}

	/**
	 * Delete the current warp
	 * 
	 * @param warp
	 *            Warp to delete
	 * @throws Exception
	 */
	public void deleteWarp(Warp warp) throws Exception {
		warp.deleteWarp();
	}

	/**
	 * Get a specific warp
	 * 
	 * @param name
	 *            Warp name
	 * @return Warp
	 */
	public Warp getWarp(String name) {
		return Warp.getWarp(name); // Get a specific warp
	}

	/**
	 * Register a plugin hook
	 * 
	 * @param name
	 *            Plugin name
	 * @param plugin
	 *            Plugin instance
	 * @param hook
	 *            WarpHook
	 */
	public void registerPlugin(String name, Plugin plugin, WarpHook hook) {

	}

	/**
	 * Get a Warp Link
	 * 
	 * @param block
	 *            Block
	 * @return WarpLink
	 */
	public WarpLink getLink(Block block) {
		return warpLinks.get(block);
	}
}
