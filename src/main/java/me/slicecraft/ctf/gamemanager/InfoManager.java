package me.slicecraft.ctf.gamemanager;

import me.slicecraft.ctf.CTF;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.*;

import java.awt.*;
import java.util.ArrayList;

public class InfoManager {
    Plugin ctfplugin = CTF.getPlugin(CTF.class);
    private Scoreboard scoreboard;
    private Objective objective;
    private Score score1;
    private Score score2;
    private Score score3;
    private String text1 = "";
    private String text2 = "";
    private String text3 = "";

    InfoManager() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();
        objective = scoreboard.registerNewObjective("gameinfo", "dummy", ChatColor.GOLD + "Capture the flag");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void changeItem(String newtext, int slot){
        if(slot == 1) {
            scoreboard.resetScores(text1);
            if(newtext != "") {
                score1 = objective.getScore(newtext);
                score1.setScore(1);
                text1 = newtext;
            }
        }
        if(slot == 2) {
            scoreboard.resetScores(text2);
            if(newtext != "") {
                score2 = objective.getScore(newtext);
                score2.setScore(2);
                text2 = newtext;
            }
        }
        if(slot == 3) {
            scoreboard.resetScores(text3);
            if(newtext != "") {
                score3 = objective.getScore(newtext);
                score3.setScore(3);
                text3 = newtext;
            }
        }
    }

    public void onJoin(Player player){
        player.setScoreboard(scoreboard);
    }
}
