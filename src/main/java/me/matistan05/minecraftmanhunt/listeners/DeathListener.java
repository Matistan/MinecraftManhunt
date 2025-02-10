package me.matistan05.minecraftmanhunt.listeners;

import me.matistan05.minecraftmanhunt.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import static me.matistan05.minecraftmanhunt.commands.ManhuntCommand.*;

public class DeathListener implements Listener {
    Main main;

    public DeathListener(Main main) {
        this.main = main;
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void DeathEvent(PlayerDeathEvent e) {
        if (!inGame && !waitingForStart) return;
        Player p = e.getEntity();
        if (isHunter(p.getName())) {
            for (int i = 0; i < e.getDrops().size(); i++) {
                if (e.getDrops().get(i).hasItemMeta()) {
                    if (!e.getDrops().get(i).getItemMeta().hasLore()) continue;
                    if (e.getDrops().get(i).getItemMeta().getLore().isEmpty()) continue;
                    if (e.getDrops().get(i).getItemMeta().getLore().get(0).equals(ChatColor.BLUE + "This compass is to track speedrunners!")) {
                        e.getDrops().remove(i);
                        break;
                    }
                }
            }
        } else if (isSpeedrunner(p.getName())) {
            getSpeedrunner(p.getName()).setLives(getSpeedrunner(p.getName()).getLives() - 1);
            if (getSpeedrunner(p.getName()).getLives() >= 1) {
                playersMessage(ChatColor.DARK_RED + "Speedrunner " + p.getName() + " died and has " + getSpeedrunner(p.getName()).getLives() + " live" + (getSpeedrunner(p.getName()).getLives() == 1 ? "" : "s") + " left!");
            } else {
                if (speedrunners.size() == 1) {
                    playersMessage(ChatColor.DARK_RED + "Last speedrunner " + p.getName() + " died!");
                    playersMessage(ChatColor.DARK_RED + "Hunters won!");
                    reset();
                } else {
                    if (main.getConfig().getBoolean("spectatorAfterDeath")) {
                        p.setGameMode(GameMode.SPECTATOR);
//                        spectators.add(p.getName());
                    }
                    removePlayer(p.getName());
                    playersMessage(ChatColor.DARK_RED + "Speedrunner " + p.getName() + " died!");
                    playersMessage(ChatColor.DARK_RED + "There " + (speedrunners.size() == 1 ? "is" : "are") + " " + speedrunners.size() + " speedrunner" + (speedrunners.size() == 1 ? "" : "s") + " left alive!");
                }
            }
        }
    }
}
