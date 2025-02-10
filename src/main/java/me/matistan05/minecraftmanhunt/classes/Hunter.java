package me.matistan05.minecraftmanhunt.classes;

public class Hunter extends ManhuntPlayer{
    // 0 = tracking nearest speedrunner, 1 = tracking specific speedrunner
    private int compassMode;
    // If compassMode is 1, this is the name of the speedrunner being tracked
    private String whichSpeedrunner;

    public Hunter(String name) {
        super(name);
        this.compassMode = 0;
        this.whichSpeedrunner = "";
    }

    public int getCompassMode() {
        return compassMode;
    }

    public void setCompassMode(int compassMode) {
        this.compassMode = compassMode;
    }

    public String getWhichSpeedrunner() {
        return whichSpeedrunner;
    }

    public void setWhichSpeedrunner(String whichSpeedrunner) {
        this.whichSpeedrunner = whichSpeedrunner;
    }
}
