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

import static java.lang.Math.max;
import static java.lang.Math.min;

public class BlockPlaceListener implements Listener{
    Plugin ctfplugin = CTF.getPlugin(CTF.class);

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        Location location = event.getBlock().getLocation();
        if(!(location.getBlockX() > min(ctfplugin.getConfig().getInt("playarea.pos1.x"), ctfplugin.getConfig().getInt("playarea.pos2.x")) && location.getBlockX() < max(ctfplugin.getConfig().getInt("playarea.pos1.x"), ctfplugin.getConfig().getInt("playarea.pos2.x")) && location.getBlockY() > min(ctfplugin.getConfig().getInt("playarea.pos1.y"), ctfplugin.getConfig().getInt("playarea.pos2.y")) && location.getBlockY() < max(ctfplugin.getConfig().getInt("playarea.pos1.y"), ctfplugin.getConfig().getInt("playarea.pos2.y")) && location.getBlockZ() > min(ctfplugin.getConfig().getInt("playarea.pos1.z"), ctfplugin.getConfig().getInt("playarea.pos2.z")) && location.getBlockZ() < max(ctfplugin.getConfig().getInt("playarea.pos1.z"), ctfplugin.getConfig().getInt("playarea.pos2.z")))){
            event.setCancelled(true);
        }
        if(CTF.gamemanager.gamestatus == GameManager.GameStatus.HIDE){
            Player player = event.getPlayer();
            if(event.getBlock().getType() == Material.GOLD_BLOCK && (event.getBlock().getLocation().equals(new Location(ctfplugin.getServer().getWorld(ctfplugin.getConfig().getString("arenaworld")), ctfplugin.getConfig().getInt("team1.drop.x"), ctfplugin.getConfig().getInt("team1.drop.y") - 1, ctfplugin.getConfig().getInt("team1.drop.z"))) || event.getBlock().getLocation().equals(new Location(ctfplugin.getServer().getWorld(ctfplugin.getConfig().getString("arenaworld")), ctfplugin.getConfig().getInt("team2.drop.x"), ctfplugin.getConfig().getInt("team2.drop.y") - 1, ctfplugin.getConfig().getInt("team2.drop.z"))))){
                event.getPlayer().sendMessage(ChatColor.RED + "You can't place the flag here");
                event.setCancelled(true);
                return;
            }
            CTF.gamemanager.flagPlace(event.getBlock().getLocation(), player);
        }else if(CTF.gamemanager.gamestatus == GameManager.GameStatus.STARTED){
            if(event.getBlock().getType() == Material.BEDROCK) {
                event.setCancelled(true);
                return;
            }
            if(event.getBlock().getLocation().equals(CTF.gamemanager.flagteam1) || event.getBlock().getLocation().equals(CTF.gamemanager.flagteam2)){
                event.getPlayer().sendMessage(ChatColor.RED + "You can't place a block here");
                event.setCancelled(true);
                return;
            }else{
                if(event.getBlock().getType() == Material.GOLD_BLOCK && !(event.getBlock().getLocation().equals(new Location(ctfplugin.getServer().getWorld(ctfplugin.getConfig().getString("arenaworld")), ctfplugin.getConfig().getInt("team1.drop.x"), ctfplugin.getConfig().getInt("team1.drop.y") - 1, ctfplugin.getConfig().getInt("team1.drop.z"))) || event.getBlock().getLocation().equals(new Location(ctfplugin.getServer().getWorld(ctfplugin.getConfig().getString("arenaworld")), ctfplugin.getConfig().getInt("team2.drop.x"), ctfplugin.getConfig().getInt("team2.drop.y") - 1, ctfplugin.getConfig().getInt("team2.drop.z"))))){
                    event.getPlayer().sendMessage(ChatColor.RED + "You can't place the flag here");
                    event.setCancelled(true);
                    return;
                }
                CTF.gamemanager.playermanager.team1players.forEach(teamplayer -> {
                    if(teamplayer.getDisplayName().equals(event.getPlayer().getDisplayName())) {
                        if (event.getBlock().getLocation().equals(new Location(ctfplugin.getServer().getWorld(ctfplugin.getConfig().getString("arenaworld")), ctfplugin.getConfig().getInt("team1.drop.x"), ctfplugin.getConfig().getInt("team1.drop.y") - 1, ctfplugin.getConfig().getInt("team1.drop.z")))){
                            if(event.getBlock().getType() != Material.GOLD_BLOCK) {
                                event.getPlayer().sendMessage(ChatColor.RED + "You can only place the flag here");
                                event.setCancelled(true);
                                return;
                            }
                            Bukkit.broadcastMessage(ChatColor.GREEN + "Team 1 won!");
                            CTF.gamemanager.endGame(event.getPlayer());
                        }else if (event.getBlock().getLocation().equals(new Location(ctfplugin.getServer().getWorld(ctfplugin.getConfig().getString("arenaworld")), ctfplugin.getConfig().getInt("team2.drop.x"), ctfplugin.getConfig().getInt("team2.drop.y") - 1, ctfplugin.getConfig().getInt("team2.drop.z")))){
                            event.setCancelled(true);
                            event.getPlayer().sendMessage(ChatColor.RED + "This is the wrong drop zone");
                        }
                    }
                });
                CTF.gamemanager.playermanager.team2players.forEach(teamplayer -> {
                    if(teamplayer.getDisplayName().equals(event.getPlayer().getDisplayName())) {
                        if (event.getBlock().getLocation().equals(new Location(ctfplugin.getServer().getWorld(ctfplugin.getConfig().getString("arenaworld")), ctfplugin.getConfig().getInt("team2.drop.x"), ctfplugin.getConfig().getInt("team2.drop.y") - 1, ctfplugin.getConfig().getInt("team2.drop.z")))){
                            if(event.getBlock().getType() != Material.GOLD_BLOCK) {
                                event.getPlayer().sendMessage(ChatColor.RED + "You can only place the flag here");
                                event.setCancelled(true);
                                return;
                            }
                            Bukkit.broadcastMessage(ChatColor.GREEN + "Team 2 won!");
                            CTF.gamemanager.endGame(event.getPlayer());
                        }else if (event.getBlock().getLocation().equals(new Location(ctfplugin.getServer().getWorld(ctfplugin.getConfig().getString("arenaworld")), ctfplugin.getConfig().getInt("team1.drop.x"), ctfplugin.getConfig().getInt("team1.drop.y") - 1, ctfplugin.getConfig().getInt("team1.drop.z")))){
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
