package me.slicecraft.ctf.listeners;

import me.slicecraft.ctf.CTF;
import me.slicecraft.ctf.gamemanager.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;

public class BlockPlaceListener implements Listener{
    Plugin ctfplugin = CTF.getPlugin(CTF.class);

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        if(CTF.gamemanager.gamestatus != GameManager.GameStatus.HIDE && CTF.gamemanager.gamestatus != GameManager.GameStatus.STARTED){
            event.setCancelled(true);
        }else if(CTF.gamemanager.gamestatus == GameManager.GameStatus.HIDE){
            Player player = event.getPlayer();
            CTF.gamemanager.flagPlace(event.getBlock().getLocation(), player);
        }
    }
}
