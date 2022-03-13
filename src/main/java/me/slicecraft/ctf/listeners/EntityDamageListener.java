package me.slicecraft.ctf.listeners;

import me.slicecraft.ctf.CTF;
import me.slicecraft.ctf.gamemanager.GameManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class EntityDamageListener implements Listener {
    Plugin ctfplugin = CTF.getPlugin(CTF.class);

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        if(event.getEntity() instanceof Player){
            if(CTF.gamemanager.gamestatus != GameManager.GameStatus.STARTED) {
                event.setCancelled(true);
                return;
            }
            Player player = (Player) event.getEntity();
            if(player.getHealth() - event.getFinalDamage() <= 0){
                event.setCancelled(true);
                player.setHealth(20);
                player.setFoodLevel(20);
                player.setGameMode(GameMode.SPECTATOR);
                player.sendMessage(ChatColor.RED + "You died!");
                respawnHandler(player);
                if(CTF.gamemanager.flagholder1 != null){
                    if(CTF.gamemanager.flagholder1.equals(player.getDisplayName())){
                        CTF.gamemanager.flagteam1.getBlock().setType(Material.GOLD_BLOCK);
                        CTF.gamemanager.flagholder1 = null;
                    }
                }
                if(CTF.gamemanager.flagholder2 != null){
                    if(CTF.gamemanager.flagholder2.equals(player.getDisplayName())){
                        CTF.gamemanager.flagteam2.getBlock().setType(Material.GOLD_BLOCK);
                        CTF.gamemanager.flagholder2 = null;
                    }
                }
            }
        }
    }

    private void respawnHandler(Player player){
        BukkitTask task = new BukkitRunnable() {
            Integer time = 5;
            @Override
            public void run() {
                if(time > 1) {
                    player.sendTitle(ChatColor.RED + "You died!", ChatColor.YELLOW + "You will respawn in " + time + " seconds", 5, 10, 5);
                }else if(time > 0){
                    player.sendTitle(ChatColor.RED + "You died!", ChatColor.YELLOW + "You will respawn in " + time + " second", 5, 10, 5);
                }else{
                    if(CTF.gamemanager.gamestatus != GameManager.GameStatus.NOTSTARTED) {
                        CTF.gamemanager.playermanager.giveItemsToPlayer(player);
                        CTF.gamemanager.playermanager.teleportSinglePlayerToArena(player);
                    }else{
                        player.getInventory().clear();
                        player.teleport(new Location(ctfplugin.getServer().getWorld(ctfplugin.getConfig().getString("lobby.world")), ctfplugin.getConfig().getInt("lobby.x"), ctfplugin.getConfig().getInt("lobby.y"), ctfplugin.getConfig().getInt("lobby.z")));
                    }
                    player.setGameMode(GameMode.SURVIVAL);
                    cancel();
                }
                time--;
            }
        }.runTaskTimer(ctfplugin, 0L, 20L);
    }
}
