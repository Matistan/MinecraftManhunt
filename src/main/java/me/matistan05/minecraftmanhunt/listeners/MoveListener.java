package me.matistan05.minecraftmanhunt.listeners;

import me.matistan05.minecraftmanhunt.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import static me.matistan05.minecraftmanhunt.commands.ManhuntCommand.*;

public class MoveListener implements Listener {
    public MoveListener(Main main) {
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void MoveEvent(PlayerMoveEvent e) {
        if ((paused && isInGame(e.getPlayer().getName())) || (waitingForStart && isHunter(e.getPlayer().getName()))) {
            if (e.getFrom().getX() != e.getTo().getX() ||
                    e.getFrom().getY() != e.getTo().getY() ||
                    e.getFrom().getZ() != e.getTo().getZ()) {
                e.getPlayer().teleport(e.getFrom());
            }
        }
    }
}
