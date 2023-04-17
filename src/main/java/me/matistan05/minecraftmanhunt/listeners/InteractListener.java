package me.matistan05.minecraftmanhunt.listeners;

import me.matistan05.minecraftmanhunt.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import static me.matistan05.minecraftmanhunt.commands.ManhuntCommand.*;

public class InteractListener implements Listener {
    private final Main main;
    public InteractListener(Main main) {
        this.main = main;
        Bukkit.getPluginManager().registerEvents(this, main);
    }
    @EventHandler
    public void InteractEvent(PlayerInteractEvent e) {
        if(!inGame || seconds != main.getConfig().getInt("headStartDuration")){return;}
        ItemStack item = e.getItem();
        if(item == null) {return;}
        if(!item.getItemMeta().hasLore()) {return;}
        if(!(item.getItemMeta().getLore().get(0).equals(ChatColor.BLUE + "This compass is to track speedrunners!"))) {return;}
        Player p = e.getPlayer();
        if(!hunters.contains(p.getName())) {return;}
        Action a = e.getAction();
        if(main.getConfig().getBoolean("trackNearestMode") || a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {
            int hunterIndex = hunters.indexOf(p.getName());
            if(a == Action.LEFT_CLICK_AIR || a == Action.LEFT_CLICK_BLOCK) {
                if (compassMode.get(hunterIndex).equals("1")) {
                    compassMode.set(hunterIndex, "0");
                }
                return;
            }
            if(a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {
                if(main.getConfig().getBoolean("compassMenu")) {
                    if(compassMode.get(hunterIndex).equals("0")) {
                        compassMode.set(hunterIndex, "1");
                    } else {
                        Inventory compassInventory = Bukkit.createInventory(p, 54, ChatColor.RED + "Choose a speedrunner!");
                        for(int i = 0; i < speedrunners.size(); i++) {
                            OfflinePlayer player = Bukkit.getOfflinePlayer(speedrunners.get(i));
                            ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
                            SkullMeta headMeta = (SkullMeta) head.getItemMeta();
                            headMeta.setOwningPlayer(player);
                            head.setItemMeta(headMeta);
                            compassInventory.setItem(i, head);
                        }
                        p.openInventory(compassInventory);
                    }
                } else {
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
}