package me.matistan05.minecraftmanhunt.listeners;

import me.matistan05.minecraftmanhunt.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.SkullMeta;

import static me.matistan05.minecraftmanhunt.commands.ManhuntCommand.*;

public class MenuListener implements Listener {
    private final Main main;
    public MenuListener(Main main) {
        this.main = main;
        Bukkit.getPluginManager().registerEvents(this, main);
    }
    @EventHandler
    public void MenuClickEvent(InventoryClickEvent e) {
        if(!inGame || seconds != main.getConfig().getInt("headStartDuration")){return;}
        if(e.getView().getTitle().equals(ChatColor.RED + "Choose a speedrunner!")) {
            if(e.getCurrentItem() == null) {return;}
            if(e.getCurrentItem().getType() == Material.PLAYER_HEAD) {
                SkullMeta headMeta = (SkullMeta) e.getCurrentItem().getItemMeta();
                whichSpeedrunner.set(hunters.indexOf(e.getWhoClicked().getName()), headMeta.getOwningPlayer().getName());
            }
            e.setCancelled(true);
        }
    }
}
