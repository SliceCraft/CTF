package me.slicecraft.ctf.listeners;

import me.slicecraft.ctf.CTF;
import me.slicecraft.ctf.gamemanager.GameManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class PlayerJoinListener implements Listener {
    Plugin plugin = CTF.getPlugin(CTF.class);
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        event.getPlayer().getInventory().clear();
        if(CTF.gamemanager.gamestatus != GameManager.GameStatus.NOTSTARTED){
            event.getPlayer().setGameMode(GameMode.SPECTATOR);
            event.getPlayer().teleport(CTF.gamemanager.playermanager.team1players.get(0));
        }else{
            event.getPlayer().setGameMode(GameMode.SURVIVAL);
            event.getPlayer().teleport(new Location(plugin.getServer().getWorld(plugin.getConfig().getString("lobby.world")), plugin.getConfig().getInt("lobby.x"), plugin.getConfig().getInt("lobby.y"), plugin.getConfig().getInt("lobby.z")));
        }
    }
}
