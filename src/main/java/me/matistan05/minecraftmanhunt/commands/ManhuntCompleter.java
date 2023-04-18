package me.matistan05.minecraftmanhunt.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

import static me.matistan05.minecraftmanhunt.commands.ManhuntCommand.hunters;
import static me.matistan05.minecraftmanhunt.commands.ManhuntCommand.speedrunners;

public class ManhuntCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> list = new LinkedList<>();
        if(args.length == 1) {
            if(startsWith("add", args[0])) {
                list.add("add");
            }
            if(startsWith("remove", args[0])) {
                list.add("remove");
            }
            if(startsWith("start", args[0])) {
                list.add("start");
            }
            if(startsWith("pause", args[0])) {
                list.add("pause");
            }
            if(startsWith("unpause", args[0])) {
                list.add("unpause");
            }
            if(startsWith("reset", args[0])) {
                list.add("reset");
            }
            if(startsWith("list", args[0])) {
                list.add("list");
            }
            if(startsWith("help", args[0])) {
                list.add("help");
            }
        } else if(args.length == 2 && args[0].equals("add")) {
            if(startsWith("speedrunner", args[1])) {
                list.add("speedrunner");
            }
            if(startsWith("hunter", args[1])) {
                list.add("hunter");
            }
        }
        if(args.length > 2 && args[0].equals("add")) {
            List<String> notForTab = new LinkedList<>();
            for(int i = 2; i < args.length - 1; i++) {
                Player player = Bukkit.getPlayerExact(args[i]);
                if(player == null) {continue;}
                notForTab.add(player.getName());
            }
            if(args[1].equals("speedrunner")) {
                notForTab.addAll(speedrunners);
            }
            if(args[1].equals("hunter")) {
                notForTab.addAll(hunters);
            }
            List<Player> players = new LinkedList<>(Bukkit.getOnlinePlayers());
            for (String argument : notForTab) {
                players.remove(Bukkit.getPlayerExact(argument));
            }
            for (Player player : players) {
                if (startsWith(player.getName(), args[args.length - 1])) {
                    list.add(player.getName());
                }
            }
        }
        return list;
    }
    private boolean startsWith(String a, String b) {
        if(b.length() <= a.length()) {
            for(int i = 0; i < b.length(); i++) {
                if(b.toLowerCase().charAt(i) != a.toLowerCase().charAt(i)) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }
}