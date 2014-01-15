package be.maximvdw.warp.utils;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerUtils {
	public static Player getPlayerByUUID(UUID id) {
	    for (World w : Bukkit.getServer().getWorlds()) {
	        for (Entity e : w.getEntities()) {
	            if (e.getUniqueId().compareTo(id) == 0) {
	                if (e instanceof Player){
	                	return (Player)e;
	                }
	            }
	        }
	    }
	    return null;
	}
}
