package me.slicecraft.ctf.listeners;

import me.slicecraft.ctf.CTF;
import me.slicecraft.ctf.gamemanager.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class BlockBreakListener implements Listener{
    Plugin ctfplugin = CTF.getPlugin(CTF.class);

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        if(CTF.gamemanager.gamestatus != GameManager.GameStatus.STARTED){
            event.setCancelled(true);
        }else{
            CTF.gamemanager.playermanager.team1players.forEach(teamplayer -> {
                if(teamplayer.getDisplayName().equals(event.getPlayer().getDisplayName())){
                    if(event.getBlock().getLocation().equals(CTF.gamemanager.flagteam2)){
                        Bukkit.broadcastMessage(ChatColor.GREEN + "Team 1 stole the flag from team 2");
                        CTF.gamemanager.flagholder2 = event.getPlayer().getDisplayName();
                        event.getPlayer().getInventory().addItem(new ItemStack(Material.GOLD_BLOCK));
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
                        event.getPlayer().getInventory().addItem(new ItemStack(Material.GOLD_BLOCK));
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
