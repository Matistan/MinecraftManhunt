package me.matistan05.minecraftmanhunt.listeners;

import me.matistan05.minecraftmanhunt.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import static me.matistan05.minecraftmanhunt.commands.ManhuntCommand.*;

public class RespawnListener implements Listener {
    private final Main main;
    public RespawnListener(Main main) {
        this.main = main;
        Bukkit.getPluginManager().registerEvents(this, main);
    }
    @EventHandler
    public void RespawnEvent(PlayerRespawnEvent e) {
        if(inGame && hunters.contains(e.getPlayer().getName())) {
            e.getPlayer().getInventory().addItem(kompas);
        }
    }
}