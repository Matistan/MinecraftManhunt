package me.matistan05.minecraftmanhunt.listeners;

import me.matistan05.minecraftmanhunt.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import static me.matistan05.minecraftmanhunt.commands.ManhuntCommand.*;

public class AdvancementListener implements Listener {
    public AdvancementListener(Main main) {
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void AdvancementEvent(PlayerAdvancementDoneEvent e) {
        if(!inGame){return;}
        Player p = e.getPlayer();
        if((e.getAdvancement().getKey().getKey().equals("end/kill_dragon")) && speedrunners.contains(p.getName())) {
            playersMessage(ChatColor.DARK_GREEN + p.getName() + " killed a dragon!");
            playersMessage(ChatColor.DARK_GREEN + "Speedrunners won!");
            reset();
        }
    }
}