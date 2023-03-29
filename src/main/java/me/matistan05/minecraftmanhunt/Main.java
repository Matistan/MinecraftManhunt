package me.matistan05.minecraftmanhunt;

import me.matistan05.minecraftmanhunt.commands.ManhuntCommand;
import me.matistan05.minecraftmanhunt.commands.ManhuntCompleter;
import me.matistan05.minecraftmanhunt.listeners.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginCommand("manhunt").setExecutor(new ManhuntCommand(this));
        getCommand("manhunt").setTabCompleter(new ManhuntCompleter());
        new DeathListener(this);
        new AdvancementListener(this);
        new InterractListener(this);
        new RespawnListener(this);
        new DropListener(this);
        new MoveListener(this);
    }
}