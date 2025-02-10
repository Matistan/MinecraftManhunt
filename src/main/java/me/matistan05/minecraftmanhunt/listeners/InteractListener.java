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

    // left click - track nearest mode, right click - cycle through speedrunners

    @EventHandler
    public void InteractEvent(PlayerInteractEvent e) {
        if (!inGame && !waitingForStart) return;
        ItemStack item = e.getItem();
        if (item == null) return;
        if (!item.getItemMeta().hasLore()) return;
        if (item.getItemMeta().getLore().isEmpty()) return;
        if (!(item.getItemMeta().getLore().get(0).equals(ChatColor.BLUE + "This compass is to track speedrunners!"))) return;
        Player p = e.getPlayer();
        if (!isHunter(p.getName())) return;
        Action a = e.getAction();
        if (a == Action.LEFT_CLICK_AIR || a == Action.LEFT_CLICK_BLOCK) {
            if (!main.getConfig().getBoolean("trackNearestMode")) return;
            getHunter(p.getName()).setCompassMode(0);
        } else if (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {
            if (main.getConfig().getBoolean("compassMenu")) {
                if (getHunter(p.getName()).getCompassMode() == 0) {
                    getHunter(p.getName()).setCompassMode(1);
                } else {
                    Inventory compassInventory = Bukkit.createInventory(p, 54, ChatColor.RED + "Choose a speedrunner!");
                    for (int i = 0; i < speedrunners.size(); i++) {
                        OfflinePlayer player = Bukkit.getOfflinePlayer(speedrunners.get(i).getName());
                        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
                        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
                        headMeta.setOwningPlayer(player);
                        head.setItemMeta(headMeta);
                        compassInventory.setItem(i, head);
                    }
                    p.openInventory(compassInventory);
                }
            } else {
                if (getHunter(p.getName()).getCompassMode() == 0) {
                    getHunter(p.getName()).setCompassMode(1);
                } else {
                    for (int slot = 0; slot < speedrunners.size(); slot++) {
                        if (speedrunners.get(slot).getName().equals(getHunter(p.getName()).getWhichSpeedrunner())) {
                            if ((slot + 1) == speedrunners.size()) {
                                getHunter(p.getName()).setWhichSpeedrunner(speedrunners.get(0).getName());
                            } else {
                                getHunter(p.getName()).setWhichSpeedrunner(speedrunners.get(slot + 1).getName());
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
}
