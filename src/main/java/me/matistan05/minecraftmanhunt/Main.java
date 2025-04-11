package me.matistan05.minecraftmanhunt;

import me.matistan05.minecraftmanhunt.commands.ManhuntCommand;
import me.matistan05.minecraftmanhunt.commands.ManhuntCompleter;
import me.matistan05.minecraftmanhunt.listeners.*;
import org.bukkit.plugin.java.JavaPlugin;

import static me.matistan05.minecraftmanhunt.commands.ManhuntCommand.inGame;
import static me.matistan05.minecraftmanhunt.commands.ManhuntCommand.reset;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginCommand("manhunt").setExecutor(new ManhuntCommand(this));
        getCommand("manhunt").setTabCompleter(new ManhuntCompleter(this));
        new DeathListener(this);
        new AdvancementListener(this);
        new InteractListener(this);
        new RespawnListener(this);
        new DropListener(this);
        new MoveListener(this);
        new MenuListener(this);
        new DisconnectListener(this);
        new DamageListener(this);
        new Metrics(this, 21908);
        JavaPlugin.getPlugin(this.getClass()).getLogger().info("*********************************************************\n" +
                                                                    "Thank you for using this plugin! <3\n" +
                                                                    "Author: Matistan\n" +
                                                                    "If you enjoy this plugin, please rate it on spigotmc.org:\n" +
                                                                    "https://www.spigotmc.org/resources/manhunt.109010/\n" +
                                                                    "*********************************************************");
    }

    @Override
    public void onDisable() {
        if (inGame) {
            reset();
        }
    }
}
