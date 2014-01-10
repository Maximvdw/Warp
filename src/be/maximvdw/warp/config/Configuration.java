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

package be.maximvdw.warp.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import be.maximvdw.warp.WarpPlugin;

/**
 * Warp Plugin
 * 
 * @author Maximvdw
 * @version 1.0.0
 */

public class Configuration {
	public static boolean checkUpdate = true; // Check for updates
	public static boolean autoUpdate = true; // Auto update
	public static boolean allowMetrics = true; // Allow metrics
	public static String hostname = "localhost"; // Database Hostname
	public static String portnmbr = "3306"; // Database Portnumber
	public static String database = "minecraft"; // Database name
	public static String username = "minecraft"; // Database username
	public static String password = ""; // Database password
	public static String prefix = ""; // Database Prefix
	public static int cacheWarpSize = 10000; // Max warps loaded in cache
	public static boolean debug = true; // Debug mode
	
	private static YamlConfiguration pluginConfig;
	private static File configFile;
	private static boolean loaded = false;

	/**
	 * Read Configuration
	 */
	public static void readConfig(){
		checkUpdate = getConfig().getBoolean("checkUpdate");
		autoUpdate = getConfig().getBoolean("autoUpdate");
		allowMetrics = getConfig().getBoolean("metrics");
	}
	
	/**
	 * Gets the configuration file.
	 * 
	 * @return the myConfig
	 */
	public static YamlConfiguration getConfig() {
		if (!loaded) {
			loadConfig();
		}
		return pluginConfig;
	}

	/**
	 * Gets the configuration file.
	 * 
	 * @return Configuration file
	 */
	public static File getConfigFile() {
		return configFile;
	}
	
	/**
	 * Loads the configuration file from the .jar.
	 */
	public static void loadConfig() {
		configFile = new File(Bukkit.getServer().getPluginManager()
				.getPlugin("Warp").getDataFolder(), "config.yml");
		if (configFile.exists()) {
			pluginConfig = new YamlConfiguration();
			try {
				pluginConfig.load(configFile);
			} catch (FileNotFoundException ex) {
				// TODO: Log exception
			} catch (IOException ex) {
				// TODO: Log exception
			} catch (InvalidConfigurationException ex) {
				// TODO: Log exception
			}
			loaded = true;
		} else {
			try {
				Bukkit.getServer().getPluginManager().getPlugin("Warp")
						.getDataFolder().mkdir();
				InputStream jarURL = WarpPlugin.class
						.getResourceAsStream("/config.yml");
				copyFile(jarURL, configFile);
				pluginConfig = new YamlConfiguration();
				pluginConfig.load(configFile);
				loaded = true;
				// TODO: Log that config has been loaded
			} catch (Exception e) {
				// TODO: Log exception
			}
		}
	}

	/**
	 * Copies a file to a new location.
	 * 
	 * @param in
	 *            InputStream
	 * @param out
	 *            File
	 * 
	 * @throws Exception
	 */
	static private void copyFile(InputStream in, File out) throws Exception {
		InputStream fis = in;
		FileOutputStream fos = new FileOutputStream(out);
		try {
			byte[] buf = new byte[1024];
			int i = 0;
			while ((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (fis != null) {
				fis.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
	}
}
