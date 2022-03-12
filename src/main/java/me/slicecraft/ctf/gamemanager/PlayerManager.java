package me.slicecraft.ctf.gamemanager;

import me.slicecraft.ctf.CTF;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class PlayerManager {
    Plugin ctfplugin = CTF.getPlugin(CTF.class);
    public List<Player> team1players = new ArrayList();
    public List<Player> team2players = new ArrayList();

    public void makeTeams() {
        List<Player> playerList = new ArrayList<>(Bukkit.getOnlinePlayers());
        team1players = playerList.subList(0, (playerList.size())/2);
        team2players = playerList.subList((playerList.size())/2, playerList.size());
    }

    public void teleportToArena(){
        team1players.forEach(player -> {
            player.teleport(new Location(ctfplugin.getServer().getWorld(ctfplugin.getConfig().getString("arenaworld")), ctfplugin.getConfig().getInt("team1spawn.x"), ctfplugin.getConfig().getInt("team1spawn.y"), ctfplugin.getConfig().getInt("team1spawn.z")));
        });
        team2players.forEach(player -> {
            player.teleport(new Location(ctfplugin.getServer().getWorld(ctfplugin.getConfig().getString("arenaworld")), ctfplugin.getConfig().getInt("team2spawn.x"), ctfplugin.getConfig().getInt("team2spawn.y"), ctfplugin.getConfig().getInt("team2spawn.z")));
        });
    }
}
