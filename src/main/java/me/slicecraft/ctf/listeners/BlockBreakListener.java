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
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class BlockBreakListener implements Listener{
    Plugin ctfplugin = CTF.getPlugin(CTF.class);

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Location location = event.getBlock().getLocation();
        if(!(location.getBlockX() > min(ctfplugin.getConfig().getInt("playarea.pos1.x"), ctfplugin.getConfig().getInt("playarea.pos2.x")) && location.getBlockX() < max(ctfplugin.getConfig().getInt("playarea.pos1.x"), ctfplugin.getConfig().getInt("playarea.pos2.x")) && location.getBlockY() > min(ctfplugin.getConfig().getInt("playarea.pos1.y"), ctfplugin.getConfig().getInt("playarea.pos2.y")) && location.getBlockY() < max(ctfplugin.getConfig().getInt("playarea.pos1.y"), ctfplugin.getConfig().getInt("playarea.pos2.y")) && location.getBlockZ() > min(ctfplugin.getConfig().getInt("playarea.pos1.z"), ctfplugin.getConfig().getInt("playarea.pos2.z")) && location.getBlockZ() < max(ctfplugin.getConfig().getInt("playarea.pos1.z"), ctfplugin.getConfig().getInt("playarea.pos2.z")))){
            event.setCancelled(true);
            return;
        }
        if(CTF.gamemanager.gamestatus != GameManager.GameStatus.STARTED){
            event.setCancelled(true);
        }else{
            CTF.gamemanager.playermanager.team1players.forEach(teamplayer -> {
                if(teamplayer.getDisplayName().equals(event.getPlayer().getDisplayName())){
                    if(event.getBlock().getLocation().equals(CTF.gamemanager.flagteam2)){
                        Bukkit.broadcastMessage(ChatColor.GREEN + "Team 1 stole the flag from team 2");
                        CTF.gamemanager.flagholder2 = event.getPlayer().getDisplayName();
                        event.getPlayer().getInventory().setItem(8, new ItemStack(Material.GOLD_BLOCK, 1));
                        event.setDropItems(false);
                    }else if(event.getBlock().getLocation().equals(CTF.gamemanager.flagteam1)){
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(ChatColor.RED + "You can't steal your own flag");
                    }
                }
            });
            CTF.gamemanager.playermanager.team2players.forEach(teamplayer -> {
                if(teamplayer.getDisplayName().equals(event.getPlayer().getDisplayName())){
                    if(event.getBlock().getLocation().equals(CTF.gamemanager.flagteam1)) {
                        Bukkit.broadcastMessage(ChatColor.GREEN + "Team 2 stole the flag from team 1");
                        CTF.gamemanager.flagholder1 = event.getPlayer().getDisplayName();
                        event.getPlayer().getInventory().setItem(8, new ItemStack(Material.GOLD_BLOCK, 1));
                        event.setDropItems(false);
                    }else if(event.getBlock().getLocation().equals(CTF.gamemanager.flagteam2)){
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(ChatColor.RED + "You can't steal your own flag");
                    }
                }
            });
        }
    }
}
