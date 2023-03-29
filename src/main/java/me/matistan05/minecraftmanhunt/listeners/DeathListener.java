package me.matistan05.minecraftmanhunt.listeners;

import me.matistan05.minecraftmanhunt.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import static me.matistan05.minecraftmanhunt.commands.ManhuntCommand.*;

public class DeathListener implements Listener {
    public DeathListener(Main main) {
        Bukkit.getPluginManager().registerEvents(this, main);
    }
    @EventHandler
    public void DeathEvent(PlayerDeathEvent e) {
        if(!inGame) {return;}
        Player p = e.getEntity();
        if(hunters.contains(p.getName())) {
            for(int i = 0; i < e.getDrops().size(); i++) {
                if(e.getDrops().get(i).hasItemMeta()) {
                    if(e.getDrops().get(i).getItemMeta().getLore().get(0).equals(ChatColor.BLUE + "This compass is to track speedrunners!")) {
                        e.getDrops().remove(i);
                        break;
                    }
                }
            }
        } else if(speedrunners.contains(p.getName())) {
            Player tar = Bukkit.getPlayerExact(p.getName());
            tar.setOp(sOps.get(speedrunners.indexOf(p.getName())));
            sOps.remove(speedrunners.indexOf(p.getName()));
            if(speedrunners.size() == 1) {
                playersMessage(ChatColor.DARK_RED + "Last speedrunner " + p.getName() + " died!");
                playersMessage(ChatColor.DARK_RED + "Hunters won!");
                for (String hunter : hunters) {
                    Player player = Bukkit.getPlayerExact(hunter);
                    if(player != null) {
                        player.getInventory().clear(compassSlot(p));
                    }
                }
                reset();
            }
            speedrunners.remove(p.getName());
            playersMessage(ChatColor.DARK_RED + "Speedrunner " + p.getName() + " died!");
            playersMessage(ChatColor.DARK_RED + "There are " + speedrunners.size() + " speedrunner" + (speedrunners.size() == 1 ? "" : "s") + " left alive!");
        }
    }
}