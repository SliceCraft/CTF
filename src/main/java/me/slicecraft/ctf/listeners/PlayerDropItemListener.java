package me.slicecraft.ctf.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDropItemListener implements Listener {
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event){
        if(event.getItemDrop().getItemStack().getType() == Material.GOLD_BLOCK || event.getItemDrop().getItemStack().getType() == Material.BEDROCK){
            event.setCancelled(true);
        }
    }
}
