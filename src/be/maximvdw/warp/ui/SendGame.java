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
import org.bukkit.entity.Player;

/**
 * Warp Plugin
 * 
 * @author Maximvdw
 * @version 1.0.0
 */

public class SendGame {
	
	/**
	 * Send a private message
	 * 
	 * @param message Message to send
	 */
	public static void toPlayer(String message, Player player){
		player.sendMessage(message);
	}
	
	/**
	 * Send a broadcast to the server
	 * 
	 * @param message Message to broadcast
	 */
	public static void toServer(String message){
		Bukkit.broadcastMessage(message);
	}

}
