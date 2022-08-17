# GW2 Discord Bot
- New project I've been working on - GW2 Discord Bot.
- Brief overview: GW2 is an MMORPG game that bases around free open-world gameplay, along with high-end gameplay.
- It has three main gamemodes: PvE, PvP and WvW. 
- This bot tries to help the users of my Discord Server by providing useful PvE information.

# Features
- This bot comes with many features.
- Current version: v1.0.1
- List of features will be listed below

### ARCDPS log uploading: 
* By dragging a .zevtc log anywhere in the server, your log will be uploaded to DPS.REPORT.
* This is especially helpful.
* It utilises the DPS.REPORT api POST method for posting logs, and by returning some information about the log, the bot is able to give you some information about it and also provide a link.
* The user has the ability to look up previous logs in the discord channel that he/she had been previously tagged in.
* It connects automatically to [dps.report api](https://dps.report/api).

### ACCOUNT information:
* This bot, connecting to GW2 api, has the ability to send you information about your account.
* This information can vary from what you wish to seek: account / character info / daily fractal info, etc.
* By using `/gw2account`, your account stats are shown to you.
* By using `/gw2character`, your character's stats are shown to you. You also have the ability to select which character you wish to see stats of.
* Added API keys are stored in a json file that is secretly saved.

### GW2 News & Patch Notes:
* This bot has been automated to look for new messages from the official [Guild Wars 2](https://www.guildwars2.com/en-gb/) page.
* Whenever there is news, the bot will send a message to a discord channel that had been previously named correctly.
* This bot has been automated to also look for news messages from [Guild Wars 2 Forums](https://en-forum.guildwars2.com/forum/6-game-update-notes/) for patch notes.

- News, Patch Notes & Logging are all made on separated webhooks, therefore using different threads, maximising performance.

### Hosting 
* Hosting of this bot is done entirely via a HttpServer that is hosted on [repl.it](https://repl.it/). 
* Bot is automatically pinged every 15 minutes (by [UpTimeRobot](https://uptimerobot.com/)), to keep the server active.

### Welcome & Leave messages
* Bot is able to send welcome and leave messages whenever a user joins/leaves the guild the bot is in.
* It also keeps track of current users.
