package me.matistan05.minecraftmanhunt.commands;

import me.matistan05.minecraftmanhunt.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static me.matistan05.minecraftmanhunt.commands.ManhuntCommand.hunters;
import static me.matistan05.minecraftmanhunt.commands.ManhuntCommand.speedrunners;

public class ManhuntCompleter implements TabCompleter {

    private static Main main;

    public ManhuntCompleter(Main main) {
        ManhuntCompleter.main = main;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> list = new LinkedList<>();
        if (args.length == 1) {
            if (startsWith("add", args[0])) {
                list.add("add");
            }
            if (startsWith("remove", args[0])) {
                list.add("remove");
            }
            if (startsWith("start", args[0])) {
                list.add("start");
            }
            if (startsWith("pause", args[0])) {
                list.add("pause");
            }
            if (startsWith("unpause", args[0])) {
                list.add("unpause");
            }
            if (startsWith("reset", args[0])) {
                list.add("reset");
            }
            if (startsWith("list", args[0])) {
                list.add("list");
            }
            if (startsWith("help", args[0])) {
                list.add("help");
            }
            if (startsWith("rules", args[0])) {
                list.add("rules");
            }
        } else if (args.length > 1 && args[0].equals("rules")) {
            if (args.length == 2) {
                list = main.getConfig().getKeys(false).stream().filter(s -> startsWith(s, args[1])).collect(Collectors.toList());
            } else if (args.length == 3 && main.getConfig().contains(args[1]) && !args[1].equals("speedrunnersLives") && !args[1].equals("headStartDuration")) {
                if (startsWith("true", args[2])) {
                    list.add("true");
                }
                if (startsWith("false", args[2])) {
                    list.add("false");
                }
            }
        } else if (args.length == 2 && args[0].equals("add")) {
            if (startsWith("speedrunner", args[1])) {
                list.add("speedrunner");
            }
            if (startsWith("hunter", args[1])) {
                list.add("hunter");
            }
        }
        if (args.length > 2 && args[0].equals("add")) {
            if (args.length > 3 && args[1].equals("@a")) {
                return list;
            }
            if (args.length == 3 && startsWith("@a", args[2])) {
                list.add("@a");
            }
            List<String> notForTab = new LinkedList<>();
            for (int i = 2; i < args.length - 1; i++) {
                Player player = Bukkit.getPlayerExact(args[i]);
                if (player == null) continue;
                notForTab.add(player.getName());
            }
            if (args[1].equals("speedrunner")) {
                notForTab.addAll(speedrunners);
            }
            if (args[1].equals("hunter")) {
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
        if (args.length > 1 && args[0].equals("remove")) {
            if (args.length > 2 && args[1].equals("@a")) {
                return list;
            }
            if (args.length == 2 && startsWith("@a", args[1])) {
                list.add("@a");
            }
            List<String> notForTab = new LinkedList<>();
            for (int i = 1; i < args.length - 1; i++) {
                Player player = Bukkit.getPlayerExact(args[i]);
                if (player == null) continue;
                notForTab.add(player.getName());
            }
            List<String> players = new LinkedList<>();
            for (String s : speedrunners) {
                Player player = Bukkit.getPlayerExact(s);
                if (player == null) continue;
                players.add(player.getName());
            }
            for (String h : hunters) {
                Player player = Bukkit.getPlayerExact(h);
                if (player == null) continue;
                players.add(player.getName());
            }
            for (String argument : notForTab) {
                players.remove(argument);
            }
            for (String player : players) {
                if (startsWith(player, args[args.length - 1])) {
                    list.add(player);
                }
            }
        }
        return list;
    }

    private boolean startsWith(String a, String b) {
        if (b.length() <= a.length()) {
            for (int i = 0; i < b.length(); i++) {
                if (b.toLowerCase().charAt(i) != a.toLowerCase().charAt(i)) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }
}