package me.matistan05.minecraftmanhunt.listeners;

import me.matistan05.minecraftmanhunt.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import static me.matistan05.minecraftmanhunt.commands.ManhuntCommand.*;

public class DisconnectListener implements Listener {
    public DisconnectListener(Main main) {
        Bukkit.getPluginManager().registerEvents(this, main);
    }
    @EventHandler
    public void DisconnectEvent(PlayerQuitEvent e) {
        if(inGame && (speedrunners.contains(e.getPlayer().getName()) || hunters.contains(e.getPlayer().getName())) && ((pausePlayers.contains(e.getPlayer().getName()) && pausePlayers.size() < hunters.size() + speedrunners.size()) || (unpausePlayers.contains(e.getPlayer().getName()) && unpausePlayers.size() < hunters.size() + speedrunners.size()))) {
            if(pausePlayers.contains(e.getPlayer().getName()) && pausePlayers.size() < hunters.size() + speedrunners.size()) {
                pausePlayers.remove(e.getPlayer().getName());
                if(pausePlayers.size() == 0) {
                    pausing.cancel();
                }
            } else {
                unpausePlayers.remove(e.getPlayer().getName());
                if(unpausePlayers.size() == 0) {
                    unpausing.cancel();
                }
            }
            playersMessage(ChatColor.AQUA + e.getPlayer().getName() + " left, so his voting is expired");
        }
    }
}