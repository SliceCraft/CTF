package me.slicecraft.ctf.commands;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import me.slicecraft.ctf.CTF;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class BuildCommand implements CommandExecutor {
    Plugin ctfplugin = CTF.getPlugin(CTF.class);
    Plugin worldeditplugin = CTF.getWorldEdit();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String args[]){
        sender.sendMessage(ChatColor.AQUA + "Building world");
        pasteAsync();
        sender.sendMessage(ChatColor.AQUA + "World should be built");
        return true;
    }

    public void pasteAsync(){
        Clipboard clipboard = null;
        File file = new File(worldeditplugin.getDataFolder().getAbsolutePath() + "/schematics/arena.schem");
        WorldEditPlugin worldedit = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        try (EditSession editSession = worldedit.getWorldEdit().getInstance().newEditSession(new BukkitWorld(ctfplugin.getServer().getWorld(ctfplugin.getConfig().getString("arenaworld"))))){
            try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
                clipboard = reader.read();
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(BlockVector3.at(0, 130, 0))
                        .ignoreAirBlocks(false)
                        .build();
                Operations.complete(operation);
            }catch(FileNotFoundException err){

            }catch(IOException err){

            }catch(WorldEditException err){

            }
        }
    }
}
