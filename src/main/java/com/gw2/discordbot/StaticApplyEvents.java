package com.gw2.discordbot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;

public class StaticApplyEvents extends ListenerAdapter {
    
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

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

            case "staticremoveplayer":
                STATIC_REMOVE_PLAYER_EVENT(event);
            break;  

            case "staticplayersget":
                STATIC_GET_ALL_PLAYERS_EVENT(event);
            break;
        }
    }

    private void STATIC_ADD_BACKUP(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        User user = event.getOption("user").getAsUser();

        event.getGuild().addRoleToMember(event.getGuild().retrieveMember(user).complete(), event.getGuild().getRoleById(Constants.staticBackupRoleID)).queue(
            message ->  {
                event.getHook().sendMessage("`You gave that person a static backup role!`").queue();

                EmbedBuilder eb = new EmbedBuilder();
                eb.setFooter("Application made possible by " + event.getJDA().getSelfUser().getName() + "!");
                eb.setTitle(user.getAsTag());
                eb.setDescription("```" + user.getAsTag() + " got the backup role!```");
    
                event.getGuild().getTextChannelById(Constants.staticApplicationsChannelID).sendMessageEmbeds(eb.build()).queue();

                user.openPrivateChannel().queue(channel -> channel.sendMessage("`You have been given a static backup role!`").queue());
            }
        );
    }

    private void STATIC_REJECT_TRYOUT(@NotNull SlashCommandInteractionEvent event) {

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

    private void STATIC_GET_ALL_PLAYERS_EVENT(@NotNull SlashCommandInteractionEvent event) {

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

        HashMap<String, String> playerRolePairs = new HashMap<>();

        playerRolePairs = SignupExcelWriting.getActiveStaticMembers();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("ALL STATIC MEMBERS");

        String string = "```";

        for(Map.Entry<String, String> entry : playerRolePairs.entrySet()) {
            string += String.format("%-20s | %s\n", event.getGuild().retrieveMemberById(entry.getKey()).complete().getUser().getName(), entry.getValue());
        }

        string += "```";

        eb.setDescription(string);
        
        event.getHook().sendMessageEmbeds(eb.build()).queue();

    }

    private void STATIC_REMOVE_PLAYER_EVENT(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        ArrayList<String> listOfMembersWithRolesIds = SignupExcelWriting.getAllActiveMembersIds();
        ArrayList<SelectOption> listOfSelectOptions = new ArrayList<>();

        for(String memberId : listOfMembersWithRolesIds) {
            listOfSelectOptions.add(SelectOption.of(event.getGuild().retrieveMemberById(memberId).complete().getUser().getAsTag(), event.getGuild().retrieveMemberById(memberId).complete().getUser().getId()).withEmoji(Emoji.fromFormatted("\uD83D\uDD28")));
        }

        SelectMenu menu = SelectMenu.create("staticremoveplayermenu").setPlaceholder("Select who you wish to remove from the static.").addOptions(listOfSelectOptions).build();

        Button buttonCancel = Button.danger("cancelstaticremove", "CANCEL")
            .withEmoji(Emoji.fromFormatted("\uD83D\uDED1"));
        Button buttonHelp = Button.primary("helpstaticremove", "HELP")
            .withEmoji(Emoji.fromFormatted("\u2753"));

        event.getHook().sendMessage("`Select a player you wish to remove from the static:`").addActionRows(ActionRow.of(menu), ActionRow.of(buttonCancel, buttonHelp)).queue();
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        switch(event.getButton().getId()) {
            case "cancelstaticaddtryout":
                event.deferEdit().setContent("`You cancelled the action.`").setActionRows().queue();
            break;

            case "cancelstaticadd":
                event.deferEdit().setContent("`You cancelled the action.`").setActionRows().queue();
            break;

            case "cancelstaticremove":
                event.deferEdit().setContent("`You cancelled the action.`").setActionRows().queue();
            break;

            case "helpstaticadd":
                event.deferEdit().setContent("```This is a command that lets you add players to the static. \nYou will be prompted with a menu to add the player.\nIf you wish to continue, run this command again.```").setActionRows().queue();
            break;

            case "helpstaticaddtryout":
                event.deferEdit().setContent("```This is a command that lets you add a tryout for the static. \nYou will be prompted with a menu to select which position you wish for the tryout to be.\nIf you wish to continue, run this command again.```").setActionRows().queue();
            break;

            case "helpstaticremove":
                event.deferEdit().setContent("```This is a command that lets you remove a static member.\nYou will be prompted with a menu to select which player you wish to remove.```").setActionRows().queue();
            break;
        }

    }

    private void STATIC_ADD_PLAYER_EVENT(@NotNull SlashCommandInteractionEvent event) {

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

            HashSet<String> emptyStaticSlots = new HashSet<>();

            for(int i = 1; i < 11; i++) {
                if(firstRow.getCell(i).getStringCellValue().equals("EMPTY")) {
                    emptyStaticSlots.add(secondRow.getCell(i).getStringCellValue());
                }
            }

            ArrayList<SelectOption> listOfAvailableSlots = new ArrayList<>();

            emptyStaticSlots.forEach(string -> {
                listOfAvailableSlots.add(SelectOption.of(string, string).withEmoji(Emoji.fromFormatted("\u2694")));
            });

            SelectMenu menu = SelectMenu.create("staticaddplayermenu").setPlaceholder("Select from available classes you wish to add the player to.").addOptions(listOfAvailableSlots).build();

            Button buttonCancel = Button.danger("cancelstaticadd", "CANCEL")
                .withEmoji(Emoji.fromFormatted("\uD83D\uDED1"));
            Button buttonHelp = Button.primary("helpstaticadd", "HELP")
                .withEmoji(Emoji.fromFormatted("\u2753"));

            event.getHook().sendMessage("`Select from available classes that are still free:`").addActionRows(ActionRow.of(menu), ActionRow.of(buttonCancel, buttonHelp)).queue();
        
            workbook.close();

            event.getJDA().addEventListener(new StaticMemberAddEvent(user));
        } catch(IOException e) {
            
        }

    }

    private void STATIC_ADD_TRYOUT_EVENT(@NotNull SlashCommandInteractionEvent event) {
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

            HashSet<String> emptyStaticSlots = new HashSet<>();

            for(int i = 1; i < 12; i++) {
                if(firstRow.getCell(i).getStringCellValue().equals("EMPTY")) {
                    emptyStaticSlots.add(secondRow.getCell(i).getStringCellValue());
                }
            }

            ArrayList<SelectOption> listOfAvailableSlots = new ArrayList<>();

            emptyStaticSlots.forEach(string -> {
                listOfAvailableSlots.add(SelectOption.of(string, string).withEmoji(Emoji.fromFormatted("\u2694")));
            });

            SelectMenu menu = SelectMenu.create("staticaddtryout").setPlaceholder("Select from available classes that are still free:").addOptions(listOfAvailableSlots).build();

            Button buttonCancel = Button.danger("cancelstaticaddtryout", "CANCEL")
                .withEmoji(Emoji.fromFormatted("\uD83D\uDED1"));
            Button buttonHelp = Button.primary("helpstaticaddtryout", "HELP")
                .withEmoji(Emoji.fromFormatted("\u2753"));

            event.getHook().sendMessage("`Select from available classes that are still free:`").addActionRows(ActionRow.of(menu), ActionRow.of(buttonCancel, buttonHelp)).queue();
        
            event.getJDA().addEventListener(new StaticAddTryout(user));

            workbook.close();
        } catch(IOException e) {
            
        }
    }
}
