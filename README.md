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

- Speedrunners-tracking compass: Hunters receive a special compass at the start to be able to locate speedrunners
- When a speedrunner is in a different dimension, compass will say it and will track their portal
- Compass has two modes:
- - Tracks every speedrunner individually
- - Or tracks the nearest one
- You can vote to pause the game! During that time, no one can move. Then you can vote to unpause and continue playing!

## How to use it

- drag the .jar file from the [Release tab](https://github.com/Matistan/MinecraftManhunt/releases) to your plugins folder on your server.
- select players to your game using `/manhunt add <player_name> <role>` command
- type `/manhunt start` to start the match!

## Commands

- `/manhunt add <player name> <role>` - adds a player with a specified role
- `/manhunt remove <player name>` - removes a player
- `/manhunt start` - starts a game
- `/manhunt reset` - resets a game
- `/manhunt pause` - pauses a game
- `/manhunt unpause` - resumes a game, if it's stopped
- `/manhunt list` - shows a list of players in a manhunt game with their roles
- `/manhunt help` - shows a list of manhunt commands

## Configuration Options

Edit the `plugins/MinecraftManhunt/config.yml` file to change the following options:

Key|Description|Type|recommended
--|--|--|--
timeSetDayOnStart | Set to true to set the time to day automatically when the game starts. | boolean | true
weatherClearOnStart | Set to true to set the weather to clear automatically when the game starts. | boolean | true
takeAwayOps | Set to true to take away OPs for the duration of the game. | boolean | true
clearInventories | Set to true to clear players inventories when the game starts. | boolean | true
headStartDuration | Set the time for speedrunners to run away. | int | 10
speedrunnersLives | Set the amount of lives for speedrunners. | int | 1
trackPortals | Set to true to enable tracking speedrunners portals when they are in a different dimension. | boolean | true
trackNearestMode | Set to true to enable tracking the nearest speedrunner on compasses. | boolean | true

### Bugs & Issues

> **Having issues?** Feel free to report them on the [Issues tab](https://github.com/Matistan/MinecraftManhunt/issues). I'll be glad to hear your opinion about the plugin as well as extra features you would like me to add!


Made by [Matistan](https://github.com/Matistan)