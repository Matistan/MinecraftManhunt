package me.matistan05.minecraftmanhunt.listeners;

import me.matistan05.minecraftmanhunt.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import static me.matistan05.minecraftmanhunt.commands.ManhuntCommand.*;

public class MoveListener implements Listener {
    private final Main main;
    public MoveListener(Main main) {
        this.main = main;
        Bukkit.getPluginManager().registerEvents(this, main);
    }
    @EventHandler
    public void MoveEvent(PlayerMoveEvent e) {
        if(inGame && ((pausePlayers.size() == (hunters.size() + speedrunners.size()) && (hunters.contains(e.getPlayer().getName()) ||
                speedrunners.contains(e.getPlayer().getName()))) || (seconds != main.getConfig().getInt("headStartDuration") &&
                hunters.contains(e.getPlayer().getName())))) {
            if(e.getFrom().getX() != e.getTo().getX() || e.getFrom().getY() != e.getTo().getY() || e.getFrom().getZ() != e.getTo().getZ()) {
                e.getPlayer().teleport(e.getFrom());
            }
        }
    }
}