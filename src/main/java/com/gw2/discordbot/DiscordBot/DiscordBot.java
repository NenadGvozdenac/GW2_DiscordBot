package com.gw2.discordbot.DiscordBot;

import java.util.Timer;
import java.util.TimerTask;

import javax.security.auth.login.LoginException;

import com.gw2.discordbot.Events.ButtonSignUpPressEvent;
import com.gw2.discordbot.Events.CharacterChoosingSelectMenu;
import com.gw2.discordbot.Events.Gw2SlashCommandInteraction;
import com.gw2.discordbot.Events.HelpButtonEventListener;
import com.gw2.discordbot.Events.JoinEvent;
import com.gw2.discordbot.Events.LeaveEvent;
import com.gw2.discordbot.Events.MessageCommands;
import com.gw2.discordbot.Events.MessageEVTCLoggingEvent;
import com.gw2.discordbot.Events.ModalAnnouncement;
import com.gw2.discordbot.Events.SignupDeleteMenu;
import com.gw2.discordbot.Events.SlashCommandData;
import com.gw2.discordbot.Events.SlashCommandInteraction;
import com.gw2.discordbot.Events.StaticApplyEvents;
import com.gw2.discordbot.Events.StaticMemberRemoveEvent;
import com.gw2.discordbot.Events.StaticSlashCommandInteraction;
import com.gw2.discordbot.Miscellaneous.AutocompleteTimezone;
import com.gw2.discordbot.Miscellaneous.ModalContactDeveloper;
import com.gw2.discordbot.Miscellaneous.NewUserReactEmoteEvent;
import com.gw2.discordbot.Miscellaneous.SignupExcelWriting;

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

            new SlashCommandData("raid_signup", "Signs you up for the static raid.", false),
            new SlashCommandData("raid_unsignup", "Unsigns you from the static raid.", false),
            new SlashCommandData("raid_signup_form", "Sends the signup form.", false),
            new SlashCommandData("raid_signup_check", "Check signups!", false),
            new SlashCommandData("raid_signup_clear", "Clears the current week's signups.", true),
            new SlashCommandData("raid_signup_player", "Signs up a player to the current week's signups.", true, new Option(OptionType.USER, "user", "Which user do you wish to sign up?", true)),
            new SlashCommandData("raid_signup_delete", "Deletes a signup by will.", true),
            new SlashCommandData("raid_signup_sheet", "Returns the signup sheet.", true),
            new SlashCommandData("raid_signup_check_my_loadout", "Returns your loadout for this week's static.", false),
            new SlashCommandData("raid_static_add_tryout", "Command that lets you add a tryout for the static.", true, new Option(OptionType.USER, "user", "User to add as a tryout", true)),
            new SlashCommandData("raid_static_reject_tryout", "Command that lets you reject a tryout for the static", true, new Option(OptionType.USER, "user", "User to reject for this static", true)),
            new SlashCommandData("raid_static_add_player", "Command that lets you add this member to the static.", true, new Option(OptionType.USER, "user", "User you wish to add to the static.", true)),
            new SlashCommandData("raid_static_add_backup", "Command that lets you add this member as a backup to the static.", true, new Option(OptionType.USER, "user", "User you wish to add to the static.", true)),
            new SlashCommandData("raid_static_remove_player", "Command that removes a player from the static.", true),
            new SlashCommandData("raid_static_players_get", "Returns a list of static members and they roles.", false),

            new SlashCommandData("strikes_signup", "Signs you up for the static raid.", false),
            new SlashCommandData("strikes_unsignup", "Unsigns you from the static raid.", false),
            new SlashCommandData("strikes_signup_form", "Sends the signup form.", false),
            new SlashCommandData("strikes_signup_check", "Check signups!", false),
            new SlashCommandData("strikes_signup_clear", "Clears the current week's signups.", true),
            new SlashCommandData("strikes_signup_player", "Signs up a player to the current week's signups.", true, new Option(OptionType.USER, "user", "Which user do you wish to sign up?", true)),
            new SlashCommandData("strikes_signup_delete", "Deletes a signup by will.", true),
            new SlashCommandData("strikes_signup_sheet", "Returns the signup sheet.", true),
            new SlashCommandData("strikes_signup_check_my_loadout", "Returns your loadout for this week's static.", false),
            new SlashCommandData("strikes_static_add_tryout", "Command that lets you add a tryout for the static.", true, new Option(OptionType.USER, "user", "User to add as a tryout", true)),
            new SlashCommandData("strikes_static_reject_tryout", "Command that lets you reject a tryout for the static", true, new Option(OptionType.USER, "user", "User to reject for this static", true)),
            new SlashCommandData("strikes_static_add_player", "Command that lets you add this member to the static.", true, new Option(OptionType.USER, "user", "User you wish to add to the static.", true)),
            new SlashCommandData("strikes_static_add_backup", "Command that lets you add this member as a backup to the static.", true, new Option(OptionType.USER, "user", "User you wish to add to the static.", true)),
            new SlashCommandData("strikes_static_remove_player", "Command that removes a player from the static.", true),
            new SlashCommandData("strikes_static_players_get", "Returns a list of static members and they roles.", false),

            new SlashCommandData("raid_static_add_signup_form", "Adds a signup form for the static.", false, new Option(OptionType.STRING, "image_link", "Link to your image!", true)),
            new SlashCommandData("strikes_static_add_signup_form", "Adds a signup form for the static.", false, new Option(OptionType.STRING, "image_link", "Link to your image!", true)),
            
            new SlashCommandData("announce", "Announces a message to the discord server.", true),
            new SlashCommandData("apistatus", "Returns the status(es) of used API(s).", false),

            new SlashCommandData("start_static_raid", "Starts the static raid time.", true, new Option(OptionType.INTEGER, "minutes_to_wait", "How many minutes before I ping everyone for the start?", true)),
            new SlashCommandData("start_static_strikes", "Starts the static raid time.", true, new Option(OptionType.INTEGER, "minutes_to_wait", "How many minutes before I ping everyone for the start?", true)),
            
            new SlashCommandData("stop_static_raid", "Stops the static raid time.", true),
            new SlashCommandData("purge", "Purges a number of messages.", true, new Option(OptionType.INTEGER, "number", "How many messages to purge?", true)),
            
            new SlashCommandData("gw2dailies", "Displays currently active dailies.", false),
            new SlashCommandData("gw2accountraidinfo", "Displays information about your weekly raids. [API key req.]", false),
            new SlashCommandData("gw2account", "Displays information about your guild wars 2 profile. [API key req.]", false),
            
            new SlashCommandData("staticaddsignupform", "Adds a picture to be displayed for the sheet.", true, new Option(OptionType.STRING, "image_link", "The link to the image", true)),
            new SlashCommandData("resetslashcommands", "Resets the slash commands of this server", true),
            
            new SlashCommandData("gw2character",  "Displays information about your character in guild wars 2. [API key req.]", false),
            new SlashCommandData("calculate_time", "Calculate time which you want.", false, new Option(OptionType.INTEGER, "day", "The day happening.", true), new Option(OptionType.INTEGER, "month", "The month happening.", true), new Option(OptionType.INTEGER, "year", "The year happening.", true), new Option(OptionType.INTEGER, "hour", "The hour happening.", true), new Option(OptionType.INTEGER, "minute", "The minute happening", true), new Option(OptionType.STRING, "timezone", "Your timezone.", true, true)),
            new SlashCommandData("profile", "Displays your profile for this server.", false),
            new SlashCommandData("help", "Lists all commands from this bot.", false),
            new SlashCommandData("ping", "Returns the ping of the bot.", false),
            
            new SlashCommandData("add_api",  "Adds your API key to the bot.", false, new Option(OptionType.STRING, "apikey", "The designated API key you wish to assign to your account.", true)),
            new SlashCommandData("delete_api", "Deletes your APi key from the bot, if you had already added it.", false),
            
            new SlashCommandData("get_api", "Lists your API key that you added.", false),
            new SlashCommandData("get_raid_json", "Gets json raid thing.", true),
            
            new SlashCommandData("test", "Tests a dev command...", true, new Option(OptionType.STRING, "longstringofbosses", "The long string of bosses. \nFORMAT: \nhttps://dps.report/...\nhttps://dps.report/...", true)),
            
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
            new StaticMemberRemoveEvent(),
            new MessageCommands(),
            new AutocompleteTimezone()
        ).enableCache(CacheFlag.CLIENT_STATUS).build();
    
        Thread threadActivity = new Thread(new Runnable() {

            public int currentIndex = 0;		
            
            public String[] messages = {
                    "v5.0",
                    "Helping the skritts!",
                    "Working for minimal wage.",
                    "Love blackjack! 🃏",
                    "NenadG made me.",
                    "Hate it here. ☕",
                    "( ͡° ͜ʖ ͡°)"
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
    }
}