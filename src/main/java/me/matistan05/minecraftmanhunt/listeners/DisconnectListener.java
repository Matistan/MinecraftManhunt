package me.matistan05.minecraftmanhunt.listeners;

import me.matistan05.minecraftmanhunt.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
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
        Player p = e.getPlayer();
        if (inGame && isInGame(p.getName())) {
            if (pausePlayers.contains(p.getName()) && !paused) {
                pausePlayers.remove(p.getName());
                if (pausePlayers.isEmpty()) {
                    pausing.cancel();
                }
                playersMessage(ChatColor.AQUA + p.getName() + " left, so his voting is expired");
            } else if (unpausePlayers.contains(p.getName()) && paused) {
                unpausePlayers.remove(p.getName());
                if (unpausePlayers.isEmpty()) {
                    unpausing.cancel();
                }
                playersMessage(ChatColor.AQUA + p.getName() + " left, so his voting is expired");
            }
        }
    }
}
