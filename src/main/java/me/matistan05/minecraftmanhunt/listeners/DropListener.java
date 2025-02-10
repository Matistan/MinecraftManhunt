package me.matistan05.minecraftmanhunt.listeners;

import me.matistan05.minecraftmanhunt.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import static me.matistan05.minecraftmanhunt.commands.ManhuntCommand.*;

public class DropListener implements Listener {
    public DropListener(Main main) {

        Bukkit.getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void DropEvent(PlayerDropItemEvent e) {
        if ((inGame || waitingForStart) && isHunter(e.getPlayer().getName()) && isCompass(e.getItemDrop().getItemStack())) {
            e.setCancelled(true);
        }
    }
}
