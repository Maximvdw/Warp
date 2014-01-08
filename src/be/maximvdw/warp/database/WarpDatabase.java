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

package be.maximvdw.warp.database;

import be.maximvdw.warp.WarpPlugin;
import be.maximvdw.warp.config.Configuration;
import be.maximvdw.warp.ui.SendConsole;

public class WarpDatabase {
	public static void initDatabase() {
		/* Initialize a new database connection */
		MySQL db = new MySQL(WarpPlugin.getInstance().getLogger(),
				Configuration.prefix, Configuration.hostname,
				Configuration.portnmbr, Configuration.database,
				Configuration.username, Configuration.password);
		// Check if warps table exists
		SendConsole.info("Checking tables ...");
		if (!db.checkTable("warps")) {
			// Warps table does not exist
			SendConsole.info("Creating table '" + Configuration.prefix + "warps'...");
			String query = "CREATE TABLE warps (id INT AUTO_INCREMENT PRIMARY_KEY, name VARCHAR(255));";
			db.createTable(query);
		}
		if (!db.checkTable("cachedwarps")) {
			// Cachedwarps table does not exist
			SendConsole.info("Creating table '" + Configuration.prefix + "cachedwarps'...");
			String query = "CREATE TABLE cachedwarps (id INT AUTO_INCREMENT PRIMARY_KEY, name VARCHAR(255));";
			db.createTable(query);
		}
	}
}
