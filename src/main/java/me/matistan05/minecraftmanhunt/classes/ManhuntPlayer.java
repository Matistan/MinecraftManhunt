package me.matistan05.minecraftmanhunt.classes;

public abstract class ManhuntPlayer {
    final String name;
    boolean op;

    public ManhuntPlayer(String name) {
        this.name = name;
        this.op = false;
    }

    public String getName() {
        return name;
    }

    public boolean isOp() {
        return op;
    }

    public void setOp(boolean op) {
        this.op = op;
    }
}
