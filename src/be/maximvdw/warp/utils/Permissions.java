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

package be.maximvdw.warp.utils;

import org.bukkit.entity.Player;

import be.maximvdw.warp.Warp;

/**
 * Warp Plugin
 * 
 * @author Maximvdw
 * @version 1.0.0
 */

public class Permissions {
	static boolean allowOpPermissions = true; // Allow OP's to have all permissions
	static boolean denyOpPermissions = false; // Deny OP's to use any permission
	static boolean debugMode = false; // Debug mode
	
	public static enum Permission{
		List,
		ListAll,
		Debug,
		Update,
		Reset,
		HelpWarp,
		HelpSetWarp
	}
	
	/**
	 * Check if a sender has warp permissions
	 * 
	 * @param sender Sender of the command
	 * @return Permissions true or false
	 */
	public static boolean hasPermissions(Warp warp, Object sender){
		// Check if player
		Player player = null;
		if (!(sender instanceof Player)){
			// Console has no permissions
			return false;
		}
		player = (Player)sender; // Cast player
		
		// Check operator permissions
		if (player.isOp() && allowOpPermissions){
			// Operator has permissions
			return true;
		}else if(player.isOp() && denyOpPermissions){
			// Operator has no permissions
			return false;
		}
		
		// Check regular permissions
		String name = warp.getName();
		if (player.hasPermission("warp.teleport.*")){
			// Permission for all warps
			return true;
		}else if (player.hasPermission("warp.teleport." + name)){
			// Permission for this warp
			return true;
		}else if (warp.getCustomNode() != "" && player.hasPermission(warp.getCustomNode())){
			// Permission for this warp
			return true;
		}else{
			// No permissions
			return false;
		}
	}
	
	/**
	 * Check if a sender has command permissions
	 * 
	 * @param permission Permission
	 * @param sender Sender of the command
	 * @return Permissions true or false
	 */
	public static boolean hasPermissions(Permission permission, Object sender){
		// Check if player
		Player player = null;
		if (!(sender instanceof Player)){
			// Console has permissions
			return true;
		}
		player = (Player)sender; // Cast player
		
		// Check operator permissions
		if (player.isOp() && allowOpPermissions){
			// Operator has permissions
			return true;
		}else if(player.isOp() && denyOpPermissions){
			// Operator has no permissions
			return false;
		}
		
		// Check regular permissions
		switch (permission){
		case List:
			return player.hasPermission("warp.command.list");
		case ListAll:
			return player.hasPermission("warp.command.listall");
		case Debug:
			return player.hasPermission("warp.admin.debug");
		case Update:
			return player.hasPermission("warp.admin.update");
		case Reset:
			return player.hasPermission("warp.admin.reset");
		case HelpWarp:
			return player.hasPermission("warp.command.teleport.help");
		case HelpSetWarp:
			return player.hasPermission("warp.command.setwarp.help");
		default:
			return true;
		}
	}
}
