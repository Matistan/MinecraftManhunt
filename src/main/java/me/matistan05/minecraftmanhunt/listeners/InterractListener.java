package me.matistan05.minecraftmanhunt.listeners;

import me.matistan05.minecraftmanhunt.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import static me.matistan05.minecraftmanhunt.commands.ManhuntCommand.*;

public class InterractListener implements Listener {
    private final Main main;
    public InterractListener(Main main) {
        this.main = main;
        Bukkit.getPluginManager().registerEvents(this, main);
    }
    @EventHandler
    public void InteractEvent(PlayerInteractEvent e) {
        if(main.getConfig().getBoolean("trackNearestMode")) {
            if(!inGame || seconds != main.getConfig().getInt("headStartDuration")){return;}
            Player p = e.getPlayer();
            if(!hunters.contains(p.getName())) {return;}
            ItemStack item = e.getItem();
            if(!(item.getItemMeta().getLore().get(0).equals(ChatColor.BLUE + "This compass is to track speedrunners!"))) {return;}
            Action a = e.getAction();
            int hunterIndex = hunters.indexOf(p.getName());
            if(a == Action.LEFT_CLICK_AIR || a == Action.LEFT_CLICK_BLOCK) {
                if (compassMode.get(hunterIndex).equals("1")) {
                    compassMode.set(hunterIndex, "0");
                }
                return;
            }
            if(a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {
                if(compassMode.get(hunterIndex).equals("0")) {
                    compassMode.set(hunterIndex, "1");
                } else {
                    int slot = speedrunners.indexOf(whichSpeedrunner.get(hunterIndex));
                    if((slot + 1) == speedrunners.size()) {
                        whichSpeedrunner.set(hunterIndex, speedrunners.get(0));
                    } else {
                        whichSpeedrunner.set(hunterIndex, speedrunners.get(slot + 1));
                    }
                }
            }
        }
    }
}