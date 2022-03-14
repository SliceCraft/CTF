package me.slicecraft.ctf.commands;

import me.slicecraft.ctf.CTF;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class DisableCommand implements CommandExecutor {
    Plugin plugin = CTF.getPlugin(CTF.class);
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String args[]){
        if(sender instanceof Player){
            Player player = (Player) sender;
            if(!player.hasPermission("ctf.disable")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to execute this command!");
                return true;
            }
        }
        sender.sendMessage(ChatColor.RED + "Plugin disabled!");
        plugin.getServer().getPluginManager().disablePlugin(plugin);
        return true;
    }
}