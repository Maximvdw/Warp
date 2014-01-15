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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.maximvdw.warp.Warp;
import be.maximvdw.warp.WarpPlugin;
import be.maximvdw.warp.ui.SendUnknown;
import be.maximvdw.warp.utils.Permissions;
import be.maximvdw.warp.utils.Permissions.Permission;
import be.maximvdw.warp.utils.tellraw.*;

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
			SendUnknown.toSender(
					"&b[&3Warp&b]&c /warps (<list>|<info>|<del>|<link>)",
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
		JSONChatMessage messageWarp = new JSONChatMessage();
		JSONChatExtra extraWarp = new JSONChatExtra(String.format("%-10s",
				warp.getName()), JSONChatColor.GOLD,
				new ArrayList<JSONChatFormat>());
		extraWarp.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT,
				"Click here to warp to '" + warp.getName() + "'");
		extraWarp.setClickEvent(JSONChatClickEventType.RUN_COMMAND, "/warp "
				+ warp.getName());

		messageWarp.addExtra(extraWarp);
		messageWarp.addText(
				" - " + String.format("%-15s", warp.getOwner().getName())
						+ " @ ", JSONChatColor.YELLOW,
				new ArrayList<JSONChatFormat>());
		messageWarp.sendToPlayer((Player) sender);
	}

	/**
	 * Show existing warps
	 * 
	 * @param sender
	 *            Sender
	 */
	public static void showWarps(Object sender, int page) {
		TreeMap<String, Warp> warps = Warp.getWarps(); // Get a list
		if (Permissions.hasPermissions(Permission.List, sender)) {
			if (warps.size() != 0) {
				int pages = warps.size() / 9;
				if (warps.size() % 9 != 0) {
					pages += 1;
				}

				SendUnknown.toSender(
						"&b[&3Warp&b]&c Available warps: &b[Page &3" + page
								+ "&b of &3" + pages + "&b]\n", sender); // Send
																			// message

				boolean listAll = Permissions.hasPermissions(
						Permission.ListAll, sender);
				for (int i = 0; i < 9; i++) {
					Warp warp = (Warp)warps.values().toArray()[i + ((page-1)*9)];
					if (listAll) {
						// Show warp name
						showWarpInfo(warp, sender);
					} else if (Permissions.hasPermissions(warp, sender)) {
						// Show warp name
						showWarpInfo(warp, sender);
					}
					if (warps.lastEntry().getValue() == warp){
						break;
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
		WarpPlugin wp = WarpPlugin.getInstance();

		// Check if it is a real player
		if (sender instanceof Player) {
			// Add playerID to player
			player = (Player) sender;
		} else {
			// Can only be used ingame

		}

		/* Check if there are enough arguments */
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("list")) { // List
				if (args.length >= 2){
					int page = Integer.parseInt(args[1]);
					showWarps(sender,page);
				}else{
					showWarps(sender,1);	
				}
			} else if (args[0].equalsIgnoreCase("del")) { // Delete
				// Check if name is defined
				if (args.length >= 2) {
					String name = args[1];
					Warp warp = wp.getWarp(name); // Get the warp
					// Check if the warp exists
					if (warp != null) {
						try {
							warp.deleteWarp(); // Delete warp
						} catch (Exception e) {
							// Error while deleting
						}
						SendUnknown.toSender(
								"&b[&3Warp&b]&e Warp '" + warp.getName()
										+ "' has been deleted!", sender);
					} else {
						// Warp not found
						SendUnknown.toSender("&b[&3Warp&b]&c Warp '" + name
								+ "' does not exist! &4/warps list", sender);
					}
				} else {
					// No name defined, show help

				}
			} else if (args[0].equalsIgnoreCase("link")) { // Link
				// Check if name is defined
				if (args.length >= 2) {
					String name = args[1];
					Warp warp = wp.getWarp(name); // Get the warp
					// Check if the warp exists
					if (warp != null) {
						Block block = player.getTargetBlock(null, 10);
						try {
							warp.linkWarp(block);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						SendUnknown.toSender(
								"&b[&3Warp&b]&a Warp '" + warp.getName()
										+ "' has been linked!", sender);
					} else {
						// Warp not found
						SendUnknown.toSender("&b[&3Warp&b]&c Warp '" + name
								+ "' does not exist! &4/warps list", sender);
					}
				} else {
					// No name defined, show help

				}
			}
		} else {
			// Show command usage
			showUsage(sender);
		}

		return false; // Return false
	}
}
