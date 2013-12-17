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

package com.maximvdw.warp.config;

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
}
