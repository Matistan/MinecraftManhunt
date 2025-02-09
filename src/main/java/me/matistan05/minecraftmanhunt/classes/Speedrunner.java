package me.matistan05.minecraftmanhunt.classes;

import org.bukkit.Location;

public class Speedrunner {
    // Speedrunner's name
    private final String name;
    // Number of lives the speedrunner has
    private int lives;
    // Location in the overworld
    private Location locWorld;
    // Location in the nether
    private Location locNether;
    // Location in the end
    private Location locTheEnd;

    public Speedrunner(String name) {
        this.name = name;
        this.lives = 1;
        this.locWorld = null;
        this.locNether = null;
        this.locTheEnd = null;
    }
}
