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

package be.maximvdw.warp.commands;

import java.util.LinkedList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.maximvdw.warp.Warp;
import be.maximvdw.warp.ui.SendUnknown;
import be.maximvdw.warp.utils.Permissions;
import be.maximvdw.warp.utils.Permissions.Permission;

/**
 * Warp Plugin
 * 
 * @author Maximvdw
 * @version 1.0.0
 */
public class WarpsCommand implements CommandExecutor {
	/**
	 * Show command usage
	 * 
	 * @param sender
	 *            Sender
	 */
	public static void showUsage(Object sender) {
		// Check if the sender has permissions
		if (Permissions.hasPermissions(Permission.HelpSetWarp, sender)) {
			SendUnknown.toSender("&b[&3Warp&b]&c /warps (<list>|<info>|<delwarp>|<link>)",
					sender); // Send command usage
		} else {
			// No permissions
			SendUnknown.toSender("&cYou do not have permissions!", sender);
		}
	}

	/**
	 * Show warp information
	 * 
	 * @param warp
	 *            Warp
	 */
	public static void showWarpInfo(Warp warp, Object sender) {
		SendUnknown.toSender(" " + warp.getName() + "", sender);
	}

	/**
	 * Show existing warps
	 * 
	 * @param sender
	 *            Sender
	 */
	public static void showWarps(Object sender) {
		LinkedList<Warp> warps = Warp.getWarps(); // Get a list
		if (Permissions.hasPermissions(Permission.List, sender)) {
			if (warps.size() != 0) {
				SendUnknown.toSender("&b[&3Warp&b]&c Available warps:\n",
						sender); // Send message
				int count = 0;
				boolean listAll = Permissions.hasPermissions(
						Permission.ListAll, sender);
				for (Warp warp : warps) {
					if (listAll) {
						// Show warp name
						showWarpInfo(warp,sender);
					} else if (Permissions.hasPermissions(warp, sender)) {
						// Show warp name
						showWarpInfo(warp,sender);
					}
				}
			} else {
				// No available warps
				SendUnknown
						.toSender(
								"&b[&3Warp&b]&c No warps found! Check your database settings!",
								sender);
			}
		} else {
			// No permissions
			SendUnknown.toSender("&cYou do not have permissions!", sender);
		}
	}

	/**
	 * Perform the /warp command
	 */
	public boolean onCommand(CommandSender sender, Command command, String cmd,
			String[] args) {
		Player player = null;

		// Check if it is a real player
		if (sender instanceof Player) {
			// Add playerID to player
			player = (Player) sender;
		} else {
			// Can only be used ingame

		}

		/* Check if there are enough arguments */
		if (args.length >= 1) {

		} else {
			// Show command usage
			showUsage(sender);
		}

		return false; // Return false
	}
}
