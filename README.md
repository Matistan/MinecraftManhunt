# Minecraft Manhunt

---

View on [Spigot](https://www.spigotmc.org/resources/manhunt.109010/) • 
Inspired by [Dream](https://www.youtube.com/@dream) • 
Download [here](https://github.com/Matistan/MinecraftManhunt/releases)

---

> **Having issues?** Feel free to report them on the [Issues tab](https://github.com/Matistan/MinecraftManhunt/issues). I'll be glad to hear your opinion about the plugin as well as extra features you would like me to add!

## Welcome to readme!

Hi! I just want to thank you for your interest in this plugin. I put a lot of effort into this project and I would really love someone to use it!

### Minecraft version

This plugin runs on a Minecraft version 1.16+.

## What is Manhunt?

There are two teams: hunters and speedrunners. Hunters have to kill all the speedrunners before one of them beats the ender dragon. Hunters can locate speedrunners using a compass.

## Features

- Compass has two modes:
- - Tracks every speedrunner individually,
- - Or tracks the nearest one
- Headstart support
- Heals and feeds players on start
- Clear inventories and resets achievements on start
- Time set day and weather clear on start
- When a speedrunner is in a different dimension, compass will display it and will track their portal
- Need to take a break during the game? Have to go to the restroom? I know that the manhunt game can last even few hours, so I've made the feature to pause the game! During this time, you're invincible and no one can move, so you'll be safe. Then you can resume the game and continue playing!

## How to use it

- drag the .jar file from the [Release tab](https://github.com/Matistan/MinecraftManhunt/releases) to your plugins folder on your server.
- add players to your game using `/manhunt add <role> <player> <player> ... `
- type `/manhunt start` to start the match!

## Commands

- `/manhunt add <role> <player> <player> ...` - adds players with roles
- `/manhunt add <role> @a` - adds all players with roles
- `/manhunt remove <player> <player> ...` - removes players
- `/manhunt remove @a` - removes all players
- `/manhunt start` - starts a game
- `/manhunt reset` - resets a game
- `/manhunt pause` - pauses a game
- `/manhunt unpause` - resumes a game
- `/manhunt list` - shows a list of players in a manhunt game with their roles
- `/manhunt help` - shows a list of manhunt commands

## Configuration Options

Edit the `plugins/MinecraftManhunt/config.yml` file to change the following options:

| Key                 | Description                                                                                 | Type    | recommended                                                   |
|---------------------|---------------------------------------------------------------------------------------------|---------|---------------------------------------------------------------|
| timeSetDayOnStart   | Set to true to set the time to day automatically when the game starts.                      | boolean | true                                                          |
| weatherClearOnStart | Set to true to set the weather to clear automatically when the game starts.                 | boolean | true                                                          |
| takeAwayOps         | Set to true to take away OPs for the duration of the game.                                  | boolean | true                                                          |
| clearInventories    | Set to true to clear players inventories when the game starts.                              | boolean | true                                                          |
| headStartDuration   | Set the time for speedrunners to run away.                                                  | int     | 10                                                            |
| speedrunnersLives   | Set the amount of lives for speedrunners.                                                   | int     | 1                                                             |
| trackPortals        | Set to true to enable tracking speedrunners portals when they are in a different dimension. | boolean | true                                                          |
| trackNearestMode    | Set to true to enable mode on the compass to track the nearest speedrunner.                 | boolean | true                                                          |
| teleport            | Set to true to teleport everyone in the same spot when the game starts.                     | boolean | true                                                          |
| compassMenu         | Set to true to open a menu to choose which speedrunner to track.                            | boolean | false; true if you're playing with a lot of speedrunners      |
| enablePauses        | Set to true to enable the pause feature (/manhunt pause).                                   | boolean | true                                                          |
| usePermissions      | Set to true to require users to have permission to use certain commands.                    | boolean | false; true if you don't trust the people you're playing with |

## Permissions

If `usePermissions` is set to `true` in the `config.yml` file, players without ops will need the following permissions to use the commands:

| Permission      | Description                                              |
|-----------------|----------------------------------------------------------|
| manhunt.manhunt | Allows the player to use all `/manhunt` commands.        |
| manhunt.add     | Allows the player to use the `/manhunt add` command.     |
| manhunt.remove  | Allows the player to use the `/manhunt remove` command.  |
| manhunt.start   | Allows the player to use the `/manhunt start` command.   |
| manhunt.reset   | Allows the player to use the `/manhunt reset` command.   |
| manhunt.list    | Allows the player to use the `/manhunt list` command.    |
| manhunt.help    | Allows the player to use the `/manhunt help` command.    |

### Bugs & Issues

> **Having issues?** Feel free to report them on the [Issues tab](https://github.com/Matistan/MinecraftManhunt/issues). I'll be glad to hear your opinion about the plugin as well as extra features you would like me to add!


Made by [Matistan](https://github.com/Matistan)
