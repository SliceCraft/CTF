package me.slicecraft.ctf.gamemanager;

import me.slicecraft.ctf.CTF;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerManager {
    Plugin ctfplugin = CTF.getPlugin(CTF.class);
    public List<Player> team1players = new ArrayList();
    public List<Player> team2players = new ArrayList();
    public Integer leftteam1players = 0;
    public Integer leftteam2players = 0;
    public Player flagowner1 = null;
    public Player flagowner2 = null;

    public void makeTeams() {
        List<Player> playerList = new ArrayList<>(Bukkit.getOnlinePlayers());
        team1players = playerList.subList(0, (playerList.size())/2);
        team2players = playerList.subList((playerList.size())/2, playerList.size());
        flagowner1 = null;
        flagowner2 = null;
        leftteam1players = 0;
        leftteam2players = 0;
    }

    public void teleportToArena(){
        team1players.forEach(player -> {
            player.teleport(new Location(ctfplugin.getServer().getWorld(ctfplugin.getConfig().getString("arenaworld")), ctfplugin.getConfig().getInt("team1.spawn.x"), ctfplugin.getConfig().getInt("team1.spawn.y"), ctfplugin.getConfig().getInt("team1.spawn.z")));
        });
        team2players.forEach(player -> {
            player.teleport(new Location(ctfplugin.getServer().getWorld(ctfplugin.getConfig().getString("arenaworld")), ctfplugin.getConfig().getInt("team2.spawn.x"), ctfplugin.getConfig().getInt("team2.spawn.y"), ctfplugin.getConfig().getInt("team2.spawn.z")));
        });
    }

    public void teleportSinglePlayerToArena(Player player){
        team1players.forEach(teamplayer -> {
            if(teamplayer.getDisplayName().equals(player.getDisplayName())){
                player.teleport(new Location(ctfplugin.getServer().getWorld(ctfplugin.getConfig().getString("arenaworld")), ctfplugin.getConfig().getInt("team1.spawn.x"), ctfplugin.getConfig().getInt("team1.spawn.y"), ctfplugin.getConfig().getInt("team1.spawn.z")));
            }
        });
        team2players.forEach(teamplayer -> {
            if(teamplayer.getDisplayName().equals(player.getDisplayName())){
                player.teleport(new Location(ctfplugin.getServer().getWorld(ctfplugin.getConfig().getString("arenaworld")), ctfplugin.getConfig().getInt("team2.spawn.x"), ctfplugin.getConfig().getInt("team2.spawn.y"), ctfplugin.getConfig().getInt("team2.spawn.z")));
            }
        });
    }

    public void giveFlags(){
        flagowner1 = team1players.get(new Random().nextInt(team1players.size()));
        flagowner1.getInventory().clear();
        flagowner1.getInventory().addItem(new ItemStack(Material.GOLD_BLOCK));
        flagowner2 = team2players.get(new Random().nextInt(team2players.size()));
        flagowner2.getInventory().clear();
        flagowner2.getInventory().addItem(new ItemStack(Material.GOLD_BLOCK));
    }

    public void giveItemsToEveryone(){
        team1players.forEach(player -> {
            giveItemsToPlayer(player);
        });
        team2players.forEach(player -> {
            giveItemsToPlayer(player);
        });
    }

    public void giveItemsToPlayer(Player player){
        Inventory inventory = player.getInventory();
        inventory.clear();
        inventory.setItem(0, new ItemStack(Material.DIAMOND_SWORD, 1));
        inventory.setItem(1, new ItemStack(Material.BOW, 1));
        inventory.setItem(2, new ItemStack(Material.DIAMOND_AXE, 1));
        inventory.setItem(3, new ItemStack(Material.DIAMOND_PICKAXE, 1));
        inventory.setItem(4, new ItemStack(Material.OAK_PLANKS, 64));
        inventory.setItem(5, new ItemStack(Material.OAK_PLANKS, 64));
        inventory.setItem(6, new ItemStack(Material.COOKED_BEEF, 64));
        inventory.setItem(7, new ItemStack(Material.ARROW, 64));
        inventory.setItem(8, new ItemStack(Material.BEDROCK, 1));
    }

    public void playerLeft(Player player){
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
        team1players.forEach(teamplayer -> {
            if(teamplayer.getDisplayName().equals(player.getDisplayName())){
                leftteam1players += 1;
            }
        });
        team2players.forEach(teamplayer -> {
            if(teamplayer.getDisplayName().equals(player.getDisplayName())){
                leftteam2players += 1;
            }
        });
        if(team1players.size() - leftteam1players == 0) CTF.gamemanager.endGameNoPlayers(1);
        if(team2players.size() - leftteam2players == 0) CTF.gamemanager.endGameNoPlayers(2);
    }
}
