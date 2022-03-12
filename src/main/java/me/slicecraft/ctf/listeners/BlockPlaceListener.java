package me.slicecraft.ctf.listeners;

import me.slicecraft.ctf.CTF;
import me.slicecraft.ctf.gamemanager.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class BlockPlaceListener implements Listener{
    Plugin ctfplugin = CTF.getPlugin(CTF.class);

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        if(CTF.gamemanager.gamestatus == GameManager.GameStatus.HIDE){
            Player player = event.getPlayer();
            CTF.gamemanager.flagPlace(event.getBlock().getLocation(), player);
        }else if(CTF.gamemanager.gamestatus == GameManager.GameStatus.STARTED){
            if(event.getBlock().getLocation().equals(CTF.gamemanager.flagteam1) || event.getBlock().getLocation().equals(CTF.gamemanager.flagteam2)){
                event.getPlayer().sendMessage(ChatColor.RED + "You can't place a block here");
            }else{
                if(event.getBlock().getType() != Material.GOLD_BLOCK) {
                    event.getPlayer().sendMessage(ChatColor.RED + "You can only place the flag here");
                    return;
                }
                CTF.gamemanager.playermanager.team1players.forEach(teamplayer -> {
                    if(teamplayer.getDisplayName().equals(event.getPlayer().getDisplayName())) {
                        if (event.getBlock().getLocation().equals(new Location(ctfplugin.getServer().getWorld(ctfplugin.getConfig().getString("arenaworld")), ctfplugin.getConfig().getInt("team1.drop.x"), ctfplugin.getConfig().getInt("team1.drop.y") - 1, ctfplugin.getConfig().getInt("team1.drop.z")))){
                            Bukkit.broadcastMessage(ChatColor.GREEN + "Team 1 won!");
                        }else {
                            event.setCancelled(true);
                            event.getPlayer().sendMessage(ChatColor.RED + "This is the wrong drop zone");
                        }
                    }
                });
                CTF.gamemanager.playermanager.team2players.forEach(teamplayer -> {
                    if(teamplayer.getDisplayName().equals(event.getPlayer().getDisplayName())) {
                        if (event.getBlock().getLocation().equals(new Location(ctfplugin.getServer().getWorld(ctfplugin.getConfig().getString("arenaworld")), ctfplugin.getConfig().getInt("team2.drop.x"), ctfplugin.getConfig().getInt("team2.drop.y") - 1, ctfplugin.getConfig().getInt("team2.drop.z")))){
                            Bukkit.broadcastMessage(ChatColor.GREEN + "Team 2 won!");
                        }else {
                            event.setCancelled(true);
                            event.getPlayer().sendMessage(ChatColor.RED + "This is the wrong drop zone");
                        }
                    }
                });
            }
        }else{
            event.setCancelled(true);
        }
    }
}
