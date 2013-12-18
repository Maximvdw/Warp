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

package com.maximvdw.warp.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractListener implements Listener {
	/**
	 * Activates Warps when Players click a linked button
	 * 
	 * @param event
	 *            The PlayerInteractEvent that occurred
	 */
	@EventHandler(ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		// Return if the Event was arm flailing
		Block block = event.getClickedBlock();
		if (block == null) {
			return;
		}

		Action action = event.getAction();
		final Player player = event.getPlayer();

		// Return if the Block is not a switch
		Material type = block.getType();
		switch (type) {
		case LEVER: // Fall through
		case STONE_BUTTON: // Fall through
		case WOOD_BUTTON:
			break;
		case TRIPWIRE:
			break;
		case STONE_PLATE: // Fall through
		case WOOD_PLATE:
			break;
		case SIGN:
			break;
		default:
			break;
		}
	}
}
