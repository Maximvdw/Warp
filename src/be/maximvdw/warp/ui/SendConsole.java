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

package be.maximvdw.warp.ui;

import org.bukkit.Bukkit;

/**
 * Warp Plugin
 * 
 * @author Maximvdw
 * @version 1.0.0
 */

public class SendConsole {
	static boolean enableLogging = false; // Enable logging
	static String prefix = "[Warp] "; // Message prefix
	
	/**
	 * Send an INFO message to the console
	 * 
	 * @param message Info message
	 */
	public static void info(String message){
		Bukkit.getLogger().info(prefix + message);
	}
	
	/**
	 * Send an WARNING message to the console
	 * 
	 * @param message Warning message
	 */
	public static void warning(String message){
		Bukkit.getLogger().warning(prefix + message);
	}
	
	/**
	 * Send an SEVERE message to the console
	 * 
	 * @param message Severe message
	 */
	public static void severe(String message){
		Bukkit.getLogger().severe(prefix + message);
	}
}
