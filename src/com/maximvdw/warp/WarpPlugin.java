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

package com.maximvdw.warp;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.maximvdw.warp.config.Configuration;
import com.maximvdw.warp.database.MySQL;
import com.maximvdw.warp.ui.SendConsole;
import com.maximvdw.warp.updater.Updater;
import com.maximvdw.warp.updater.Updater.UpdateResult;

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

	@Override
	public void onEnable() {
		// Enable the Warp Plugin
		plugin = this; // Save plugin instance
		pm = Bukkit.getPluginManager();
		
		// Check for updates
		Update(false, null); // Rely on config (no force)

		// Connect to database
		MySQL db = new MySQL(this.getLogger(), Configuration.prefix,
				Configuration.hostname, Configuration.portnmbr,
				Configuration.database, Configuration.username,
				Configuration.password);
		// Check if warps table exists
		if (!db.checkTable("warps")) {
			SendConsole.info("Creating table 'warps'...");
			String query = "CREATE TABLE warps (id INT AUTO_INCREMENT PRIMARY_KEY, name VARCHAR(255));";
			db.createTable(query);
		}
	}

	@Override
	public void onDisable() {
		// Disable the Warp Plugin

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
}
