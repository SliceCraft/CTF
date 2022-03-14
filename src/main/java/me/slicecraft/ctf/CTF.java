package me.slicecraft.ctf;

import me.slicecraft.ctf.commands.BuildCommand;
import me.slicecraft.ctf.commands.DisableCommand;
import me.slicecraft.ctf.commands.MaketeamsCommand;
import me.slicecraft.ctf.gamemanager.GameManager;
import me.slicecraft.ctf.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class CTF extends JavaPlugin {

    public static Plugin getWorldEdit(){
        return Bukkit.getPluginManager().getPlugin("WorldEdit");
    }
    public static GameManager gamemanager = null;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        getCommand("build").setExecutor(new BuildCommand());
        getCommand("disable").setExecutor(new DisableCommand());
        getCommand("maketeams").setExecutor(new MaketeamsCommand());
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
        getServer().getPluginManager().registerEvents(new EntityDamageListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerLeaveListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDropItemListener(), this);
        gamemanager = new GameManager();
        gamemanager.startLobbyTimer();
    }
}
