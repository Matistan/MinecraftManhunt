package me.matistan05.minecraftmanhunt.classes;

import org.bukkit.GameMode;
import org.bukkit.Location;

public class Speedrunner extends ManhuntPlayer {
    // Number of lives the speedrunner has
    private int lives;
    // Location in the overworld
    private Location locWorld;
    // Location in the nether
    private Location locNether;
    // Location in the end
    private Location locTheEnd;
    // Gamemode before start
    private GameMode gameMode;

    public Speedrunner(String name) {
        super(name);
        this.lives = 1;
        this.locWorld = null;
        this.locNether = null;
        this.locTheEnd = null;
        this.gameMode = GameMode.SURVIVAL;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public Location getLocWorld() {
        return locWorld;
    }

    public void setLocWorld(Location locWorld) {
        this.locWorld = locWorld;
    }

    public Location getLocNether() {
        return locNether;
    }

    public void setLocNether(Location locNether) {
        this.locNether = locNether;
    }

    public Location getLocTheEnd() {
        return locTheEnd;
    }

    public void setLocTheEnd(Location locTheEnd) {
        this.locTheEnd = locTheEnd;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }
}
