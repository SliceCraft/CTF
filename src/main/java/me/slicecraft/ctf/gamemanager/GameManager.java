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
import com.sk89q.worldedit.function.mask.BlockTypeMask;
import com.sk89q.worldedit.function.mask.ExistingBlockMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockTypes;
import me.slicecraft.ctf.CTF;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
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
    public InfoManager infomanager = new InfoManager();
    public int time = 60;
    public Location flagteam1 = null;
    public Location flagteam2 = null;
    public String flagholder1 = null;
    public String flagholder2 = null;

    public enum GameStatus {
        NOTSTARTED,
        HIDE,
        STARTED
    }

    public void startGame(){
        gamestatus = GameStatus.HIDE;
        flagteam1 = null;
        flagteam2 = null;
        flagholder1 = null;
        flagholder2 = null;
        resetPlayArea();
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
        new BukkitRunnable() {
            @Override
            public void run() {
                if (gamestatus == GameStatus.NOTSTARTED) {
                    infomanager.changeItem(ChatColor.GREEN + "Starting in: " + ChatColor.AQUA + Math.round(time/60) + ":" + String.format("%02d", (time - (Math.round(time/60)*60))), 1);
                    if (time == 60 || time == 30 || time == 15 || time == 5 || time == 4 || time == 3 || time == 2) {
                        Bukkit.broadcastMessage(ChatColor.GREEN + "Teleporting to arena in " + time + " seconds if there are enough players!");
                    } else if (time == 1) {
                        Bukkit.broadcastMessage(ChatColor.GREEN + "Teleporting to arena in " + time + " second if there are enough players!");
                    } else if (time == 0) {
                        if (Bukkit.getOnlinePlayers().size() >= 2) {
                            startGame();
                        } else {
                            Bukkit.broadcastMessage(ChatColor.RED + "Not enough players to teleport to arena");
                        }
                        time = 61;
                    }
                } else if (gamestatus == GameStatus.HIDE) {
                    infomanager.changeItem(ChatColor.GREEN + "Time left: " + ChatColor.AQUA + Math.round(time/60) + ":" + String.format("%02d", (time - (Math.round(time/60)*60))), 1);
                    if (time == 60 || time == 30 || time == 15 || time == 5 || time == 4 || time == 3 || time == 2) {
                        Bukkit.broadcastMessage(ChatColor.GREEN + "You have " + time + " seconds left to hide the flag!");
                    } else if (time == 1) {
                        Bukkit.broadcastMessage(ChatColor.GREEN + "You have " + time + " second left to hide the flag!");
                    } else if (time == 0) {
                        int noflag = 0;
                        if (flagteam1 == null) noflag += 1;
                        if (flagteam2 == null) noflag += 2;
                        if (noflag != 0) {
                            endGameNoFlag(noflag);
                        } else {
                            gamestatus = GameStatus.STARTED;
                            removeWall();
                            playermanager.giveItemsToEveryone();
                            time = 601;
                        }
                    }
                } else if (gamestatus == GameStatus.STARTED) {
                    infomanager.changeItem(ChatColor.GREEN + "Game over in: " + ChatColor.AQUA + Math.round(time/60) + ":" + String.format("%02d", (time - (Math.round(time/60)*60))), 3);
                    if(flagholder1 == null) infomanager.changeItem(ChatColor.YELLOW + "Team 1 flag: " + ChatColor.GREEN + "Not stolen", 2);
                    else infomanager.changeItem(ChatColor.YELLOW + "Team 1 flag: " + ChatColor.RED + "Stolen", 2);
                    if(flagholder2 == null) infomanager.changeItem(ChatColor.YELLOW + "Team 2 flag: " + ChatColor.GREEN + "Not stolen", 1);
                    else infomanager.changeItem(ChatColor.YELLOW + "Team 2 flag: " + ChatColor.RED + "Stolen", 1);
                    if (time == 120 || time == 180 || time == 240 || time == 300 || time == 360 || time == 420 || time == 480 || time == 540 || time == 600) {
                        Bukkit.broadcastMessage(ChatColor.GREEN + "You have " + time / 60 + " minutes left to capture the flag!");
                    } else if (time == 60) {
                        Bukkit.broadcastMessage(ChatColor.GREEN + "You have " + time / 60 + " minute left to capture the flag!");
                    } else if (time == 0) {
                        CTF.gamemanager.endGame(null);
                        Bukkit.broadcastMessage(ChatColor.GREEN + "Game ended in a tie");
                        gamestatus = GameStatus.NOTSTARTED;
                        time = 61;
                    }
                }
                time--;
            }
        }.runTaskTimer(ctfplugin, 0L, 20L);
    }

    public void pasteArena(){
        Clipboard clipboard;
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

    public void resetPlayArea(){
        WorldEditPlugin worldedit = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
        try (EditSession editSession = worldedit.getWorldEdit().getInstance().newEditSession(new BukkitWorld(ctfplugin.getServer().getWorld(ctfplugin.getConfig().getString("arenaworld"))))){
            BlockVector3 pos1 = BlockVector3.at(ctfplugin.getConfig().getInt("playarea.pos1.x"), ctfplugin.getConfig().getInt("playarea.pos1.y"), ctfplugin.getConfig().getInt("playarea.pos1.z"));
            BlockVector3 pos2 = BlockVector3.at(ctfplugin.getConfig().getInt("playarea.pos2.x"), ctfplugin.getConfig().getInt("playarea.pos2.y"), ctfplugin.getConfig().getInt("playarea.pos2.z"));
            Region region = new CuboidRegion(new BukkitWorld(ctfplugin.getServer().getWorld(ctfplugin.getConfig().getString("arenaworld"))), pos1, pos2);
            Mask mask = new ExistingBlockMask(editSession);
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

    public void endGameNoPlayers(Integer team){
        infomanager.changeItem("", 1);
        infomanager.changeItem("", 2);
        infomanager.changeItem("", 3);
        if(team == 1){
            playermanager.team2players.forEach(player -> {
                player.sendTitle(ChatColor.GREEN + "You won!", ChatColor.GREEN + "The players from the other team left", 10, 100, 10);
            });
        }else if(team == 2){
            playermanager.team1players.forEach(player -> {
                player.sendTitle(ChatColor.GREEN + "You won!", ChatColor.GREEN + "The players from the other team left", 10, 100, 10);
            });
        }
        List<Player> playerList = new ArrayList<>(Bukkit.getOnlinePlayers());
        playerList.forEach(player -> {
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().clear();
            player.teleport(new Location(ctfplugin.getServer().getWorld(ctfplugin.getConfig().getString("lobby.world")), ctfplugin.getConfig().getInt("lobby.x"), ctfplugin.getConfig().getInt("lobby.y"), ctfplugin.getConfig().getInt("lobby.z")));
        });
        gamestatus = GameStatus.NOTSTARTED;
        time = 60;
    }

    public void endGameNoFlag(Integer team){
        infomanager.changeItem("", 1);
        infomanager.changeItem("", 2);
        infomanager.changeItem("", 3);
        if(team == 1){
            playermanager.team1players.forEach(player -> {
                player.sendTitle(ChatColor.RED + "You lost!", ChatColor.RED + "The flag wasn't placed", 10, 100, 10);
            });
            playermanager.team2players.forEach(player -> {
                player.sendTitle(ChatColor.GREEN + "You won!", ChatColor.GREEN + "The other team didn't place the flag", 10, 100, 10);
            });
        }else if(team == 2){
            playermanager.team1players.forEach(player -> {
                player.sendTitle(ChatColor.GREEN + "You won!", ChatColor.GREEN + "The other team didn't place the flag", 10, 100, 10);
            });
            playermanager.team2players.forEach(player -> {
                player.sendTitle(ChatColor.RED + "You lost!", ChatColor.RED + "The flag wasn't placed", 10, 100, 10);
            });
        }else if(team == 3){
            playermanager.team1players.forEach(player -> {
                player.sendTitle(ChatColor.RED + "Game over!", ChatColor.RED + "No-one placed a flag!", 10, 100, 10);
            });
            playermanager.team2players.forEach(player -> {
                player.sendTitle(ChatColor.RED + "Game over!", ChatColor.RED + "No-one placed a flag!", 10, 100, 10);
            });
        }
        List<Player> playerList = new ArrayList<>(Bukkit.getOnlinePlayers());
        playerList.forEach(player -> {
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().clear();
            player.teleport(new Location(ctfplugin.getServer().getWorld(ctfplugin.getConfig().getString("lobby.world")), ctfplugin.getConfig().getInt("lobby.x"), ctfplugin.getConfig().getInt("lobby.y"), ctfplugin.getConfig().getInt("lobby.z")));
        });
        gamestatus = GameStatus.NOTSTARTED;
        time = 61;
    }

    public void endGame(@Nullable Player winner){
        infomanager.changeItem("", 1);
        infomanager.changeItem("", 2);
        infomanager.changeItem("", 3);
        if(winner != null){
            playermanager.team1players.forEach(teamplayer -> {
                if(teamplayer.getDisplayName().equals(winner.getDisplayName())){
                    playermanager.team1players.forEach(player -> {
                        player.sendTitle(ChatColor.GREEN + "You won!", ChatColor.GREEN + winner.getDisplayName() + " captured the flag!", 10, 100, 10);
                        player.teleport(new Location(ctfplugin.getServer().getWorld(ctfplugin.getConfig().getString("lobby.world")), ctfplugin.getConfig().getInt("lobby.x"), ctfplugin.getConfig().getInt("lobby.y"), ctfplugin.getConfig().getInt("lobby.z")));
                    });
                    playermanager.team2players.forEach(player -> {
                        player.sendTitle(ChatColor.RED + "You lost!", ChatColor.RED + winner.getDisplayName() + " captured the flag!", 10, 100, 10);
                        player.teleport(new Location(ctfplugin.getServer().getWorld(ctfplugin.getConfig().getString("lobby.world")), ctfplugin.getConfig().getInt("lobby.x"), ctfplugin.getConfig().getInt("lobby.y"), ctfplugin.getConfig().getInt("lobby.z")));
                    });
                }
            });
            playermanager.team2players.forEach(teamplayer -> {
                if(teamplayer.getDisplayName().equals(winner.getDisplayName())){
                    playermanager.team1players.forEach(player -> {
                        player.sendTitle(ChatColor.RED + "You lost!", ChatColor.RED + winner.getDisplayName() + " captured the flag!", 10, 100, 10);
                        player.teleport(new Location(ctfplugin.getServer().getWorld(ctfplugin.getConfig().getString("lobby.world")), ctfplugin.getConfig().getInt("lobby.x"), ctfplugin.getConfig().getInt("lobby.y"), ctfplugin.getConfig().getInt("lobby.z")));
                    });
                    playermanager.team2players.forEach(player -> {
                        player.sendTitle(ChatColor.GREEN + "You won!", ChatColor.GREEN + winner.getDisplayName() + " captured the flag!", 10, 100, 10);
                        player.teleport(new Location(ctfplugin.getServer().getWorld(ctfplugin.getConfig().getString("lobby.world")), ctfplugin.getConfig().getInt("lobby.x"), ctfplugin.getConfig().getInt("lobby.y"), ctfplugin.getConfig().getInt("lobby.z")));
                    });
                }
            });
        }else{
            playermanager.team1players.forEach(player -> {
                player.sendTitle(ChatColor.RED + "Game over!", ChatColor.RED + "Game ended in a tie!", 10, 100, 10);
                player.teleport(new Location(ctfplugin.getServer().getWorld(ctfplugin.getConfig().getString("lobby.world")), ctfplugin.getConfig().getInt("lobby.x"), ctfplugin.getConfig().getInt("lobby.y"), ctfplugin.getConfig().getInt("lobby.z")));
            });
            playermanager.team2players.forEach(player -> {
                player.sendTitle(ChatColor.RED + "Game over!", ChatColor.RED + "Game ended in a tie!", 10, 100, 10);
                player.teleport(new Location(ctfplugin.getServer().getWorld(ctfplugin.getConfig().getString("lobby.world")), ctfplugin.getConfig().getInt("lobby.x"), ctfplugin.getConfig().getInt("lobby.y"), ctfplugin.getConfig().getInt("lobby.z")));
            });
        }
        List<Player> playerList = new ArrayList<>(Bukkit.getOnlinePlayers());
        playerList.forEach(player -> {
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().clear();
            player.teleport(new Location(ctfplugin.getServer().getWorld(ctfplugin.getConfig().getString("lobby.world")), ctfplugin.getConfig().getInt("lobby.x"), ctfplugin.getConfig().getInt("lobby.y"), ctfplugin.getConfig().getInt("lobby.z")));
        });
        gamestatus = GameStatus.NOTSTARTED;
        time = 60;
    }
}
