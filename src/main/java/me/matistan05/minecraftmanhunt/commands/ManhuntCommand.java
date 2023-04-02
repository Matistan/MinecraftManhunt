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
    BukkitTask pausing;
    BukkitTask unpausing;
    public static ItemStack compass;
    private double finalDistance, distance;
    private Player hunter, speedrunner, target;
    public static int pause;
    public static int unpause;
    public static List<String> pausePlayers = new LinkedList<>();
    public static List<String> unpausePlayers = new LinkedList<>();

/*
§0 - black
§1 - blue
§2 - green
§3 - aqua
§4 - red
§5 -pink
§6 -gold
§7 - light grey
§8 - grey
§9 - light blue
 */

    public ManhuntCommand(Main main) {
        ManhuntCommand.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player p = (Player) sender;
        if(args.length == 0) {
            p.sendMessage(ChatColor.RED + "You must type an argument. For help, type: /manhunt help");
            return true;
        }
        if (args[0].equals("help")) {
            if (args.length != 1) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /manhunt help");
                return true;
            }
            p.sendMessage(ChatColor.GREEN + "------- " + ChatColor.WHITE + " Minecraft Manhunt " + ChatColor.GREEN + "----------");
            p.sendMessage(ChatColor.BLUE + "Here is a list of manhunt commands:");
            p.sendMessage(ChatColor.YELLOW + "/manhunt add <player name> <role> " + ChatColor.AQUA + "- adds a player to a manhunt game with a specified role");
            p.sendMessage(ChatColor.YELLOW + "/manhunt remove <player name> " + ChatColor.AQUA + "- removes a player from a manhunt game");
            p.sendMessage(ChatColor.YELLOW + "/manhunt start " + ChatColor.AQUA + "- starts a manhunt game");
            p.sendMessage(ChatColor.YELLOW + "/manhunt reset " + ChatColor.AQUA + "- resets a manhunt game");
            p.sendMessage(ChatColor.YELLOW + "/manhunt pause " + ChatColor.AQUA + "- pauses a manhunt game");
            p.sendMessage(ChatColor.YELLOW + "/manhunt unpause " + ChatColor.AQUA + "- resumes a manhunt game, if stopped");
            p.sendMessage(ChatColor.YELLOW + "/manhunt list " + ChatColor.AQUA + "- shows a list of players in a manhunt game with their roles");
            p.sendMessage(ChatColor.YELLOW + "/manhunt help " + ChatColor.AQUA + "- shows a list of manhunt commands");
            p.sendMessage(ChatColor.GREEN + "----------------------------------");
            return true;
        }
        if (args[0].equals("add")) {
            if(args.length != 3) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /manhunt help");
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[1]);
            if(target == null) {
                p.sendMessage(ChatColor.RED + "This player does not exist or is offline");
                return true;
            }
            if(inGame) {
                p.sendMessage(ChatColor.RED + "The game has already started!");
                return true;
            }
            if(args[2].equals("speedrunner")) {
                if(speedrunners.contains(target.getName())) {
                    p.sendMessage(ChatColor.RED + "This player is already a speedrunner!");
                    return true;
                }
                if(hunters.contains(target.getName())) {
                    speedrunners.add(target.getName());
                    hunters.remove(target.getName());
                    p.sendMessage(ChatColor.AQUA + "Changed " + target.getName() + " role from hunter to speedrunner!");
                    return true;
                }
                speedrunners.add(target.getName());
                p.sendMessage(ChatColor.AQUA + "Successfully added new speedrunner " + target.getName() + " to the game!");
                return true;
            }
            if(args[2].equals("hunter")) {
                if(hunters.contains(target.getName())) {
                    p.sendMessage(ChatColor.RED + "This player is already a hunter!");
                    return true;
                }
                if(speedrunners.contains(target.getName())) {
                    speedrunners.remove(target.getName());
                    hunters.add(target.getName());
                    p.sendMessage(ChatColor.AQUA + "Changed " + target.getName() + " role from speedrunner to hunter!");
                    return true;
                }
                hunters.add(target.getName());
                p.sendMessage(ChatColor.AQUA + "Successfully added new hunter " + target.getName() + " to the game!");
                return true;
            }
            p.sendMessage(ChatColor.RED + "Wrong manhunt role. For help, type: /manhunt help");
            return true;
        }
        if (args[0].equals("remove")) {
            if(args.length != 2) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /manhunt help");
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[1]);
            if(target == null) {
                p.sendMessage(ChatColor.RED + "This player does not exist or is offline");
                return true;
            }
            if(speedrunners.contains(target.getName())) {
                speedrunners.remove(target.getName());
                p.sendMessage(ChatColor.AQUA + "Successfully removed " + target.getName() + " from speedrunners");
                return true;
            }
            if (hunters.contains(target.getName())) {
                hunters.remove(target.getName());
                p.sendMessage(ChatColor.AQUA + "Successfully removed " + target.getName() + " from hunters");
                return true;
            }
            p.sendMessage(ChatColor.RED + "This player is not in your manhunt game");
            return true;
        }
        if (args[0].equals("start")) {
            if(args.length != 1) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /manhunt help");
                return true;
            }
            if(speedrunners.size() + hunters.size() == 0) {
                p.sendMessage(ChatColor.RED + "There are no hunters and speedrunners!");
                return true;
            }
            if(speedrunners.size() == 0) {
                p.sendMessage(ChatColor.RED + "There are no speedrunners!");
                return true;
            }if(hunters.size() == 0) {
                p.sendMessage(ChatColor.RED + "There are no hunters!");
                return true;
            }
            if(inGame) {
                p.sendMessage(ChatColor.YELLOW + "The game has already started!");
                return true;
            }
            if(main.getConfig().getBoolean("timeSetDayOnStart")) {
                p.getWorld().setTime(0);
            }
            if(main.getConfig().getBoolean("weatherClearOnStart")) {
                p.getWorld().setStorm(false);
            }
            seconds = main.getConfig().getInt("headStartDuration");
            pause = 0;
            unpause = hunters.size() + speedrunners.size();
            ItemStack item = new ItemStack(Material.COMPASS,1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "Tracking: " + ChatColor.GREEN + "nearest speedrunner");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.BLUE + "This compass is to track speedrunners!");
            meta.setLore(lore);
            item.setItemMeta(meta);
            compass = item;
            Player tar;
            for (String value : hunters) {
                tar = Bukkit.getPlayerExact(value);
                if (tar != null) {
                    tar.getInventory().clear();
                    tar.getInventory().addItem(compass);
                    tar.setGameMode(GameMode.SURVIVAL);
                    if(main.getConfig().getBoolean("takeAwayOps")) {
                        hOps.add(tar.isOp());
                        tar.setOp(false);
                    }
                    if(main.getConfig().getBoolean("clearInventories")) {
                        tar.getInventory().clear();
                    }
                    compassMode.add("1");
                    whichSpeedrunner.add(speedrunners.get(0));
                    tar.setHealth(20);
                    tar.setFoodLevel(20);
                    tar.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 255));
                    Iterator<Advancement> advancements = Bukkit.getServer().advancementIterator();
                    while (advancements.hasNext()) {
                        AdvancementProgress progress = tar.getAdvancementProgress(advancements.next());
                        for (String s : progress.getAwardedCriteria())
                            progress.revokeCriteria(s);
                    }
                }
            }
            for (String value : speedrunners) {
                Player player = Bukkit.getPlayerExact(value);
                if (player != null) {
                    Location loc = player.getLocation();
                    locWorld.add(loc);
                    locNether.add(null);
                    locTheEnd.add(null);
                    lives.add(main.getConfig().getInt("speedrunnersLives"));
                    player.setGameMode(GameMode.SURVIVAL);
                    if(main.getConfig().getBoolean("takeAwayOps")) {
                        hOps.add(player.isOp());
                        player.setOp(false);
                    }
                    if(main.getConfig().getBoolean("clearInventories")) {
                        player.getInventory().clear();
                    }
                    player.setHealth(20);
                    player.setFoodLevel(20);
                    player.getInventory().clear();
                    Iterator<Advancement> advancements = Bukkit.getServer().advancementIterator();
                    while (advancements.hasNext()) {
                        AdvancementProgress progress = player.getAdvancementProgress(advancements.next());
                        for (String s : progress.getAwardedCriteria())
                            progress.revokeCriteria(s);
                    }
                }
            }
            inGame = true;
            starting = new BukkitRunnable() {
                @Override
                public void run() {
                    seconds -= 1;
                    if(!inGame || seconds == -1) {
                        this.cancel();
                    }
                    playersMessage(ChatColor.BLUE + String.valueOf(seconds + 1) + " second" + (seconds == 0 ? "" : "s") +" remaining!");
                    for (String s : hunters) {
                        Player player = Bukkit.getPlayerExact(s);
                        if(player != null) {
                            player.sendTitle(ChatColor.DARK_PURPLE + String.valueOf(seconds), "", 0, 20, 10);
                        }
                    }
                    for (String s : speedrunners) {
                        Player player = Bukkit.getPlayerExact(s);
                        if(player != null) {
                            player.sendTitle(ChatColor.DARK_PURPLE + String.valueOf(seconds), "", 0, 20, 10);
                        }
                    }
                }
            }.runTaskTimer(main, 0, 20);
            for (String s : hunters) {
                Player player = Bukkit.getPlayerExact(s);
                if(player != null) {
                    player.sendTitle(ChatColor.DARK_PURPLE + "START!", "", 0, 20, 10);
                }
            }
            for (String s : speedrunners) {
                Player player = Bukkit.getPlayerExact(s);
                if(player != null) {
                    player.sendTitle(ChatColor.DARK_PURPLE + "START!", "", 0, 20, 10);
                }
            }
            playersMessage(ChatColor.AQUA + "START!");
            seconds = main.getConfig().getInt("headStartDuration");
            game = new BukkitRunnable() {
                @Override
                public void run() {
                    for(int i = 0; i < speedrunners.size(); i++) {
                        speedrunner = Bukkit.getPlayerExact(speedrunners.get(i));
                        if(speedrunner.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
                            locNether.set(i, speedrunner.getLocation());
                        } else if(speedrunner.getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
                            locWorld.set(i, speedrunner.getLocation());
                        } else if(speedrunner.getWorld().getEnvironment().equals(World.Environment.THE_END)) {
                            locTheEnd.set(i, speedrunner.getLocation());
                        }
                    }
                    for(int i = 0; i < hunters.size(); i++) {
                        hunter = Bukkit.getPlayerExact(hunters.get(i));
                        if(compassMode.get(i).equals("0")) {
                            target = null;
                            finalDistance = Double.MAX_VALUE;
                            for (String s : speedrunners) {
                                speedrunner = Bukkit.getPlayerExact(s);
                                if (hunter.getWorld().getEnvironment().equals(speedrunner.getWorld().getEnvironment())) {
                                    distance = getDistance(hunter.getLocation(), speedrunner.getLocation());
                                    if (distance < finalDistance) {
                                        finalDistance = distance;
                                        target = speedrunner;
                                    }
                                }
                            }
                            if(target == null) {
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
                            if(target == null) {
                                meta.setDisplayName(ChatColor.RED + whichSpeedrunner.get(i) + " is not online!");
                            } else {
                                if(!target.getWorld().getEnvironment().equals(hunter.getWorld().getEnvironment())) {
                                    meta.setDisplayName(ChatColor.RED + target.getName() + " is not in this dimension!");
                                } else {
                                    meta.setDisplayName(ChatColor.GOLD + "Tracking: " + ChatColor.GREEN + whichSpeedrunner.get(i));
                                }
                                if(target.getWorld().getEnvironment().equals(hunter.getWorld().getEnvironment()) && main.getConfig().getBoolean("trackPortals")) {
                                    if(hunter.getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
                                        meta.setLodestone(locWorld.get(speedrunners.indexOf(target.getName())));
                                    } else if(hunter.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
                                        meta.setLodestone(locNether.get(speedrunners.indexOf(target.getName())));
                                    } else if(hunter.getWorld().getEnvironment().equals(World.Environment.THE_END)) {
                                        meta.setLodestone(locTheEnd.get(speedrunners.indexOf(target.getName())));
                                    }
                                }
                            }
                            hunter.getInventory().getItem(compassSlot(hunter)).setItemMeta(meta);
                        }
                    }
                }
            }.runTaskTimer(main, 0, 1);
            return true;
        }
        if (args[0].equals("reset")) {
            if(args.length != 1) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /manhunt help");
                return true;
            }
            p.sendMessage(ChatColor.AQUA + "Manhunt game has been reset!");
            reset();
            return true;
        }
        if (args[0].equals("pause")) {
            if(args.length != 1) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /manhunt help");
                return true;
            }
            if(!inGame) {
                p.sendMessage(ChatColor.RED + "There is no running manhunt game!");
                return true;
            }
            if(!(hunters.contains(p.getName()) || speedrunners.contains(p.getName()))) {
                p.sendMessage(ChatColor.RED + "You are not in a manhunt game!");
                return true;
            }
            if(pause == hunters.size() + speedrunners.size()) {
                p.sendMessage(ChatColor.RED + "Game is already paused!");
                return true;
            }
            if(pausePlayers.contains(p.getName())) {
                p.sendMessage(ChatColor.RED + "You have already voted to pause the game!");
                return true;
            }
            pause +=1;
            playersMessage(ChatColor.AQUA + p.getName() + " wants to pause the game! (" + pause + "/" + (hunters.size() + speedrunners.size()) + ")");
            pausePlayers.add(p.getName());
            if(pause == hunters.size() + speedrunners.size()) {
                pausing.cancel();
                unpause = 0;
                unpausePlayers.clear();
                for (String s : hunters) {
                    Player player = Bukkit.getPlayerExact(s);
                    if(player != null) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200000, 255));
                    }
                }
                for (String s : speedrunners) {
                    Player player = Bukkit.getPlayerExact(s);
                    if(player != null) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200000, 255));
                    }
                }
                playersMessage(ChatColor.AQUA + "Game paused!");
                return true;
            }
            if(pause == 1) {
                pausing = new BukkitRunnable() {
                    @Override
                    public void run() {
                        pause = 0;
                        pausePlayers.clear();
                        playersMessage(ChatColor.AQUA + "Voting for pause has expired");
                    }
                }.runTaskLater(main, 1200);
            }
            return true;
        }
        if (args[0].equals("unpause")) {
            if(args.length != 1) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /manhunt help");
                return true;
            }
            if(!inGame) {
                p.sendMessage(ChatColor.RED + "There is no running manhunt game!");
                return true;
            }
            if(!(hunters.contains(p.getName()) || speedrunners.contains(p.getName()))) {
                p.sendMessage(ChatColor.RED + "You are not in a manhunt game!");
                return true;
            }
            if (unpause == hunters.size() + speedrunners.size()) {
                p.sendMessage(ChatColor.RED + "Game is not paused!");
                return true;
            }
            if (unpausePlayers.contains(p.getName())) {
                p.sendMessage(ChatColor.RED + "You have already voted to unpause the game!");
                return true;
            }
            unpause += 1;
            playersMessage(ChatColor.AQUA + p.getName() + " wants to unpause the game! (" + unpause + "/" + (hunters.size() + speedrunners.size()) + ")");
            unpausePlayers.add(p.getName());
            if (unpause == hunters.size() + speedrunners.size()) {
                unpausing.cancel();
                pause = 0;
                pausePlayers.clear();
                playersMessage(ChatColor.AQUA + "Game unpaused!");
                for (String s : hunters) {
                    Player player = Bukkit.getPlayerExact(s);
                    if(player != null) {
                        player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 255));
                    }
                }
                for (String s : speedrunners) {
                    Player player = Bukkit.getPlayerExact(s);
                    if(player != null) {
                        player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 255));
                    }
                }
                return true;
            }
            if(unpause == 1) {
                unpausing = new BukkitRunnable() {
                    @Override
                    public void run() {
                        unpause = 0;
                        unpausePlayers.clear();
                        playersMessage(ChatColor.AQUA + "Voting for unpause has expired");
                    }
                }.runTaskLater(main, 1200);
            }
            return true;
        }
        if (args[0].equals("list")) {
            if(args.length != 1) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /manhunt help");
                return true;
            }
            if(speedrunners.size() + hunters.size() == 0) {
                p.sendMessage(ChatColor.RED + "There is no player in your game!");
                return true;
            }
            p.sendMessage(ChatColor.GREEN + "------- " + ChatColor.WHITE + " Minecraft Manhunt " + ChatColor.GREEN + "----------");
            if(speedrunners.size() > 0) {
                p.sendMessage(ChatColor.GOLD + "Speedrunners:");
            }
            for (String s : speedrunners) {
                p.sendMessage(ChatColor.AQUA + s);
            }
            if(hunters.size() > 0) {
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
    public static void reset() {
        for (String s : hunters) {
            Player player = Bukkit.getPlayerExact(s);
            if(player != null) {
                player.getInventory().clear(compassSlot(player));
            }
        }
        if(inGame) {
            if(seconds != main.getConfig().getInt("headStartDuration")) {
                starting.cancel();
            } else {
                game.cancel();
            }
            if(main.getConfig().getBoolean("takeAwayOps")) {
                for(int i = 0; i < hunters.size(); i++) {
                    Player tar = Bukkit.getPlayerExact(hunters.get(i));
                    if(tar != null) {
                        tar.setOp(hOps.get(i));
                    }
                }
                for(int i = 0; i < speedrunners.size(); i++) {
                    Player tar = Bukkit.getPlayerExact(speedrunners.get(i));
                    if(tar != null) {
                        tar.setOp(sOps.get(i));
                    }
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
        for(int i = 0; i < 41; i++) {
            if(i == 36) {
                i = 40;
            }
            if(p.getInventory().getItem(i) != null) {
                if(p.getInventory().getItem(i).hasItemMeta()) {
                    if(p.getInventory().getItem(i).getItemMeta().hasLore()) {
                        if(p.getInventory().getItem(i).getItemMeta().getLore().get(0).equals(ChatColor.BLUE + "This compass is to track speedrunners!")) {
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
            if(player != null) {
                player.sendMessage(s);
            }
        }
        for (String value : speedrunners) {
            Player player = Bukkit.getPlayerExact(value);
            if(player != null) {
                player.sendMessage(s);
            }
        }
    }
}