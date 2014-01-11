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

package be.maximvdw.warp.hooks;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import com.palmergames.bukkit.towny.Towny;

public class TownyHook implements WarpHook {
	Towny plugin = null; // Plugin

	/**
	 * Initialize Towny-Advanced hook
	 * 
	 * @param plugin
	 *            Towny plugin
	 */
	public TownyHook(Plugin plugin) {
		this.plugin = (Towny)plugin;
	}

	@Override
	public boolean allowRandomWarp(Location location) {
		// TODO Auto-generated method stub
		return false;
	}

}
