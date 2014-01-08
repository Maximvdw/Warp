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

import org.bukkit.entity.Player;

public class SendUnknown {
	/**
	 * Send a message to an unkown receiver
	 * 
	 * @param message Message to send
	 * @param sender Reciever
	 */
	public static void toSender(String message, Object sender){
		if (sender instanceof Player){
			SendGame.toPlayer(message, (Player)sender); // Send to game
		}else{
			SendConsole.info(message); // Send to console
		}
	}
}
