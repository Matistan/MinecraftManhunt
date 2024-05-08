package me.matistan05.minecraftmanhunt.commands;

import me.matistan05.minecraftmanhunt.Main;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class ManhuntCommand implements CommandExecutor {
    private static Main main;
    public static List<String> hunters = new LinkedList<>();
    public static List<String> speedrunners = new LinkedList<>();
    public static List<String> compassMode = new LinkedList<>();
    public static List<String> whichSpeedrunner = new LinkedList<>();
    public static List<Location> locWorld = new LinkedList<>();
    public static List<Location> locNether = new LinkedList<>();
    public static List<Location> locTheEnd = new LinkedList<>();
    public static List<Boolean> hOps = new LinkedList<>();
    public static List<Boolean> sOps = new LinkedList<>();
    public static List<Integer> lives = new LinkedList<>();
    public static int seconds;
    public static boolean inGame = false;
    private static BukkitTask starting;
    public static BukkitTask game;
    public static BukkitTask pausing, unpausing;
    public static ItemStack compass;
    private double finalDistance, distance;
    private Player hunter, speedrunner, target;
    public static List<String> pausePlayers = new LinkedList<>();
    public static List<String> unpausePlayers = new LinkedList<>();
    public static Player tpPlayer;

    public ManhuntCommand(Main main) {
        ManhuntCommand.main = main;
    }

    @Override
    public boolean onCommand(CommandSender p, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            p.sendMessage(ChatColor.RED + "You must type an argument. For help, type: /manhunt help");
            return true;
        }
        if (args[0].equals("help")) {
            if (!p.hasPermission("manhunt.help") && main.getConfig().getBoolean("usePermissions")) {
                p.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }
            if (args.length != 1) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /manhunt help");
                return true;
            }
            p.sendMessage(ChatColor.GREEN + "------- " + ChatColor.WHITE + " Minecraft Manhunt " + ChatColor.GREEN + "----------");
            p.sendMessage(ChatColor.BLUE + "Here is a list of manhunt commands:");
            p.sendMessage(ChatColor.YELLOW + "/manhunt add <role> <player> <player> ... " + ChatColor.AQUA + "- adds players to a game with roles");
            p.sendMessage(ChatColor.YELLOW + "/manhunt add <role> @a " + ChatColor.AQUA + "- adds all players with roles");
            p.sendMessage(ChatColor.YELLOW + "/manhunt remove <player> <player> ..." + ChatColor.AQUA + "- removes players from a  game");
            p.sendMessage(ChatColor.YELLOW + "/manhunt remove @a " + ChatColor.AQUA + "- removes all players");
            p.sendMessage(ChatColor.YELLOW + "/manhunt start " + ChatColor.AQUA + "- starts a manhunt game");
            p.sendMessage(ChatColor.YELLOW + "/manhunt reset " + ChatColor.AQUA + "- resets a manhunt game");
            p.sendMessage(ChatColor.YELLOW + "/manhunt pause " + ChatColor.AQUA + "- pauses a manhunt game");
            p.sendMessage(ChatColor.YELLOW + "/manhunt unpause " + ChatColor.AQUA + "- resumes a manhunt game");
            p.sendMessage(ChatColor.YELLOW + "/manhunt list " + ChatColor.AQUA + "- shows a list of players in a manhunt game with their roles");
            p.sendMessage(ChatColor.YELLOW + "/manhunt rules <rule> value(optional) " + ChatColor.AQUA + "- changes some additional rules of the game (in config.yml)");
            p.sendMessage(ChatColor.YELLOW + "/manhunt help " + ChatColor.AQUA + "- shows a list of manhunt commands");
            p.sendMessage(ChatColor.GREEN + "----------------------------------");
            return true;
        }
        if (args[0].equals("rules")) {
            if (!p.hasPermission("manhunt.rules") && main.getConfig().getBoolean("usePermissions")) {
                p.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }
            if (args.length != 3 && args.length != 2) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /manhunt help");
                return true;
            }
            if (!main.getConfig().contains(args[1])) {
                p.sendMessage(ChatColor.RED + "There is no such rule. See the config.yml file for more information.");
                return true;
            }
            if (args.length == 2) {
                p.sendMessage(ChatColor.AQUA + "The value of the rule " + args[1] + " is: " + main.getConfig().get(args[1]));
                return true;
            }
            if (args[1].equals("headStartDuration") || args[1].equals("speedrunnersLives")) {
                try {
                    main.getConfig().set(args[1], Integer.parseInt(args[2]));
                } catch (NumberFormatException e) {
                    p.sendMessage(ChatColor.RED + "The value must be a number!");
                    return true;
                }
            } else {
                if (!args[2].equals("true") && !args[2].equals("false")) {
                    p.sendMessage(ChatColor.RED + "The value must be true or false!");
                    return true;
                }
                main.getConfig().set(args[1], Boolean.parseBoolean(args[2]));
            }
            main.saveConfig();
            p.sendMessage(ChatColor.AQUA + "The value of the rule " + args[1] + " has been changed to: " + args[2]);
            return true;
        }
        if (args[0].equals("add")) {
            if (!p.hasPermission("manhunt.add") && main.getConfig().getBoolean("usePermissions")) {
                p.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }
            if (args.length < 3) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /manhunt help");
                return true;
            }
            if (inGame) {
                p.sendMessage(ChatColor.RED + "The game has already started!");
                return true;
            }
            int count = 0;
            if (args[1].equals("speedrunner")) {
                if (args[2].equals("@a")) {
                    if (args.length != 3) {
                        p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /manhunt help");
                        return true;
                    }
                    for (Player target : Bukkit.getOnlinePlayers()) {
                        if (speedrunners.contains(target.getName())) continue;
                        hunters.remove(target.getName());
                        speedrunners.add(target.getName());
                        count++;
                    }
                    if (count > 0) {
                        p.sendMessage(ChatColor.AQUA + "Successfully added " + count + " new speedrunner" + (count == 1 ? "" : "s") + " to the game!");
                    } else {
                        p.sendMessage(ChatColor.RED + "No speedrunner was added!");
                    }
                    return true;
                }
                for (int i = 2; i < args.length; i++) {
                    target = Bukkit.getPlayerExact(args[i]);
                    if (target == null || speedrunners.contains(target.getName())) continue;
                    hunters.remove(target.getName());
                    speedrunners.add(target.getName());
                    count++;
                }
                if (count > 0) {
                    p.sendMessage(ChatColor.AQUA + "Successfully added " + count + " new speedrunner" + (count == 1 ? "" : "s") + " to the game!");
                } else {
                    p.sendMessage(ChatColor.RED + "Could not add " + (args.length == 3 ? "this player!" : "these players!"));
                }
                return true;
            }
            if (args[1].equals("hunter")) {
                if (args[2].equals("@a")) {
                    if (args.length != 3) {
                        p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /manhunt help");
                        return true;
                    }
                    for (Player target : Bukkit.getOnlinePlayers()) {
                        if (hunters.contains(target.getName())) continue;
                        speedrunners.remove(target.getName());
                        hunters.add(target.getName());
                        count++;
                    }
                    if (count > 0) {
                        p.sendMessage(ChatColor.AQUA + "Successfully added " + count + " new hunter" + (count == 1 ? "" : "s") + " to the game!");
                    } else {
                        p.sendMessage(ChatColor.RED + "No hunter was added!");
                    }
                    return true;
                }
                for (int i = 2; i < args.length; i++) {
                    target = Bukkit.getPlayerExact(args[i]);
                    if (target == null || hunters.contains(target.getName())) continue;
                    speedrunners.remove(target.getName());
                    hunters.add(target.getName());
                    count++;
                }
                if (count > 0) {
                    p.sendMessage(ChatColor.AQUA + "Successfully added " + count + " new hunter" + (count == 1 ? "" : "s") + " to the game!");
                } else {
                    p.sendMessage(ChatColor.RED + "Could not add " + (args.length == 3 ? "this player!" : "these players!"));
                }
                return true;
            }
            p.sendMessage(ChatColor.RED + "Wrong manhunt role. For help, type: /manhunt help");
            return true;
        }
        if (args[0].equals("remove")) {
            if (!p.hasPermission("manhunt.remove") && main.getConfig().getBoolean("usePermissions")) {
                p.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }
            if (args.length < 2) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /manhunt help");
                return true;
            }
            int count = 0;
            if (args[1].equals("@a")) {
                if (args.length != 2) {
                    p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /manhunt help");
                    return true;
                }
                for (Player target : Bukkit.getOnlinePlayers()) {
                    if ((!speedrunners.contains(target.getName()) && !hunters.contains(target.getName())) ||
                            (inGame && (speedrunners.contains(target.getName()) && speedrunners.size() == 1 || hunters.contains(target.getName()) &&
                                    hunters.size() == 1))) continue;
                    removePlayer(target.getName());
                    count++;
                }
                if (count > 0) {
                    p.sendMessage(ChatColor.AQUA + "Successfully removed " + count + " player" + (count == 1 ? "" : "s") + " from the game!");
                } else {
                    p.sendMessage(ChatColor.RED + "No player was removed!");
                }
                return true;
            }
            for (int i = 1; i < args.length; i++) {
                target = Bukkit.getPlayerExact(args[i]);
                if (target == null || (inGame && (speedrunners.contains(target.getName()) && speedrunners.size() == 1 || hunters.contains(target.getName()) &&
                        hunters.size() == 1)) || (!speedrunners.contains(target.getName()) && !hunters.contains(target.getName()))) continue;
                if (hunters.contains(target.getName())) {
                    hunters.remove(target.getName());
                } else {
                    speedrunners.remove(target.getName());
                }
                count++;
            }
            if (count > 0) {
                p.sendMessage(ChatColor.AQUA + "Successfully removed " + count + " player" + (count == 1 ? "" : "s") + " from the game!");
            } else {
                p.sendMessage(ChatColor.RED + "Could not remove " + (args.length == 2 ? "this player!" : "these players!"));
            }
            return true;
        }
        if (args[0].equals("start")) {
            if (!p.hasPermission("manhunt.start") && main.getConfig().getBoolean("usePermissions")) {
                p.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }
            if (args.length != 1) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /manhunt help");
                return true;
            }
            if (p instanceof Player && !(hunters.contains(p.getName()) || speedrunners.contains(p.getName()))) {
                p.sendMessage(ChatColor.RED + "You are not in a manhunt game!");
                return true;
            }
            if (speedrunners.size() + hunters.size() == 0) {
                p.sendMessage(ChatColor.RED + "There are no hunters and speedrunners!");
                return true;
            }
            if (speedrunners.isEmpty()) {
                p.sendMessage(ChatColor.RED + "There are no speedrunners!");
                return true;
            }
            if (hunters.isEmpty()) {
                p.sendMessage(ChatColor.RED + "There are no hunters!");
                return true;
            }
            if (inGame) {
                p.sendMessage(ChatColor.YELLOW + "The game has already started!");
                return true;
            }
            for (String v : speedrunners) {
                Player player = Bukkit.getPlayerExact(v);
                if (player == null) {
                    p.sendMessage(ChatColor.RED + "Someone from your game is offline!");
                    return true;
                }
            }
            for (String v : hunters) {
                Player player = Bukkit.getPlayerExact(v);
                if (player == null) {
                    p.sendMessage(ChatColor.RED + "Someone from your game is offline!");
                    return true;
                }
            }
            if (main.getConfig().getBoolean("timeSetDayOnStart")) {
                p.getServer().getWorlds().get(0).setTime(0);
            }
            if (main.getConfig().getBoolean("weatherClearOnStart")) {
                p.getServer().getWorlds().get(0).setStorm(false);
            }
            seconds = Math.max(main.getConfig().getInt("headStartDuration"), 0);
            ItemStack item = new ItemStack(Material.COMPASS, 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "Tracking: " + ChatColor.GREEN + "nearest speedrunner");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.BLUE + "This compass is to track speedrunners!");
            meta.setLore(lore);
            item.setItemMeta(meta);
            compass = item;
            Player tar;
            if (main.getConfig().getBoolean("teleport")) {
                tpPlayer = null;
                for (String speed : speedrunners) {
                    Player pl = Bukkit.getPlayerExact(speed);
                    if (pl != null) {
                        tpPlayer = pl;
                        break;
                    }
                }
                if (tpPlayer == null) {
                    for (String hun : hunters) {
                        Player pl = Bukkit.getPlayerExact(hun);
                        if (pl != null) {
                            tpPlayer = pl;
                            break;
                        }
                    }
                }
            }
            for (String value : hunters) {
                unpausePlayers.add(value);
                tar = Bukkit.getPlayerExact(value);
                if (tar != null) {
                    if (main.getConfig().getBoolean("clearInventories")) {
                        tar.getInventory().clear();
                    }
                    tar.getInventory().addItem(compass);
                    tar.setGameMode(GameMode.SURVIVAL);
                    if (main.getConfig().getBoolean("takeAwayOps")) {
                        hOps.add(tar.isOp());
                        tar.setOp(false);
                    }
                    if (main.getConfig().getBoolean("teleport")) {
                        tar.teleport(tpPlayer);
                    }
                    whichSpeedrunner.add(speedrunners.get(0));
                    compassMode.add("1");
                    tar.setHealth(20);
                    tar.setFoodLevel(20);
                    tar.setSaturation(5);
                    Iterator<Advancement> advancements = Bukkit.getServer().advancementIterator();
                    while (advancements.hasNext()) {
                        AdvancementProgress progress = tar.getAdvancementProgress(advancements.next());
                        for (String s : progress.getAwardedCriteria())
                            progress.revokeCriteria(s);
                    }
                }
            }
            for (String value : speedrunners) {
                unpausePlayers.add(value);
                Player player = Bukkit.getPlayerExact(value);
                if (player != null) {
                    Location loc = player.getLocation();
                    locWorld.add(loc);
                    locNether.add(null);
                    locTheEnd.add(null);
                    lives.add(Math.max(main.getConfig().getInt("speedrunnersLives"), 1));
                    player.setGameMode(GameMode.SURVIVAL);
                    if (main.getConfig().getBoolean("takeAwayOps")) {
                        sOps.add(player.isOp());
                        player.setOp(false);
                    }
                    if (main.getConfig().getBoolean("teleport")) {
                        player.teleport(tpPlayer);
                    }
                    if (main.getConfig().getBoolean("clearInventories")) {
                        player.getInventory().clear();
                    }
                    player.setHealth(20);
                    player.setFoodLevel(20);
                    player.setSaturation(5);
                    Iterator<Advancement> advancements = Bukkit.getServer().advancementIterator();
                    while (advancements.hasNext()) {
                        AdvancementProgress progress = player.getAdvancementProgress(advancements.next());
                        for (String s : progress.getAwardedCriteria())
                            progress.revokeCriteria(s);
                    }
                }
            }
            inGame = true;
            if (seconds != 0) {
                starting = new BukkitRunnable() {
                    @Override
                    public void run() {
                        playersMessage(ChatColor.BLUE + String.valueOf(seconds) + " second" + (seconds == 1 ? "" : "s") + " remaining!");
                        for (String s : hunters) {
                            Player player = Bukkit.getPlayerExact(s);
                            if (player != null) {
                                player.sendTitle(ChatColor.DARK_PURPLE + String.valueOf(seconds), "", 0, 20, 10);
                            }
                        }
                        for (String s : speedrunners) {
                            Player player = Bukkit.getPlayerExact(s);
                            if (player != null) {
                                player.sendTitle(ChatColor.DARK_PURPLE + String.valueOf(seconds), "", 0, 20, 10);
                            }
                        }
                        seconds -= 1;
                        if (seconds == 0) {
                            starting.cancel();
                        }
                    }
                }.runTaskTimer(main, 0, 20);
            } else {
                start();
            }
            game = new BukkitRunnable() {
                @Override
                public void run() {
                    if (inGame && seconds == 0 && main.getConfig().getInt("headStartDuration") != 0) {
                        start();
                        seconds = main.getConfig().getInt("headStartDuration");
                    }
                    for (int i = 0; i < speedrunners.size(); i++) {
                        speedrunner = Bukkit.getPlayerExact(speedrunners.get(i));
                        if (speedrunner == null) continue;
                        if (speedrunner.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
                            locNether.set(i, speedrunner.getLocation());
                        } else if (speedrunner.getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
                            locWorld.set(i, speedrunner.getLocation());
                        } else if (speedrunner.getWorld().getEnvironment().equals(World.Environment.THE_END)) {
                            locTheEnd.set(i, speedrunner.getLocation());
                        }
                    }
                    for (int i = 0; i < hunters.size(); i++) {
                        hunter = Bukkit.getPlayerExact(hunters.get(i));
                        if (hunter == null || compassSlot(hunter) == 50) continue;
                        if (compassMode.get(i).equals("0")) {
                            target = null;
                            finalDistance = Double.MAX_VALUE;
                            for (String s : speedrunners) {
                                speedrunner = Bukkit.getPlayerExact(s);
                                if (speedrunner == null) continue;
                                if (hunter.getWorld().getEnvironment().equals(speedrunner.getWorld().getEnvironment())) {
                                    distance = getDistance(hunter.getLocation(), speedrunner.getLocation());
                                    if (distance < finalDistance) {
                                        finalDistance = distance;
                                        target = speedrunner;
                                    }
                                }
                            }
                            if (target == null) {
                                ItemMeta meta = compass.getItemMeta();
                                meta.setDisplayName(ChatColor.RED + "There is no speedrunner is this dimension!");
                                hunter.getInventory().getItem(compassSlot(hunter)).setItemMeta(meta);
                            } else {
                                CompassMeta meta = (CompassMeta) compass.getItemMeta();
                                meta.setLodestone(target.getLocation());
                                meta.setDisplayName(ChatColor.GOLD + "Tracking: " + ChatColor.GREEN + "nearest speedrunner");
                                hunter.getInventory().getItem(compassSlot(hunter)).setItemMeta(meta);
                            }
                        } else {
                            CompassMeta meta = (CompassMeta) compass.getItemMeta();
                            target = Bukkit.getPlayerExact(whichSpeedrunner.get(i));
                            if (target == null) {
                                meta.setDisplayName(ChatColor.RED + whichSpeedrunner.get(i) + " is not online!");
                            } else {
                                if (!target.getWorld().getEnvironment().equals(hunter.getWorld().getEnvironment())) {
                                    meta.setDisplayName(ChatColor.RED + target.getName() + " is not in this dimension!");
                                } else if (target.isDead()) {
                                    meta.setDisplayName(ChatColor.RED + target.getName() + " is dead!");
                                } else {
                                    meta.setDisplayName(ChatColor.GOLD + "Tracking: " + ChatColor.GREEN + whichSpeedrunner.get(i));
                                }
                                if (main.getConfig().getBoolean("trackPortals") || target.getWorld().getEnvironment().equals(hunter.getWorld().getEnvironment())) {
                                    if (hunter.getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
                                        meta.setLodestone(locWorld.get(speedrunners.indexOf(target.getName())));
                                    } else if (hunter.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
                                        meta.setLodestone(locNether.get(speedrunners.indexOf(target.getName())));
                                    } else if (hunter.getWorld().getEnvironment().equals(World.Environment.THE_END)) {
                                        meta.setLodestone(locTheEnd.get(speedrunners.indexOf(target.getName())));
                                    }
                                }
                            }
                            hunter.getInventory().getItem(compassSlot(hunter)).setItemMeta(meta);
                        }
                    }
                }
            }.runTaskTimer(main, 20L * seconds, 1);
            return true;
        }
        if (args[0].equals("reset")) {
            if (!p.hasPermission("manhunt.reset") && main.getConfig().getBoolean("usePermissions")) {
                p.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }
            if (args.length != 1) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /manhunt help");
                return true;
            }
            p.sendMessage(ChatColor.AQUA + "Manhunt game has been reset!");
            reset();
            return true;
        }
        if (args[0].equals("pause")) {
            if (args.length != 1) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /manhunt help");
                return true;
            }
            if (!(p instanceof Player)) {
                p.sendMessage(ChatColor.RED + "Only players can use this command!");
                return true;
            }
            if (!inGame) {
                p.sendMessage(ChatColor.RED + "There is no running manhunt game!");
                return true;
            }
            if (!(hunters.contains(p.getName()) || speedrunners.contains(p.getName()))) {
                p.sendMessage(ChatColor.RED + "You are not in a manhunt game!");
                return true;
            }
            if (!main.getConfig().getBoolean("enablePauses")) {
                p.sendMessage(ChatColor.RED + "Pauses are disabled!");
                return true;
            }
            if (pausePlayers.size() == hunters.size() + speedrunners.size()) {
                p.sendMessage(ChatColor.RED + "Game is already paused!");
                return true;
            }
            if (pausePlayers.contains(p.getName())) {
                p.sendMessage(ChatColor.RED + "You have already voted to pause the game!");
                return true;
            }
            pausePlayers.add(p.getName());
            playersMessage(ChatColor.AQUA + p.getName() + " wants to pause the game! (" + pausePlayers.size() + "/" + (hunters.size() + speedrunners.size()) + ")");
            if (pausePlayers.size() == hunters.size() + speedrunners.size()) {
                pausing.cancel();
                unpausePlayers.clear();
                playersMessage(ChatColor.AQUA + "Game paused!");
                p.getServer().getWorlds().get(0).setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                return true;
            }
            if (pausePlayers.size() == 1) {
                pausing = new BukkitRunnable() {
                    @Override
                    public void run() {
                        pausePlayers.clear();
                        playersMessage(ChatColor.AQUA + "Voting for pause has expired");
                    }
                }.runTaskLater(main, 1200);
            }
            return true;
        }
        if (args[0].equals("unpause")) {
            if (args.length != 1) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /manhunt help");
                return true;
            }
            if (!(p instanceof Player)) {
                p.sendMessage(ChatColor.RED + "Only players can use this command!");
                return true;
            }
            if (!inGame) {
                p.sendMessage(ChatColor.RED + "There is no running manhunt game!");
                return true;
            }
            if (!(hunters.contains(p.getName()) || speedrunners.contains(p.getName()))) {
                p.sendMessage(ChatColor.RED + "You are not in a manhunt game!");
                return true;
            }
            if (!main.getConfig().getBoolean("enablePauses")) {
                p.sendMessage(ChatColor.RED + "Pauses are disabled!");
                return true;
            }
            if (unpausePlayers.size() == hunters.size() + speedrunners.size()) {
                p.sendMessage(ChatColor.RED + "Game is not paused!");
                return true;
            }
            if (unpausePlayers.contains(p.getName())) {
                p.sendMessage(ChatColor.RED + "You have already voted to unpause the game!");
                return true;
            }
            unpausePlayers.add(p.getName());
            playersMessage(ChatColor.AQUA + p.getName() + " wants to unpause the game! (" + unpausePlayers.size() + "/" + (hunters.size() + speedrunners.size()) + ")");
            if (unpausePlayers.size() == hunters.size() + speedrunners.size()) {
                unpausing.cancel();
                pausePlayers.clear();
                playersMessage(ChatColor.AQUA + "Game unpaused!");
                p.getServer().getWorlds().get(0).setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
                for (String s : hunters) {
                    Player player = Bukkit.getPlayerExact(s);
                    if (player != null) {
                        player.setFallDistance(0);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 255));
                    }
                }
                for (String s : speedrunners) {
                    Player player = Bukkit.getPlayerExact(s);
                    if (player != null) {
                        player.setFallDistance(0);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 255));
                    }
                }
                return true;
            }
            if (unpausePlayers.size() == 1) {
                unpausing = new BukkitRunnable() {
                    @Override
                    public void run() {
                        unpausePlayers.clear();
                        playersMessage(ChatColor.AQUA + "Voting for unpause has expired");
                    }
                }.runTaskLater(main, 1200);
            }
            return true;
        }
        if (args[0].equals("list")) {
            if (args.length != 1) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /manhunt help");
                return true;
            }
            if (speedrunners.size() + hunters.size() == 0) {
                p.sendMessage(ChatColor.RED + "There is no player in your game!");
                return true;
            }
            p.sendMessage(ChatColor.GREEN + "------- " + ChatColor.WHITE + " Minecraft Manhunt " + ChatColor.GREEN + "----------");
            if (!speedrunners.isEmpty()) {
                p.sendMessage(ChatColor.GOLD + "Speedrunners:");
            }
            for (String s : speedrunners) {
                p.sendMessage(ChatColor.AQUA + s);
            }
            if (!hunters.isEmpty()) {
                p.sendMessage(ChatColor.GOLD + "Hunters:");
            }
            for (String s : hunters) {
                p.sendMessage(ChatColor.AQUA + s);
            }
            p.sendMessage(ChatColor.GREEN + "----------------------------------");
            return true;
        }
        p.sendMessage(ChatColor.RED + "Wrong argument. For help, type: /manhunt help");
        return true;
    }

    public static void removePlayer(String name) {
        int index;
        if (hunters.contains(name)) {
            index = hunters.indexOf(name);
        } else {
            index = speedrunners.indexOf(name);
        }
        if (inGame) {
            if (main.getConfig().getBoolean("takeAwayOps")) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(name);
                if (hunters.contains(name)) {
                    target.setOp(hOps.get(index));
                } else {
                    target.setOp(sOps.get(index));
                }
                hOps.remove(index);
                sOps.remove(index);
            }
            if (hunters.contains(name)) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(name);
                player.getPlayer().getInventory().clear(compassSlot(player.getPlayer()));
            }
            if (speedrunners.contains(name)) {
                lives.remove(index);
                locWorld.remove(index);
                locNether.remove(index);
                locTheEnd.remove(index);
            }
        }
        hunters.remove(index);
        speedrunners.remove(index);
    }

    public static void reset() {
        for (String s : hunters) {
            Player player = Bukkit.getPlayerExact(s);
            if (player != null) {
                player.getInventory().clear(compassSlot(player));
            }
        }
        if (inGame) {
            if (seconds != main.getConfig().getInt("headStartDuration")) {
                starting.cancel();
            }
            game.cancel();
            if (main.getConfig().getBoolean("takeAwayOps")) {
                for (int i = 0; i < hunters.size(); i++) {
                    OfflinePlayer tar = Bukkit.getOfflinePlayer(hunters.get(i));
                    tar.setOp(hOps.get(i));
                }
                for (int i = 0; i < speedrunners.size(); i++) {
                    OfflinePlayer tar = Bukkit.getOfflinePlayer(speedrunners.get(i));
                    tar.setOp(sOps.get(i));
                }
            }
        }
        pausePlayers.clear();
        unpausePlayers.clear();
        hOps.clear();
        sOps.clear();
        hunters.clear();
        speedrunners.clear();
        compassMode.clear();
        whichSpeedrunner.clear();
        locWorld.clear();
        locNether.clear();
        locTheEnd.clear();
        lives.clear();
        inGame = false;
    }

    private double getDistance(Location from, Location to) {
        double fromX = from.getX();
        double fromZ = from.getZ();
        double toX = to.getX();
        double toZ = to.getZ();
        return Math.sqrt(Math.pow((fromX - toX), 2) + Math.pow((fromZ - toZ), 2));
    }

    public static int compassSlot(Player p) {
        for (int i = 0; i < 41; i++) {
            if (i == 36) {
                i = 40;
            }
            if (p.getInventory().getItem(i) != null) {
                if (p.getInventory().getItem(i).hasItemMeta()) {
                    if (p.getInventory().getItem(i).getItemMeta().hasLore()) {
                        if (p.getInventory().getItem(i).getItemMeta().getLore().get(0).equals(ChatColor.BLUE + "This compass is to track speedrunners!")) {
                            return i;
                        }
                    }
                }
            }
        }
        return 50;
    }

    public static void playersMessage(String s) {
        for (String value : hunters) {
            Player player = Bukkit.getPlayerExact(value);
            if (player != null) {
                player.sendMessage(s);
            }
        }
        for (String value : speedrunners) {
            Player player = Bukkit.getPlayerExact(value);
            if (player != null) {
                player.sendMessage(s);
            }
        }
    }

    public static void start() {
        for (String s : hunters) {
            Player player = Bukkit.getPlayerExact(s);
            if (player != null) {
                player.sendTitle(ChatColor.DARK_PURPLE + "START!", "", 0, 20, 10);
                player.setFallDistance(0);
                player.sendMessage(ChatColor.AQUA + "START!");
            }
        }
        for (String s : speedrunners) {
            Player player = Bukkit.getPlayerExact(s);
            if (player != null) {
                player.sendTitle(ChatColor.DARK_PURPLE + "START!", "", 0, 20, 10);
                player.sendMessage(ChatColor.AQUA + "START!");
            }
        }
    }
}