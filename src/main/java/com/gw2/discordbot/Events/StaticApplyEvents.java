package com.gw2.discordbot.Events;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gw2.discordbot.DiscordBot.Constants;
import com.gw2.discordbot.DiscordBot.DiscordBot;
import com.gw2.discordbot.DiscordBot.Logging;
import com.gw2.discordbot.DiscordBot.Token;
import com.gw2.discordbot.Miscellaneous.RandomFunnyQuote;
import com.gw2.discordbot.Miscellaneous.SignupExcelWriting;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

public class StaticApplyEvents extends ListenerAdapter {
    
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if(!event.isFromGuild()) {
            if(!event.isAcknowledged()) {
                event.deferReply(true).queue(message -> message.sendMessage("Command isn't usable in DMs...").queue());
                return;
            } else return;
        }

        switch(event.getName()) {
            case "staticaddtryout":
                STATIC_ADD_TRYOUT_EVENT(event);
            break;

            case "staticaddplayer":
                STATIC_ADD_PLAYER_EVENT(event);
            break;

            case "staticaddbackup":
                STATIC_ADD_BACKUP(event);
            break;

            case "staticrejecttryout":
                STATIC_REJECT_TRYOUT(event);
            break;

            case "staticaddsignupform":
                STATIC_ADD_SIGNUP_FORM(event);
            break;

            case "staticremoveplayer":
                STATIC_REMOVE_PLAYER_EVENT(event);
            break;  

            case "staticplayersget":
                STATIC_GET_ALL_PLAYERS_EVENT(event);
            break;
        }
    }

    private void STATIC_ADD_SIGNUP_FORM(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();
        String linkToImage = event.getOption("image_link").getAsString();

        if(!linkToImage.startsWith("http")) {
            event.getHook().sendMessage("Your link is not actually an image. Aborting.").queue();
            return;
        }

        ArrayList<Token> loginTokenObj = Token.readCurrentlyAddedTokens();

        try(FileWriter writer = new FileWriter(new File(new File("jsonFolder"), "token.json"))) {

            Gson gson = new GsonBuilder()
                     .disableHtmlEscaping()
                     .setFieldNamingStrategy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                     .setPrettyPrinting()
                     .serializeNulls()
                     .create();

            for(int i = 0; i < loginTokenObj.size(); i++) {
                if(loginTokenObj.get(i).getTokenName().equalsIgnoreCase("staticSignupForm")) {
                    loginTokenObj.get(i).setTokenValue(linkToImage);
                    writer.write(gson.toJson(loginTokenObj));
                    writer.close();
                    event.getHook().sendMessage("Changed image! If you wish to change it again, run this command again.").queue();
                    return;
                }
            }

            loginTokenObj.add(new Token("staticSignupForm", linkToImage));
            writer.write(gson.toJson(loginTokenObj));

            writer.close();
            Logging.LOG(Token.class, "Added image for signup form: " + linkToImage);

            event.getHook().sendMessage("Added image! If you wish to change it, run this command again.").queue();
        } catch(IOException e1) {
            System.out.println("Error in opening the file.");
        }
    }

    private void STATIC_ADD_BACKUP(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        User user = event.getOption("user").getAsUser();

        event.getGuild().addRoleToMember(event.getGuild().retrieveMember(user).complete(), event.getGuild().getRoleById(Constants.staticBackupRoleID)).queue(
            message ->  {
                event.getHook().sendMessage("`You gave that person a static backup role!`").queue();

                EmbedBuilder eb = new EmbedBuilder();
                eb.setFooter(RandomFunnyQuote.getFunnyQuote());
                eb.setTitle(user.getAsTag());
                eb.setDescription("```" + user.getAsTag() + " got the backup role!```");
    
                event.getGuild().getTextChannelById(Constants.staticApplicationsChannelID).sendMessageEmbeds(eb.build()).queue();

                user.openPrivateChannel().queue(channel -> channel.sendMessage("`You have been given a static backup role!`").queue());
            }
        );
    }

    private void STATIC_REJECT_TRYOUT(SlashCommandInteractionEvent event) {

        event.deferReply(true).queue();

        User user = event.getOption("user").getAsUser();

        Role staticApplicantRole = event.getGuild().getRoleById("1013185863116660838");

        if(event.getGuild().retrieveMember(user).complete().getRoles().contains(staticApplicantRole)) {
            event.getHook().sendMessage("Rejected the user " + user.getAsTag() + " for this static!").queue(message -> 
                {
                    event.getGuild().retrieveMember(user).queue(member -> event.getGuild().removeRoleFromMember(member, staticApplicantRole).queue());
                    user.openPrivateChannel().queue(channel -> channel.sendMessage("```You have been denied your application as a static member, unfortunately. \nYou can apply again if you wish to do so.```").queue());
                }
            );
        } else {
            event.getHook().sendMessage("That user does not contain the applicant role, therefore is not an applicant for this static.").queue();
        }

    }

    private void STATIC_GET_ALL_PLAYERS_EVENT(SlashCommandInteractionEvent event) {

        event.deferReply(true).queue();

        if(!event.isFromGuild()) {
            event.getHook().sendMessage("This command can not be used in DMs.").queue();
            return;
        }

        Role staticRole = event.getGuild().getRoleById("1007918310190501948");

        if(!event.getMember().getRoles().contains(staticRole)) {
            event.getHook().sendMessage("You cannot do that command as of this time.").queue();
            return;
        }

        LinkedHashMap<String, String> playerRolePairs = new LinkedHashMap<>();

        playerRolePairs = SignupExcelWriting.getActiveStaticMembers();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("ALL STATIC MEMBERS");

        String string = "```";

        for(Map.Entry<String, String> entry : playerRolePairs.entrySet()) {
            string += String.format("%-20s | %s\n", DiscordBot.jda.retrieveUserById(entry.getKey()).complete().getName(), entry.getValue());
        }

        string += "```";

        eb.setDescription(string);
        
        event.getHook().sendMessageEmbeds(eb.build()).queue();

    }

    private void STATIC_REMOVE_PLAYER_EVENT(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        ArrayList<String> listOfMembersWithRolesIds = SignupExcelWriting.getAllActiveMembersIds();
        ArrayList<SelectOption> listOfSelectOptions = new ArrayList<>();

        for(String memberId : listOfMembersWithRolesIds) {
            listOfSelectOptions.add(SelectOption.of(DiscordBot.jda.retrieveUserById(memberId).complete().getAsTag(), DiscordBot.jda.retrieveUserById(memberId).complete().getId()).withEmoji(Emoji.fromFormatted("\uD83D\uDD28")));
        }

        StringSelectMenu menu = StringSelectMenu.create("staticremoveplayermenu").setPlaceholder("Select who you wish to remove from the static.").addOptions(listOfSelectOptions).build();

        Button buttonCancel = Button.danger("cancelstaticremove", "CANCEL")
            .withEmoji(Emoji.fromFormatted("\uD83D\uDED1"));
        Button buttonHelp = Button.primary("helpstaticremove", "HELP")
            .withEmoji(Emoji.fromFormatted("\u2753"));

        event.getHook().sendMessage("`Select a player you wish to remove from the static:`").addActionRow(menu).addActionRow(buttonCancel, buttonHelp).queue();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        switch(event.getButton().getId()) {
            case "cancelstaticaddtryout":
                event.deferEdit().setContent("`You cancelled the action.`").setComponents().queue();
            break;

            case "cancelstaticadd":
                event.deferEdit().setContent("`You cancelled the action.`").setComponents().queue();
            break;

            case "cancelstaticremove":
                event.deferEdit().setContent("`You cancelled the action.`").setComponents().queue();
            break;

            case "helpstaticadd":
                event.deferEdit().setContent("```This is a command that lets you add players to the static. \nYou will be prompted with a menu to add the player.\nIf you wish to continue, run this command again.```").setComponents().queue();
            break;

            case "helpstaticaddtryout":
                event.deferEdit().setContent("```This is a command that lets you add a tryout for the static. \nYou will be prompted with a menu to select which position you wish for the tryout to be.\nIf you wish to continue, run this command again.```").setComponents().queue();
            break;

            case "helpstaticremove":
                event.deferEdit().setContent("```This is a command that lets you remove a static member.\nYou will be prompted with a menu to select which player you wish to remove.```").setComponents().queue();
            break;
        }

    }

    private void STATIC_ADD_PLAYER_EVENT(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        if(!event.isFromGuild()) {
            event.getHook().sendMessage("This command can not be used in DMs.").queue();
            return;
        }

        if(event.getUser().equals(event.getJDA().getSelfUser())) {
            event.getHook().sendMessage("How did you message yourself?").queue();
            return;
        }

        Role staticRole = event.getGuild().getRoleById(Constants.staticRoleID);

        if(!event.getMember().getRoles().contains(staticRole)) {
            event.getHook().sendMessage("You cannot do that command as of this time.").queue();
            return;
        }

        User user = event.getOption("user").getAsUser();
        
        Role staticBackupRole = event.getGuild().getRoleById(Constants.staticBackupRoleID);

        if(event.getGuild().retrieveMember(user).complete().getRoles().contains(staticRole) || event.getGuild().retrieveMember(user).complete().getRoles().contains(staticBackupRole)) {
            event.getHook().sendMessage("That user is already part of the static...").queue();
            return;
        }

        if(user.equals(event.getJDA().getSelfUser())) {
            event.getHook().sendMessage("Unfortunately, you cannot add a bot as a static member.").queue();
            return;
        }

        try (FileInputStream file = new FileInputStream(new File("static.xlsx"))) {
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheet("StaticMembers");

            Row firstRow = sheet.getRow(0);
            Row secondRow = sheet.getRow(1);

            ArrayList<String> emptyStaticSlots = new ArrayList<String>();

            for(int i = 1; i < 11; i++) {
                if(firstRow.getCell(i).getStringCellValue().equals("EMPTY")) {
                    emptyStaticSlots.add(secondRow.getCell(i).getStringCellValue());
                }
            }

            ArrayList<String> listOfAvailableSlots = new ArrayList<>();

            for(String option : emptyStaticSlots) {
                if(!listOfAvailableSlots.contains(option)) {
                    listOfAvailableSlots.add(option);
                }
            }

            ArrayList<SelectOption> listOfAvailableSlotsSelectMenu = new ArrayList<>();

            for(String string : listOfAvailableSlots) {
                listOfAvailableSlotsSelectMenu.add(SelectOption.of(string, string).withEmoji(Emoji.fromFormatted("\u2694")));
            }

            StringSelectMenu menu = StringSelectMenu.create("staticaddplayermenu").setPlaceholder("Select from available classes you wish to add the player to.").addOptions(listOfAvailableSlotsSelectMenu).build();

            Button buttonCancel = Button.danger("cancelstaticadd", "CANCEL")
                .withEmoji(Emoji.fromFormatted("\uD83D\uDED1"));
            Button buttonHelp = Button.primary("helpstaticadd", "HELP")
                .withEmoji(Emoji.fromFormatted("\u2753"));

            event.getHook().sendMessage("`Select from available classes that are still free:`").addActionRow(menu).addActionRow(buttonCancel, buttonHelp).queue();
            workbook.close();
            event.getJDA().addEventListener(new StaticMemberAddEvent(user));
        } catch(IOException e) {
            
        }
    }

    private void STATIC_ADD_TRYOUT_EVENT(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        if(!event.isFromGuild()) {
            event.getHook().sendMessage("This command can not be used in DMs.").queue();
            return;
        }

        User user = event.getOption("user").getAsUser();

        Role staticRole = event.getGuild().getRoleById(Constants.staticRoleID);
        Role staticApplicantRole = event.getGuild().getRoleById(Constants.staticApplicantRoleID);
        Role staticBackupRole = event.getGuild().getRoleById(Constants.staticBackupRoleID);

        if(!(event.getMember().getRoles().contains(staticRole) 
        && !event.getGuild().retrieveMember(user).complete().getRoles().contains(staticApplicantRole) 
        && !event.getGuild().retrieveMember(user).complete().getRoles().contains(staticRole)   
        && !event.getGuild().retrieveMember(user).complete().getRoles().contains(staticBackupRole)
        )) {
            event.getHook().sendMessage("You cannot do that command as of this time. Probably because that user already is either part of the static or applying?").queue();
            return;
        }

        if(user.equals(event.getJDA().getSelfUser())) {
            event.getHook().sendMessage("Unfortunately, you cannot add a bot as a static member.").queue();
            return;
        }

        try (FileInputStream file = new FileInputStream(new File("static.xlsx"))) {
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheet("StaticMembers");

            Row firstRow = sheet.getRow(0);
            Row secondRow = sheet.getRow(1);

            ArrayList<String> emptyStaticSlots = new ArrayList<String>();

            for(int i = 1; i < 11; i++) {
                if(firstRow.getCell(i).getStringCellValue().equals("EMPTY")) {
                    emptyStaticSlots.add(secondRow.getCell(i).getStringCellValue());
                }
            }

            ArrayList<String> listOfAvailableSlots = new ArrayList<>();

            for(String option : emptyStaticSlots) {
                if(!listOfAvailableSlots.contains(option)) {
                    listOfAvailableSlots.add(option);
                }
            }

            ArrayList<SelectOption> listOfAvailableSlotsSelectMenu = new ArrayList<>();

            for(String string : listOfAvailableSlots) {
                listOfAvailableSlotsSelectMenu.add(SelectOption.of(string, string).withEmoji(Emoji.fromFormatted("\u2694")));
            }

            StringSelectMenu menu = StringSelectMenu.create("staticaddtryout").setPlaceholder("Select from available classes that are still free:").addOptions(listOfAvailableSlotsSelectMenu).build();

            Button buttonCancel = Button.danger("cancelstaticaddtryout", "CANCEL")
                .withEmoji(Emoji.fromFormatted("\uD83D\uDED1"));
            Button buttonHelp = Button.primary("helpstaticaddtryout", "HELP")
                .withEmoji(Emoji.fromFormatted("\u2753"));

            event.getHook().sendMessage("`Select from available classes that are still free:`").addActionRow(menu).addActionRow(buttonCancel, buttonHelp).queue();
        
            event.getJDA().addEventListener(new StaticAddTryout(user));

            workbook.close();
        } catch(IOException e) {
            
        }
    }
}
