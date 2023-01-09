package com.gw2.discordbot;

import java.util.Timer;
import java.util.TimerTask;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class DiscordBot {
    
    public static JDA jda;
    public static SlashCommandData[] commandData;
    public static String token;

    static {
        commandData = new SlashCommandData[] {
            new SlashCommandData("invite", "Gives you an invite to this guild.", false, new Option(OptionType.BOOLEAN, "private", "Whether this message is private or not. Default: true", false)),
            new SlashCommandData("contact-developer", "Prompts you to send a support ticket.", false),
            new SlashCommandData("shutdown", "Shuts down the bot.", true),
            new SlashCommandData("qtpfires", "Gives the optimal QTP fires.", false),
            new SlashCommandData("signup", "Signs you up for the static raid.", false),
            new SlashCommandData("unsignup", "Unsigns you from the static raid.", false),
            new SlashCommandData("signupform", "Sends the signup form.", false),
            new SlashCommandData("signupcheck", "Check signups!", false),
            new SlashCommandData("signupclear", "Clears the current week's signups.", true),
            new SlashCommandData("signupplayer", "Signs up a player to the current week's signups.", true, new Option(OptionType.USER, "user", "Which user do you wish to sign up?", true)),
            new SlashCommandData("signupdelete", "Deletes a signup by will.", true),
            new SlashCommandData("signupsheet", "Returns the signup sheet.", true),
            new SlashCommandData("signupcheckmyloadout", "Returns your loadout for this week's static.", false),
            new SlashCommandData("staticaddtryout", "Command that lets you add a tryout for the static.", true, new Option(OptionType.USER, "user", "User to add as a tryout", true)),
            new SlashCommandData("staticrejecttryout", "Command that lets you reject a tryout for the static", true, new Option(OptionType.USER, "user", "User to reject for this static", true)),
            new SlashCommandData("staticaddplayer", "Command that lets you add this member to the static.", true, new Option(OptionType.USER, "user", "User you wish to add to the static.", true)),
            new SlashCommandData("staticaddbackup", "Command that lets you add this member as a backup to the static.", true, new Option(OptionType.USER, "user", "User you wish to add to the static.", true)),
            new SlashCommandData("staticremoveplayer", "Command that removes a player from the static.", true),
            new SlashCommandData("staticplayersget", "Returns a list of static members and they roles.", false),
            new SlashCommandData("announce", "Announces a message to the discord server.", true),
            new SlashCommandData("apistatus", "Returns the status(es) of used API(s).", false),
            new SlashCommandData("startstaticraid", "Starts the static raid time.", true, new Option(OptionType.INTEGER, "minutes_to_wait", "How many minutes before I ping everyone for the start?", true)),
            new SlashCommandData("stopstaticraid", "Stops the static raid time.", true),
            new SlashCommandData("purge", "Purges a number of messages.", true, new Option(OptionType.INTEGER, "number", "How many messages to purge?", true)),
            new SlashCommandData("gw2dailies", "Displays currently active dailies.", false),
            new SlashCommandData("gw2accountraidinfo", "Displays information about your weekly raids. [API key req.]", false),
            new SlashCommandData("gw2account", "Displays information about your guild wars 2 profile. [API key req.]", false),
            new SlashCommandData("resetslashcommands", "Resets the slash commands of this server", true),
            new SlashCommandData("gw2character",  "Displays information about your character in guild wars 2. [API key req.]", false),
            new SlashCommandData("profile", "Displays your profile for this server.", false),
            new SlashCommandData("help", "Lists all commands from this bot.", false),
            new SlashCommandData("ping", "Returns the ping of the bot.", false),
            new SlashCommandData("add_api",  "Adds your API key to the bot.", false, new Option(OptionType.STRING, "apikey", "The designated API key you wish to assign to your account.", true)),
            new SlashCommandData("delete_api", "Deletes your APi key from the bot, if you had already added it.", false),
            new SlashCommandData("get_api", "Lists your API key that you added.", false),
            new SlashCommandData("get_raid_json", "Gets json raid thing.", true),
            new SlashCommandData("test", "Tests a dev command...", true),
            new SlashCommandData("startserver", "Starts the server for receiving dps.reports.", true)
        };
    }

    public DiscordBot() throws LoginException, InterruptedException {
        token = Token.getLoginToken();
    
        jda = JDABuilder.createLight(token, 
        GatewayIntent.GUILD_MESSAGES, 
        GatewayIntent.MESSAGE_CONTENT, 
        GatewayIntent.GUILD_MEMBERS, 
        GatewayIntent.GUILD_PRESENCES,
        GatewayIntent.GUILD_MESSAGE_REACTIONS).addEventListeners(
            new JoinEvent(),
            new LeaveEvent(),
            new SlashCommandInteraction(),
            new CharacterChoosingSelectMenu(),
            new MessageEVTCLoggingEvent(),
            new HelpButtonEventListener(),
            new NewUserReactEmoteEvent(),
            new ModalContactDeveloper(),
            new ModalAnnouncement(),
            new SignupExcelWriting(),
            new SignupDeleteMenu(),
            new StaticSlashCommandInteraction(),
            new Gw2SlashCommandInteraction(),
            new ButtonSignUpPressEvent(),
            new StaticApplyEvents(),
            new StaticMemberRemoveEvent()
        ).enableCache(CacheFlag.CLIENT_STATUS).build();
    
        Thread threadActivity = new Thread(new Runnable() {

            public int currentIndex = 0;		
            
            public String[] messages = {
                    "[BA]",
                    "Use / for commands",
                    "/help",
                    "by NenadG",
                    "v4.0",
                    "new PAID hosting!"
            };
            
            @Override
            public void run() {
                new Timer().schedule(new TimerTask() {	
                    public void run() {	
                        jda.getPresence().setActivity(Activity.playing(messages[currentIndex]));	
                        currentIndex=(currentIndex+1)%messages.length;  

                    }}, 0, 60 * 1000);	
                
                    Logging.LOG(Main.class, "Activity thread started!");
            }
        });
        
        threadActivity.start();

        jda.awaitReady();

        // -.- ADDING COMMANDS -.-

        for(SlashCommandData data : commandData) {
            data.addGlobally(jda);
        }

        SlashCommandData.insertIntoJson(commandData);

        new Timer().schedule(new TimerTask() {
            
            public void run() {
                System.out.println("Restarting...");

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.exit(0);

                this.cancel();
            }

        }, 48 * 60 * 60 * 1000);
    }

}
