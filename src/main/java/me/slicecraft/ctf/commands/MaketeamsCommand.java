package me.slicecraft.ctf.commands;

import me.slicecraft.ctf.CTF;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MaketeamsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String args[]){
        CTF.gamemanager.playermanager.makeTeams();
        CTF.gamemanager.playermanager.teleportToArena();
        return true;
    }
}
