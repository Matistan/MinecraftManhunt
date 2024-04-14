package me.matistan05.minecraftmanhunt.listeners;

import me.matistan05.minecraftmanhunt.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import static me.matistan05.minecraftmanhunt.commands.ManhuntCommand.*;

public class DamageListener implements Listener {
    private final Main main;

    public DamageListener(Main main) {
        this.main = main;
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void DamageEvent(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        if (inGame && ((pausePlayers.size() == (hunters.size() + speedrunners.size()) && (hunters.contains(player.getName()) || speedrunners.contains(player.getName()))) ||
                (seconds != main.getConfig().getInt("headStartDuration") && hunters.contains(player.getName())))) {
            e.setCancelled(true);
        }
    }

}
