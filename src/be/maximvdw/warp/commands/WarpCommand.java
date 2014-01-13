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

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.maximvdw.warp.Warp;
import be.maximvdw.warp.WarpPlugin;
import be.maximvdw.warp.ui.SendGame;
import be.maximvdw.warp.ui.SendUnknown;
import be.maximvdw.warp.utils.Permissions;
import be.maximvdw.warp.utils.Permissions.Permission;

/**
 * Warp Plugin
 * 
 * @author Maximvdw
 * @version 1.0.0
 */
public class WarpCommand implements CommandExecutor {
	/**
	 * Show command usage
	 * 
	 * @param sender
	 *            Sender
	 */
	public static void showUsage(Object sender) {
		// Check if the sender has permissions
		if (Permissions.hasPermissions(Permission.HelpWarp, sender)) {
			SendUnknown.toSender("&b[&3Warp&b]&c /warp [<player>] <warp>",
					sender); // Send command usage
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
		}

		/* Check if there are enough arguments */
		if (args.length >= 1) {
			if (args.length >= 2) {
				// Get player name
				String playername = args[0];
				WarpPlugin wp = WarpPlugin.getInstance();
				Player targetPlayer = wp.getServer().getPlayer(playername);
				// Check if the player exists
				if (targetPlayer != null) {
					String name = args[0];
					Warp warp = Warp.getWarp(name); // Get specific warp
					// Check if warp exists
					if (warp != null) {
						// Check if the player has permission
						if (warp.hasPermission(player)) {
							warp.warpPlayer(targetPlayer, true);
							// Player not found
							SendUnknown.toSender("&b[&3Warp&b]&a Teleported "
									+ targetPlayer.getName() + " to warp '"
									+ name + "'!", sender);
						} else {
							// No permissions
							SendGame.toPlayer("&cYou do not have permissions!",
									player);
						}
					} else {
						// Warp not found
						SendUnknown.toSender("&b[&3Warp&b]&c Warp '" + name
								+ "' does not exist! &4/warps list", sender);
					}
				} else {
					// Player not found
					SendUnknown.toSender("&b[&3Warp&b]&c Player " + playername
							+ " was not found!", sender);
				}
			} else {
				// Set named warp
				String name = args[0];
				Warp warp = Warp.getWarp(name); // Get specific warp
				// Check if warp exists
				if (warp != null) {
					// Check if the player has permission
					if (warp.hasPermission(player)) {
						warp.warpPlayer(player, true);
					} else {
						// No permissions
						SendGame.toPlayer("&cYou do not have permissions!",
								player);
					}
				} else {
					// Warp not found
					SendUnknown.toSender("&b[&3Warp&b]&c Warp '" + name
							+ "' does not exist! &4/warps list", sender);
				}
			}
		} else {
			// Show command usage
			showUsage(sender);
		}

		return false; // Return false
	}
}
