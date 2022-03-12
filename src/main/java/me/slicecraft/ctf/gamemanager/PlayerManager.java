package me.slicecraft.ctf.gamemanager;

import me.slicecraft.ctf.CTF;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        Player flagplacer1 = team1players.get(new Random().nextInt(team1players.size()));
        flagplacer1.getInventory().clear();
        flagplacer1.getInventory().addItem(new ItemStack(Material.GOLD_BLOCK));
        Player flagplacer2 = team2players.get(new Random().nextInt(team2players.size()));
        flagplacer2.getInventory().clear();
        flagplacer2.getInventory().addItem(new ItemStack(Material.GOLD_BLOCK));
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
}
