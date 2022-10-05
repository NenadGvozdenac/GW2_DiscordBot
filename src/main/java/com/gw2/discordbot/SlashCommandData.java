package com.gw2.discordbot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.RestAction;

@SuppressWarnings("null")
public class SlashCommandData {
    
    private String commandName;
    private String commandDescription;
    private Boolean adminOnly;

    private List<Option> options;

    public SlashCommandData(String commandName, String commandDescription, Boolean adminOnly, Option... options) {
        this.commandName = commandName;
        this.commandDescription = commandDescription + (adminOnly ? " `(ADMIN)`" : "");
        this.adminOnly = adminOnly;
        this.options = new ArrayList<>();

        for(Option option : options) {
            this.options.add(option);
        }
    }

    public String getCommandName() {
        return this.commandName;
    }

    public String getCommandDescription() {
        return this.commandDescription;
    }

    public Boolean getAdminOnly() {
        return this.adminOnly;
    }

    @Override
    public String toString() {
        return "[name=" + this.commandName + ", description=" + this.commandDescription + ", adminOnly=" + this.adminOnly + "]";
    }

    public void addToGuild(Guild guildById) {
        RestAction<List<Command>> listOfCommandsRestAction = guildById.retrieveCommands();

        listOfCommandsRestAction.queue((List<Command> listOfCommands) -> {
            for(Command c : listOfCommands) {
                if(c.getName().equals(this.commandName) && c.getDescription().equals(this.commandDescription)) {
                    return;
                }
            }
    
            if(options.size() == 0) {
                net.dv8tion.jda.api.interactions.commands.build.SlashCommandData data = Commands.slash(this.commandName, this.commandDescription);
        
                if(adminOnly) {
                    data.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR));
                }
        
                System.out.println("Command added into server: '" + guildById.getName() + "': " + this.toString());
    
                guildById.upsertCommand((CommandData) data).queue();
            } else {
                net.dv8tion.jda.api.interactions.commands.build.SlashCommandData data = Commands.slash(this.commandName, this.commandDescription);
                            
                for(Option option : this.options) {
                    data.addOption(option.getOptionType(), option.getOptionName(), option.getOptionDesc(), option.getMandatoryOption());
                }
                            
                if(adminOnly) {
                    data.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR));
                }
                            
                System.out.println("Command added into server: '" + guildById.getName() + "': " + this.toString());
    
                guildById.upsertCommand((CommandData) data).queue();
            }
        });
    }

    public void addGlobally(JDA jda) {
        RestAction<List<Command>> listOfCommandsRestAction = DiscordBot.jda.retrieveCommands();

        listOfCommandsRestAction.queue((List<Command> listOfCommands) -> {
            for(Command c : listOfCommands) {
                if(c.getName().equals(this.commandName) && c.getDescription().equals(this.commandDescription)) {
                    return;
                }
            }
    
            if(options.size() == 0) {
                net.dv8tion.jda.api.interactions.commands.build.SlashCommandData data = Commands.slash(this.commandName, this.commandDescription);
        
                if(adminOnly) {
                    data.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR));
                }
        
                System.out.println("Command added globally: '" + DiscordBot.jda.getSelfUser().getName() + "': " + this.toString());
    
                DiscordBot.jda.upsertCommand((CommandData) data).queue();
            } else {
                net.dv8tion.jda.api.interactions.commands.build.SlashCommandData data = Commands.slash(this.commandName, this.commandDescription);
                            
                for(Option option : this.options) {
                    data.addOption(option.getOptionType(), option.getOptionName(), option.getOptionDesc(), option.getMandatoryOption());
                }
                            
                if(adminOnly) {
                    data.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR));
                }
                            
                System.out.println("Command added globally: '" + DiscordBot.jda.getSelfUser().getName() + "': " + this.toString());
    
                DiscordBot.jda.upsertCommand((CommandData) data).queue();
            }

        });
    }

    
    public static void insertIntoJson(SlashCommandData[] commandData) {

        try (FileWriter writer = new FileWriter(new File(new File("jsonFolder"), "commands.json"))) {

            Gson gson = new GsonBuilder()
                        .disableHtmlEscaping()
                        .setFieldNamingStrategy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                        .setPrettyPrinting()
                        .serializeNulls()
                        .create();
   
            String insertString = gson.toJson(commandData);
            writer.write(insertString);

            Logger logger = LoggerFactory.getLogger(SlashCommandData.class);
            logger.info("Commands inserted into JSON file");
   
        } catch(IOException e) {
            System.err.println("Unforunately, the JSON file couldn't be opened, and therefore commands couldn't be inserted.");
            DiscordBot.jda.shutdown();
        }
    }
}
