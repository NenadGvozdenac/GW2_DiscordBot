# GW2 Discord Bot
- New project I've been working on - GW2 Discord Bot.
- Brief overview: GW2 is an MMORPG game that bases around free open-world gameplay, along with high-end gameplay.
- It has three main gamemodes: PvE, PvP and WvW. 
- This bot tries to help the users of my Discord Server by providing useful PvE information.

# Newest update: v3.3.0
- Added new static commands and linked them to discord members.
- Now, there is another sheet that just has members and their original roles.
- Static members are there by default when they join, and if for some reason we remove them, there is a command for that.

# Newest update: v3.2.0
- Fixed daily achievements sometimes not being sent, by utilising asynchronous functions.
- Added buttons to `/signup` and `/signupplayer` messages. 
- `Cancel` button cancels the action, editing the message, and removing the ActionRows.
- `Help` button removes the ActionRows, and sends the message explaining the command to the user.
- Formatted the dps.reports logging, now indicating the group's dps.

# Update: v3.0.0
- Separated commands into three files: `StaticSlashCommandInteraction.java`, `SlashCommandInteraction.java` & `Gw2SlashCommandInteraction.java`
- Added new commands to help with static needs:
- `/signupsheet`, admin command that returns the data storage for all static needs
- `/signupcheckmyloadout`, public command that returns the loadout required by the player for that static raid
- Made all static commands not doable in DM with the bot. It just felt weird.
- `/startstaticraid`, admin command that starts the raid, now pings everyone at the start. It also lists everyone's roles below the ping.
- `/startstaticraid`, also now takes an input of minutes. After minutes have passed, everyone will be pinged. It opens the port on the server for logs to flow into. After 4 hours, if the port is still open, it closes the port. (Administrator probably forgot to close it manually)
- `/stopstaticraid`, only stops the server.

# Update: v2.1.0
- Added an AUTOUPLOADER that works on the basis of Client-Server.
- On the hosting server, there exists a possibility to have a server. A server is hosted on a certain port.
- The server can be created by doing `/startstaticraid`, which makes the server active.
- Upon doing `/stopstaticraid`, the server deactivates.
- This server is one of the features for automatic uploading logs.
- A client application (also made), sends the log to the server via a link.
- Server responds if they got the file, and if the return is **200**, log is uploaded to a discord text channel.

- Added static commands that will be used for the static group I am leader of!
- `/signup`, signs the person up for a certain role on that week.
- `/unsignup`, unsigns the person from this week's raids.
- `/signupform`, gives the person the form for the signups.

- `/signupcheck`, admin command that lets the admins see the list of people signed up and their roles.
- `/signupdelete`, admin command that lets the admins delete a signup forcefully.
- `/signupplayer`, admin command that lets the admins sign up a person forcefully.
- `/signupclear`, admin command that lets the admins clear all signups for that week.

- A sheet is stored in the database of the server. All data is saved there, if need comes that it should be retrieved.

- `/qtpfires`, gives the picture of the qadim the peerless fires that are optimal.

# Features
- This bot comes with many features.
- Current version: v1.1.0
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

### Daily achievements
* This bot has the ability to automatically send updates when new achievements drop daily.
* It will send achievements to the discord server.

### Welcome & Leave messages
* Bot is able to send welcome and leave messages whenever a user joins/leaves the guild the bot is in.
* It also keeps track of current users.