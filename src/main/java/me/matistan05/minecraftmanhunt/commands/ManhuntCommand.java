package me.matistan05.minecraftmanhunt.commands;

import me.matistan05.minecraftmanhunt.Main;
import me.matistan05.minecraftmanhunt.classes.Hunter;
import me.matistan05.minecraftmanhunt.classes.Speedrunner;
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
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class ManhuntCommand implements CommandExecutor {
    private static Main main;
    public static List<Hunter> hunters = new ArrayList<>();
    public static List<Speedrunner> speedrunners = new ArrayList<>();
    public static int secondsToStart;
    public static boolean waitingForStart = false;
    public static boolean paused = false;
    public static boolean inGame = false;
    private static BukkitTask starting;
    public static BukkitTask game;
    public static BukkitTask pausing, unpausing;
    public static ItemStack compass;
    public static List<String> pausePlayers = new LinkedList<>();
    public static List<String> unpausePlayers = new LinkedList<>();
    public static Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    public static Player findPlayer;

    public ManhuntCommand(Main main) {
        ManhuntCommand.main = main;
    }

    @Override
    public boolean onCommand(CommandSender p, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            p.sendMessage(ChatColor.RED + "You must type an argument. For help, type: /manhunt help");
        } else if (args[0].equals("help")) {
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
        } else if (args[0].equals("rules")) {
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
        } else if (args[0].equals("add")) {
            if (!p.hasPermission("manhunt.add") && main.getConfig().getBoolean("usePermissions")) {
                p.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }
            if (args.length < 3) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /manhunt help");
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
                        if (isSpeedrunner(target.getName())) continue;
                        if (isHunter(target.getName())) {
                            if (inGame || waitingForStart) continue;
                            hunters.removeIf(h -> h.getName().equals(target.getName()));
                        }
                        speedrunners.add(new Speedrunner(target.getName()));
                        if (inGame || waitingForStart) setUpPlayer(target.getName(), false);
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
                    Player target = Bukkit.getPlayerExact(args[i]);
                    if (target == null || isSpeedrunner(target.getName())) continue;
                    if (isHunter(target.getName())) {
                        if (inGame || waitingForStart) continue;
                        hunters.removeIf(h -> h.getName().equals(target.getName()));
                    }
                    speedrunners.add(new Speedrunner(target.getName()));
                    if (inGame || waitingForStart) setUpPlayer(target.getName(), false);
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
                        if (isHunter(target.getName())) continue;
                        if (isSpeedrunner(target.getName())) {
                            if (inGame || waitingForStart) continue;
                            speedrunners.removeIf(h -> h.getName().equals(target.getName()));
                        }
                        hunters.add(new Hunter(target.getName()));
                        if (inGame || waitingForStart) setUpPlayer(target.getName(), true);
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
                    Player target = Bukkit.getPlayerExact(args[i]);
                    if (target == null || isHunter(target.getName())) continue;
                    if (isSpeedrunner(target.getName())) {
                        if (inGame || waitingForStart) continue;
                        speedrunners.removeIf(h -> h.getName().equals(target.getName()));
                    }
                    hunters.add(new Hunter(target.getName()));
                    if (inGame || waitingForStart) setUpPlayer(target.getName(), true);
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
        } else if (args[0].equals("remove")) {
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
                    if ((!isInGame(target.getName())) ||
                       ((inGame || waitingForStart) &&
                        (isSpeedrunner(target.getName()) && speedrunners.size() == 1 ||
                         isHunter(target.getName()) && hunters.size() == 1))) continue;
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
                Player target = Bukkit.getPlayerExact(args[i]);
                if (target == null || (!isInGame(target.getName())) ||
                     ((inGame || waitingForStart) &&
                      (isSpeedrunner(target.getName()) && speedrunners.size() == 1 ||
                             isHunter(target.getName()) && hunters.size() == 1))) continue;
                removePlayer(target.getName());
                count++;
            }
            if (count > 0) {
                p.sendMessage(ChatColor.AQUA + "Successfully removed " + count + " player" + (count == 1 ? "" : "s") + " from the game!");
            } else {
                p.sendMessage(ChatColor.RED + "Could not remove " + (args.length == 2 ? "this player!" : "these players!"));
            }
        } else if (args[0].equals("start")) {
            if (!p.hasPermission("manhunt.start") && main.getConfig().getBoolean("usePermissions")) {
                p.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }
            if (args.length != 1) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /manhunt help");
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
            if (inGame || waitingForStart) {
                p.sendMessage(ChatColor.YELLOW + "The game has already started!");
                return true;
            }
            for (Speedrunner speedrunner : speedrunners) {
                Player player = Bukkit.getPlayerExact(speedrunner.getName());
                if (player == null) {
                    p.sendMessage(ChatColor.RED + "Someone from your game is offline!");
                    return true;
                }
            }
            for (Hunter hunter : hunters) {
                Player player = Bukkit.getPlayerExact(hunter.getName());
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
            createCompass();
            findPlayer = null;
            if (main.getConfig().getBoolean("teleport")) {
                for (Speedrunner speedrunnerObject : speedrunners) {
                    Player pl = Bukkit.getPlayerExact(speedrunnerObject.getName());
                    if (pl != null) {
                        findPlayer = pl;
                        break;
                    }
                }
                if (findPlayer == null) {
                    for (Hunter hunter : hunters) {
                        Player pl = Bukkit.getPlayerExact(hunter.getName());
                        if (pl != null) {
                            findPlayer = pl;
                            break;
                        }
                    }
                }
            }

            Team huntersTeam = scoreboard.registerNewTeam("hunters");
            huntersTeam.setAllowFriendlyFire(main.getConfig().getBoolean("friendlyFire"));
            huntersTeam.setColor(ChatColor.RED);
            huntersTeam.setPrefix(ChatColor.DARK_RED + "Hunter ");

            Team speedrunnersTeam = scoreboard.registerNewTeam("speedrunners");
            speedrunnersTeam.setAllowFriendlyFire(main.getConfig().getBoolean("friendlyFire"));
            speedrunnersTeam.setColor(ChatColor.GREEN);
            speedrunnersTeam.setPrefix(ChatColor.DARK_GREEN + "Speedrunner ");

            for (Hunter hunterObject : hunters) {
                setUpPlayer(hunterObject.getName(), true);
            }
            for (Speedrunner speedrunnerObject : speedrunners) {
                setUpPlayer(speedrunnerObject.getName(), false);
            }
            waitingForStart = true;
            secondsToStart = Math.max(main.getConfig().getInt("headStartDuration"), 0);
            starting = new BukkitRunnable() {
                @Override
                public void run() {
                    if (secondsToStart == 0) {
                        inGame = true;
                        waitingForStart = false;
                        start();
                        starting.cancel();
                    } else {
                        playersMessage(ChatColor.BLUE + String.valueOf(secondsToStart) + " second" + (secondsToStart == 1 ? "" : "s") + " remaining!");
                        playersTitle(ChatColor.DARK_PURPLE + String.valueOf(secondsToStart));
                        secondsToStart -= 1;
                    }
                }
            }.runTaskTimer(main, 0, 20);
            game = new BukkitRunnable() {
                @Override
                public void run() {
                    for (Speedrunner speedrunnerObject : speedrunners) {
                        Player speedrunner = Bukkit.getPlayerExact(speedrunnerObject.getName());
                        if (speedrunner == null) continue;
                        if (speedrunner.getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
                            speedrunnerObject.setLocWorld(speedrunner.getLocation());
                        } else if (speedrunner.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
                            speedrunnerObject.setLocNether(speedrunner.getLocation());
                        } else if (speedrunner.getWorld().getEnvironment().equals(World.Environment.THE_END)) {
                            speedrunnerObject.setLocTheEnd(speedrunner.getLocation());
                        }
                    }
                    for (Hunter hunterObject : hunters) {
                        Player hunter = Bukkit.getPlayerExact(hunterObject.getName());
                        if (hunter == null || compassSlot(hunter) == 50) continue;
                        if (hunterObject.getCompassMode() == 0) {
                            double finalDistance = Double.MAX_VALUE;
                            Player target = null;
                            for (Speedrunner speedrunnerObject : speedrunners) {
                                Player speedrunner = Bukkit.getPlayerExact(speedrunnerObject.getName());
                                if (speedrunner == null) continue;
                                if (hunter.getWorld().getEnvironment().equals(speedrunner.getWorld().getEnvironment())) {
                                    double distance = getDistance(hunter.getLocation(), speedrunner.getLocation());
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
                            String targetName = hunterObject.getWhichSpeedrunner();
                            if (!isSpeedrunner(targetName)) {
                                targetName = speedrunners.get(0).getName();
                            }
                            Player target = Bukkit.getPlayerExact(targetName);
                            if (target == null) {
                                meta.setDisplayName(ChatColor.RED + targetName + " is not online!");
                            } else {
                                if (!target.getWorld().getEnvironment().equals(hunter.getWorld().getEnvironment())) {
                                    meta.setDisplayName(ChatColor.RED + targetName + " is not in this dimension!");
                                } else {
                                    meta.setDisplayName(ChatColor.GOLD + "Tracking: " + ChatColor.GREEN + targetName);
                                }
                                if (main.getConfig().getBoolean("trackPortals") || target.getWorld().getEnvironment().equals(hunter.getWorld().getEnvironment())) {
                                    if (hunter.getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
                                        meta.setLodestone(getSpeedrunner(targetName).getLocWorld());
                                    } else if (hunter.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
                                        meta.setLodestone(getSpeedrunner(targetName).getLocNether());
                                    } else if (hunter.getWorld().getEnvironment().equals(World.Environment.THE_END)) {
                                        meta.setLodestone(getSpeedrunner(targetName).getLocTheEnd());
                                    }
                                }
                            }
                            hunter.getInventory().getItem(compassSlot(hunter)).setItemMeta(meta);
                        }
                    }
                }
            }.runTaskTimer(main, 0, 1);
        } else if (args[0].equals("reset")) {
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
        } else if (args[0].equals("pause")) {
            if (args.length != 1) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /manhunt help");
                return true;
            }
            if (!p.hasPermission("manhunt.pause") && main.getConfig().getBoolean("usePermissions") && !isInGame(p.getName())) {
                p.sendMessage(ChatColor.RED + "You have to be in a game to vote for pause, or need special permission!");
                return true;
            }
            if (!inGame) {
                p.sendMessage(ChatColor.RED + "There is no running manhunt game!");
                return true;
            }
            if (!main.getConfig().getBoolean("enablePauses")) {
                p.sendMessage(ChatColor.RED + "Pauses are disabled!");
                return true;
            }
            if (paused) {
                p.sendMessage(ChatColor.RED + "Game is already paused!");
                return true;
            }
            if (pausePlayers.contains(p.getName())) {
                p.sendMessage(ChatColor.RED + "You have already voted to pause the game!");
                return true;
            }
            if (p.hasPermission("manhunt.pause")) {
                pauseGame(p);
                return true;
            }
            pausePlayers.add(p.getName());
            playersMessage(ChatColor.AQUA + p.getName() + " wants to pause the game! (" + pausePlayers.size() + "/" + (hunters.size() + speedrunners.size()) + ")");
            if (pausePlayers.size() == hunters.size() + speedrunners.size()) {
                pauseGame(p);
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
        } else if (args[0].equals("unpause")) {
            if (args.length != 1) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /manhunt help");
                return true;
            }
            if (!p.hasPermission("manhunt.unpause") && main.getConfig().getBoolean("usePermissions") && !isInGame(p.getName())) {
                p.sendMessage(ChatColor.RED + "You have to be in a game to vote for unpause, or need special permission!");
                return true;
            }
            if (!inGame) {
                p.sendMessage(ChatColor.RED + "There is no running manhunt game!");
                return true;
            }
            if (!main.getConfig().getBoolean("enablePauses")) {
                p.sendMessage(ChatColor.RED + "Pauses are disabled!");
                return true;
            }
            if (!paused) {
                p.sendMessage(ChatColor.RED + "Game is not paused!");
                return true;
            }
            if (unpausePlayers.contains(p.getName())) {
                p.sendMessage(ChatColor.RED + "You have already voted to unpause the game!");
                return true;
            }
            if (p.hasPermission("manhunt.unpause")) {
                unpauseGame(p);
                return true;
            }
            unpausePlayers.add(p.getName());
            playersMessage(ChatColor.AQUA + p.getName() + " wants to unpause the game! (" + unpausePlayers.size() + "/" + (hunters.size() + speedrunners.size()) + ")");
            if (unpausePlayers.size() == hunters.size() + speedrunners.size()) {
                unpauseGame(p);
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
        } else if (args[0].equals("list")) {
            if (!p.hasPermission("manhunt.list") && main.getConfig().getBoolean("usePermissions")) {
                p.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }
            if (args.length != 1) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /manhunt help");
                return true;
            }
            if (speedrunners.size() + hunters.size() == 0) {
                p.sendMessage(ChatColor.RED + "There is no player in your game!");
                return true;
            }
            p.sendMessage(ChatColor.YELLOW + "-------" + ChatColor.WHITE + " Minecraft Manhunt " + ChatColor.YELLOW + "-------");
            if (!speedrunners.isEmpty()) {
                p.sendMessage(ChatColor.GREEN + "Speedrunners:");
                for (Speedrunner speedrunnerObject : speedrunners) {
                    p.sendMessage(ChatColor.DARK_GREEN + speedrunnerObject.getName());
                }
            }
            if (!hunters.isEmpty()) {
                p.sendMessage(ChatColor.RED + "Hunters:");
                for (Hunter hunterObject : hunters) {
                    p.sendMessage(ChatColor.DARK_RED + hunterObject.getName());
                }
            }
            p.sendMessage(ChatColor.YELLOW + "----------------------------------");
        } else {
            p.sendMessage(ChatColor.RED + "Wrong argument. For help, type: /manhunt help");
        }
        return true;
    }

    public static void removePlayer(String name) {
        Hunter hunterObject = getHunter(name);
        if (hunterObject != null) {
            Player hunter = Bukkit.getPlayerExact(name);
            if (inGame || waitingForStart) {
                if (main.getConfig().getBoolean("takeAwayOps")) {
                    OfflinePlayer target = Bukkit.getOfflinePlayer(name);
                    target.setOp(hunterObject.isOp());
                }
                if (hunter != null) {
                    hunter.getInventory().clear(compassSlot(hunter));
                }
            }
            hunters.removeIf(h -> h.getName().equals(name));
            Team huntersTeam = scoreboard.getTeam("hunters");
            if (huntersTeam != null) huntersTeam.removeEntry(name);
        } else {
            Speedrunner speedrunnerObject = getSpeedrunner(name);
            if (speedrunnerObject != null) {
                if (inGame || waitingForStart) {
                    if (main.getConfig().getBoolean("takeAwayOps")) {
                        OfflinePlayer target = Bukkit.getOfflinePlayer(name);
                        target.setOp(speedrunnerObject.isOp());
                    }
                }
                speedrunners.removeIf(s -> s.getName().equals(name));
                Team speedrunnersTeam = scoreboard.getTeam("speedrunners");
                if (speedrunnersTeam != null) speedrunnersTeam.removeEntry(name);
            }
        }
    }

    public static void reset() {
        while (!hunters.isEmpty()) {
            removePlayer(hunters.get(0).getName());
        }
        while (!speedrunners.isEmpty()) {
            removePlayer(speedrunners.get(0).getName());
        }
        Team huntersTeam = scoreboard.getTeam("hunters");
        if (huntersTeam != null) huntersTeam.unregister();
        Team speedrunnersTeam = scoreboard.getTeam("speedrunners");
        if (speedrunnersTeam != null) speedrunnersTeam.unregister();
        if (inGame) {
            game.cancel();
            inGame = false;
        }
        if (waitingForStart) {
            starting.cancel();
            waitingForStart = false;
        }
        pausePlayers.clear();
        unpausePlayers.clear();
        paused = false;
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
            if (isCompass(p.getInventory().getItem(i))) {
                return i;
            }
        }
        return 50;
    }

    public static void playersMessage(String s) {
        for (Hunter hunterObject : hunters) {
            Player hunter = Bukkit.getPlayerExact(hunterObject.getName());
            if (hunter != null) {
                hunter.sendMessage(s);
            }
        }
        for (Speedrunner speedrunnerObject : speedrunners) {
            Player speedrunner = Bukkit.getPlayerExact(speedrunnerObject.getName());
            if (speedrunner != null) {
                speedrunner.sendMessage(s);
            }
        }
    }

    public static void playersTitle(String s) {
        for (Hunter hunterObject : hunters) {
            Player hunter = Bukkit.getPlayerExact(hunterObject.getName());
            if (hunter != null) {
                hunter.sendTitle(s, "", 0, 20, 10);
            }
        }
        for (Speedrunner speedrunnerObject : speedrunners) {
            Player speedrunner = Bukkit.getPlayerExact(speedrunnerObject.getName());
            if (speedrunner != null) {
                speedrunner.sendTitle(s, "", 0, 20, 10);
            }
        }
    }

    public static void start() {
        playersTitle(ChatColor.DARK_PURPLE + "START!");
        playersMessage(ChatColor.AQUA + "START!");
        for (Hunter hunterObject : hunters) {
            Player hunter = Bukkit.getPlayerExact(hunterObject.getName());
            if (hunter != null) hunter.setFallDistance(0);
        }
    }

    public static void createCompass() {
        ItemStack item = new ItemStack(Material.COMPASS, 1);
        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(main, "ManhuntCompass");
        meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
        meta.setDisplayName(ChatColor.GOLD + "Tracking: " + ChatColor.GREEN + "nearest speedrunner");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.BLUE + "This compass is to track speedrunners!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        compass = item;
    }

    public static boolean isCompass(ItemStack itemStack) {
        if (itemStack == null) return false;
        if (!itemStack.hasItemMeta() || itemStack.getItemMeta() == null) return false;
        return (itemStack.getItemMeta().getPersistentDataContainer().has
                (new NamespacedKey(main, "ManhuntCompass"), PersistentDataType.BYTE));
    }

    public static void setUpPlayer(String name, boolean isHunter) {
        Player player = Bukkit.getPlayerExact(name);
        if (player == null) return;
        if (main.getConfig().getBoolean("clearInventories")) {
            player.getInventory().clear();
        }
        if (main.getConfig().getBoolean("teleport") && !inGame) {
            player.teleport(findPlayer);
        }
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(5);
        Iterator<Advancement> advancements = Bukkit.getServer().advancementIterator();
        while (advancements.hasNext()) {
            AdvancementProgress progress = player.getAdvancementProgress(advancements.next());
            for (String s : progress.getAwardedCriteria())
                progress.revokeCriteria(s);
        }
        if (isHunter) {
            Team huntersTeam = scoreboard.getTeam("hunters");
            if (huntersTeam != null) huntersTeam.addEntry(name);
            Hunter hunterObject = getHunter(name);
            player.getInventory().addItem(compass);
            hunterObject.setWhichSpeedrunner(speedrunners.get(0).getName());
            hunterObject.setCompassMode(1);
            if (main.getConfig().getBoolean("takeAwayOps")) {
                hunterObject.setOp(player.isOp());
                player.setOp(false);
            }
        } else {
            Team speedrunnersTeam = scoreboard.getTeam("speedrunners");
            if (speedrunnersTeam != null) speedrunnersTeam.addEntry(name);
            Speedrunner speedrunnerObject = getSpeedrunner(name);
            if (player.getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
                speedrunnerObject.setLocWorld(player.getLocation());
            } else speedrunnerObject.setLocWorld(null);
            if (player.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
                speedrunnerObject.setLocNether(player.getLocation());
            } else speedrunnerObject.setLocNether(null);
            if (player.getWorld().getEnvironment().equals(World.Environment.THE_END)) {
                speedrunnerObject.setLocTheEnd(player.getLocation());
            } else speedrunnerObject.setLocTheEnd(null);
            speedrunnerObject.setLives(Math.max(main.getConfig().getInt("speedrunnersLives"), 1));
            if (main.getConfig().getBoolean("spectatorAfterDeath")) {
                speedrunnerObject.setGameMode(player.getGameMode());
            }
            if (main.getConfig().getBoolean("takeAwayOps")) {
                speedrunnerObject.setOp(player.isOp());
                player.setOp(false);
            }
        }
    }

    public static void unpauseGame(CommandSender p) {
        paused = false;
        if (unpausing != null && !unpausing.isCancelled()) unpausing.cancel();
        pausePlayers.clear();
        playersMessage(ChatColor.AQUA + "Game unpaused!");
        p.getServer().getWorlds().get(0).setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        for (Hunter hunterObject : hunters) {
            Player hunter = Bukkit.getPlayerExact(hunterObject.getName());
            if (hunter != null) {
                hunter.setFallDistance(0);
                hunter.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 255));
            }
        }
        for (Speedrunner speedrunnerObject : speedrunners) {
            Player speedrunner = Bukkit.getPlayerExact(speedrunnerObject.getName());
            if (speedrunner != null) {
                speedrunner.setFallDistance(0);
                speedrunner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 255));
            }
        }
    }

    public static void pauseGame(CommandSender p) {
        paused = true;
        if (pausing != null && !pausing.isCancelled()) pausing.cancel();
        unpausePlayers.clear();
        playersMessage(ChatColor.AQUA + "Game paused!");
        p.getServer().getWorlds().get(0).setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
    }

    public static boolean isHunter(String name) {
        return hunters.stream().anyMatch(h -> h.getName().equals(name));
    }

    public static boolean isSpeedrunner(String name) {
        return speedrunners.stream().anyMatch(s -> s.getName().equals(name));
    }

    public static boolean isInGame(String name) {
        return isHunter(name) || isSpeedrunner(name);
    }

    public static Speedrunner getSpeedrunner(String name) {
        return speedrunners.stream().filter(s -> s.getName().equals(name)).findFirst().orElse(null);
    }

    public static Hunter getHunter(String name) {
        return hunters.stream().filter(h -> h.getName().equals(name)).findFirst().orElse(null);
    }
}
