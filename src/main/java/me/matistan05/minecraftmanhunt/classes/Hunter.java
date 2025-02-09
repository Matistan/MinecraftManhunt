package me.matistan05.minecraftmanhunt.classes;

public class Hunter {
    // Hunter's name
    private final String name;
    // Whether the hunter was an operator before the game started
    private final boolean op;
    // 0 = tracking nearest speedrunner, 1 = tracking specific speedrunner
    private int compassMode;
    // If compassMode is 1, this is the name of the speedrunner being tracked
    private int whichSpeedrunner;

    public Hunter(String name) {
        this.name = name;
        this.op = false;
        this.compassMode = 0;
        this.whichSpeedrunner = 0;
    }
}
