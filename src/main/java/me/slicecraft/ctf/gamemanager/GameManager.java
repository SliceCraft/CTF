package me.slicecraft.ctf.gamemanager;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.mask.BlockMask;
import com.sk89q.worldedit.function.mask.BlockTypeMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockTypes;
import me.slicecraft.ctf.CTF;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameManager {
    Plugin ctfplugin = CTF.getPlugin(CTF.class);
    Plugin worldeditplugin = CTF.getWorldEdit();
    public GameStatus gamestatus = GameStatus.NOTSTARTED;
    public PlayerManager playermanager = new PlayerManager();
    public int time = 60;
    public Location flagteam1 = null;
    public Location flagteam2 = null;
    public String flagholder1 = null;
    public String flagholder2 = null;

    public enum GameStatus {
        NOTSTARTED,
        HIDE,
        STARTING,
        STARTED
    }

    public void startGame(){
        gamestatus = GameStatus.HIDE;
        flagteam1 = null;
        flagteam2 = null;
        flagholder1 = null;
        flagholder2 = null;
        pasteArena();
        playermanager.makeTeams();
        playermanager.teleportToArena();
        playermanager.giveFlags();
        List<Player> playerList = new ArrayList<>(Bukkit.getOnlinePlayers());
        playerList.forEach(player -> {
            player.setGameMode(GameMode.SURVIVAL);
            player.setHealth(20);
            player.setFoodLevel(20);
        });
    }

    public void startLobbyTimer(){
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if(gamestatus == GameStatus.NOTSTARTED){
                    if(time == 60 || time == 30 || time == 15 || time == 5 || time == 4 || time == 3 || time == 2){
                        Bukkit.broadcastMessage(ChatColor.GREEN + "Teleporting to arena in " + time + " seconds if there are enough players!");
                    }else if(time == 1){
                        Bukkit.broadcastMessage(ChatColor.GREEN + "Teleporting to arena in " + time + " second if there are enough players!");
                    }else if(time == 0){
                        if(Bukkit.getOnlinePlayers().size() >= 2){
                            startGame();
                            time = 61;
                        }else{
                            Bukkit.broadcastMessage(ChatColor.RED + "Not enough players to teleport to arena");
                            time = 61;
                        }
                    }
                }else if(gamestatus == GameStatus.HIDE){
                    if(time == 60 || time == 30 || time == 15 || time == 5 || time == 4 || time == 3 || time == 2){
                        Bukkit.broadcastMessage(ChatColor.GREEN + "You have " + time + " seconds left to hide the flag!");
                    }else if(time == 1){
                        Bukkit.broadcastMessage(ChatColor.GREEN + "You have " + time + " second left to hide the flag!");
                    }else if(time == 0){
                        // TODO If timer reaches 0 the block will be place at the players current location unless this is impossible in which case that team auto loses
                        gamestatus = GameStatus.STARTED;
                        removeWall();
                        playermanager.giveItemsToEveryone();
                        time = 601;
                    }
                }else if(gamestatus == GameStatus.STARTED){
                    if(time == 120 || time == 180 || time == 240 || time == 300 || time == 360 || time == 420 || time == 480 || time == 540 || time == 600){
                        Bukkit.broadcastMessage(ChatColor.GREEN + "You have " + time/60 + " minutes left to capture the flag!");
                    }else if(time == 60){
                        Bukkit.broadcastMessage(ChatColor.GREEN + "You have " + time/60 + " minute left to capture the flag!");
                    }else if(time == 0){
                        // TODO Make the game end in a tie
                        Bukkit.broadcastMessage(ChatColor.GREEN + "Placeholder game cancelled");
                        gamestatus = GameStatus.NOTSTARTED;
                        time = 61;
                    }
                }
                time--;
            }
        }.runTaskTimer(ctfplugin, 0L, 5L);
    }

    public void pasteArena(){
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

    public void removeWall(){
        WorldEditPlugin worldedit = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
        try (EditSession editSession = worldedit.getWorldEdit().getInstance().newEditSession(new BukkitWorld(ctfplugin.getServer().getWorld(ctfplugin.getConfig().getString("arenaworld"))))){
            BlockVector3 pos1 = BlockVector3.at(ctfplugin.getConfig().getInt("wall.pos1.x"), ctfplugin.getConfig().getInt("wall.pos1.y"), ctfplugin.getConfig().getInt("wall.pos1.z"));
            BlockVector3 pos2 = BlockVector3.at(ctfplugin.getConfig().getInt("wall.pos2.x"), ctfplugin.getConfig().getInt("wall.pos2.y"), ctfplugin.getConfig().getInt("wall.pos2.z"));
            Region region = new CuboidRegion(new BukkitWorld(ctfplugin.getServer().getWorld(ctfplugin.getConfig().getString("arenaworld"))), pos1, pos2);
            Mask mask = new BlockTypeMask(editSession, BlockTypes.GLASS);
            editSession.replaceBlocks(region, mask, BlockTypes.AIR.getDefaultState());
        }catch(MaxChangedBlocksException err){

        }
    }

    public void flagPlace(Location location, Player player){
        playermanager.team1players.forEach(teamplayer -> {
            if(teamplayer.getDisplayName().equals(player.getDisplayName())){
                flagteam1 = location;
                Bukkit.broadcastMessage(ChatColor.GREEN + "Team 1 placed their flag");
            }
        });
        playermanager.team2players.forEach(teamplayer -> {
            if(teamplayer.getDisplayName().equals(player.getDisplayName())){
                flagteam2 = location;
                Bukkit.broadcastMessage(ChatColor.GREEN + "Team 2 placed their flag");
            }
        });
        if(flagteam1 != null && flagteam2 != null){
            time = 0;
        }
    }
}
