package me.matistan05.minecraftmanhunt.listeners;

import me.matistan05.minecraftmanhunt.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        if(inGame) {
            if(hunters.contains(e.getPlayer().getName())) {
                if(!e.getItemDrop().getItemStack().getItemMeta().hasLore()) {return;}
                if(e.getItemDrop().getItemStack().getItemMeta().getLore().get(0).equals(ChatColor.BLUE + "This compass is to track speedrunners!")) {
                    e.setCancelled(true);
                }
            }
        }
    }
}