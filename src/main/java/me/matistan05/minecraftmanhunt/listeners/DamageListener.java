package me.matistan05.minecraftmanhunt.listeners;

import me.matistan05.minecraftmanhunt.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import static me.matistan05.minecraftmanhunt.commands.ManhuntCommand.*;

public class DamageListener implements Listener {
    public DamageListener(Main main) {
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void DamageEvent(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        if (paused && (isInGame(player.getName())) || waitingForStart && isHunter(player.getName())) {
            e.setCancelled(true);
        }
    }
}
