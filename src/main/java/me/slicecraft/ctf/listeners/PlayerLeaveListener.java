package me.slicecraft.ctf.listeners;

import me.slicecraft.ctf.CTF;
import me.slicecraft.ctf.gamemanager.GameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        if(CTF.gamemanager.gamestatus != GameManager.GameStatus.NOTSTARTED) {
            CTF.gamemanager.playermanager.playerLeft(event.getPlayer());
        }
    }
}
