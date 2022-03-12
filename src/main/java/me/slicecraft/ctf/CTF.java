package me.slicecraft.ctf;

import me.slicecraft.ctf.commands.BuildCommand;
import me.slicecraft.ctf.commands.DisableCommand;
import me.slicecraft.ctf.commands.MaketeamsCommand;
import me.slicecraft.ctf.gamemanager.GameManager;
import me.slicecraft.ctf.listeners.BlockBreakListener;
import me.slicecraft.ctf.listeners.BlockPlaceListener;
import me.slicecraft.ctf.listeners.EntityDamageListener;
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
        getCommand("build").setExecutor(new BuildCommand());
        getCommand("disable").setExecutor(new DisableCommand());
        getCommand("maketeams").setExecutor(new MaketeamsCommand());
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
        getServer().getPluginManager().registerEvents(new EntityDamageListener(), this);
        gamemanager = new GameManager();
        gamemanager.startLobbyTimer();
    }
}
