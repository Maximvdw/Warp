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

package be.maximvdw.warp.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import be.maximvdw.warp.Warp;
import be.maximvdw.warp.WarpLink;
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
public class BlockListener implements Listener {
	WarpPlugin wp = null;

	/**
	 * Initialize warpplugin Player listener
	 * 
	 * @param plugin
	 *            Plugin
	 */
	public BlockListener(WarpPlugin plugin) {
		wp = plugin;
	}

	/**
	 * On block break event
	 * 
	 * @param event
	 *            Event
	 */
	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		WarpLink link = (wp.getLink(block));

		// Check if the block is linked
		if (link != null) {
			Warp warp = link.getWarp(); // Get the warp
			// Check if player is allowed to unlink all
			if (Permissions.hasPermissions(Permission.UnlinkAll, player)) {
				playerUnlinkWarp(link, warp, player);
			} else if (Permissions.hasPermissions(Permission.UnlinkOwn, player)
					&& warp.getOwner() == player) {
				playerUnlinkWarp(link, warp, player);
			} else {
				// No permissions
				SendGame.toPlayer("&cYou do not have permissions!", player);
				event.setCancelled(true); // Cancel event
			}
		}
	}

	/**
	 * Unlink a warp
	 * 
	 * @param link
	 *            Warp Link
	 * @param warp
	 *            Warp
	 * @param player
	 *            Player
	 */
	public void playerUnlinkWarp(WarpLink link, Warp warp, Player player) {
		warp.unlinkWarp(link); // Unlink warp
		// Show message
		SendGame.toPlayer("&b[&3Warp&b]&c Warp &6'" + warp.getName()
				+ "' &chas been unlinked!", player);
	}
}
