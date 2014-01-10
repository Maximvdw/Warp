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

package be.maximvdw.warp;

import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Warp Plugin
 * 
 * @author Maximvdw
 * @version 1.0.0
 */
public class WarpLink {
	Block block = null; // Link location
	Warp warp = null; // Linked warp

	/**
	 * Link a warp to a trigger
	 * 
	 * @param warp
	 *            Warp
	 * @param location
	 *            Location
	 * @throws Exception
	 */
	public WarpLink(Warp warp, Block block) throws Exception {
		// Save arguments (check comes later)
		this.warp = warp;
		this.block = block;

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
			// Unkown blocktype
			throw new Exception(
					"Warps can only be linked to buttons,levers,tripwire,pressureplates or signs");
		}
	}
}
