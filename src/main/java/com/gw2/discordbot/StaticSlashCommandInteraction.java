package com.gw2.discordbot;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;

import kotlin.Pair;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;

public class StaticSlashCommandInteraction extends ListenerAdapter {
    
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
    
        switch(event.getName()) {
            case "startstaticraid":
                START_STATIC_RAID_EVENT(event);
            break;

            case "stopstaticraid":
                STOP_STATIC_RAID_EVENT(event);
            break;

            case "signup":
                SIGNUP_EVENT(event);
            break;

            case "unsignup":
                UNSIGNUP_EVENT(event);
            break;

            case "signupform":
                SIGNUPFORM_EVENT(event);
            break;

            case "signupcheck":
                SIGNUPCHECK_EVENT(event);
            break;

            case "signupclear":
                SIGNUPCLEAR_EVENT(event);
            break;

            case "signupplayer":
                SIGNUPPLAYER_EVENT(event);
            break;

            case "signupdelete":
                SIGNUPDELETE_EVENT(event);
            break;

            case "signupsheet":
                SIGNUPSHEET_EVENT(event);
            break;

            case "signupcheckmyloadout":
                SIGNUPCHECKMYLOADOUT(event);
            break;
        }
    }

    private void SIGNUPCHECKMYLOADOUT(@NotNull SlashCommandInteractionEvent event) {

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

        if(!SignupExcelWriting.checkIfUserAlreadyPresent(event.getUser())) {
            event.getHook().sendMessage("You are not present in the signups!").queue();
            return;
        }

        try (FileInputStream file = new FileInputStream(new File("static.xlsx"))) {
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet signupSheet = workbook.getSheetAt(0);
            XSSFSheet compositionSheet = workbook.getSheetAt(1);

            Row firstRowFirstSheet = signupSheet.getRow(0);

            Integer columnNumber = 0;

            for(int i = 1; i < 11; i++) {
                if(firstRowFirstSheet.getCell(i).getStringCellValue().equals(event.getUser().getId())) {
                    columnNumber = i;
                    break;
                }
            }

            if(columnNumber == 0) {
                event.getHook().sendMessage("Unfortunately, you are not signed up for this week!").queue();
                workbook.close();
                return;
            }

            List<Pair<String, String>> pairBossRoleList = new ArrayList<>();

            for(int i = 0; i < 25; i++) {
                Row currentOpenRow = compositionSheet.getRow(i);

                pairBossRoleList.add(new Pair<String, String>(currentOpenRow.getCell(0).getStringCellValue(), currentOpenRow.getCell(columnNumber).getStringCellValue()));
            }

            String stringToDisplay = "```";

            for(Pair<String, String> pairBossRole : pairBossRoleList) {

                String newString = String.format("%-20s %s\n", pairBossRole.getFirst(), pairBossRole.getSecond());

                stringToDisplay += newString;
            }

            stringToDisplay += "```";

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(event.getUser().getAsTag());
            eb.setDescription(stringToDisplay);
            eb.setFooter("Thank you for using " + Main.jda.getSelfUser().getName() + "!", Constants.gw2LogoNoBackground);

            event.getHook().sendMessageEmbeds(eb.build()).queue();

            workbook.close();
        } catch(IOException e) {

        }
    }

    private void SIGNUPSHEET_EVENT(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        if(!event.isFromGuild()) {
            event.getHook().sendMessage("This command can not be used in DMs.").queue();
            return;
        }

        File file = new File("static.xlsx");
        event.getHook().sendFile(file).queue();
    }

    private void SIGNUPDELETE_EVENT(@NotNull SlashCommandInteractionEvent event) {

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

        try (FileInputStream file = new FileInputStream(new File("static.xlsx"))) {
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);

            Row firstOpenRow = sheet.getRow(0);

            List<String> listOfIds = new ArrayList<>();

            for(Integer i = 1; i < 11; i++) {
                if(!firstOpenRow.getCell(i).getStringCellValue().equals("EMPTY")) {
                    listOfIds.add(firstOpenRow.getCell(i).getStringCellValue());
                }
            }

            if(listOfIds.isEmpty()) {
                event.getHook().sendMessage("Unfortunately, the signup sheet is empty.").queue();
                workbook.close();
                return;
            }

            List<SelectOption> listOfAvailableSlots = new ArrayList<>();

            listOfIds.forEach(id -> {
                SelectOption option = SelectOption.of(event.getJDA().retrieveUserById(id).complete().getAsTag(), id);
                listOfAvailableSlots.add(option);
            });

            SelectMenu menu = SelectMenu.create("signupdeletemenu")
                .setPlaceholder("Which person do you wish to delete from the list?").addOptions(listOfAvailableSlots).build();

            event.getHook().sendMessage("`Select which person you wish to delete from signups:`").addActionRow(menu).queue();
            workbook.close();
        } catch(IOException e) {

        }
    }

    private void SIGNUPPLAYER_EVENT(@NotNull SlashCommandInteractionEvent event) {

        event.deferReply(true).queue();

        if(!event.isFromGuild()) {
            event.getHook().sendMessage("This command can not be used in DMs.").queue();
            return;
        }

        User user = event.getOption("user").getAsUser();

        Role staticRole = event.getGuild().getRoleById("1007918310190501948");

        if(!event.getMember().getRoles().contains(staticRole)) {
            event.getHook().sendMessage("You cannot do that command as of this time.").queue();
            return;
        }

        if(!event.getGuild().retrieveMember(user).complete().getRoles().contains(staticRole)) {
            event.getHook().sendMessage("You cannot do that, because the user is not part of this static!").queue();
            return;
        }

        if(SignupExcelWriting.checkIfUserAlreadyPresent(user)) {
            event.getHook().sendMessage("That user is already present in the signups!").queue();
            return;
        }

        try (FileInputStream file = new FileInputStream(new File("static.xlsx"))) {
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);

            Row firstOpenRow = sheet.getRow(0);
            Row secondRow = sheet.getRow(1);

            HashSet<String> hashSet = new HashSet<>();

            for(Integer i = 1; i < 11; i++) {
                if(firstOpenRow.getCell(i).getStringCellValue().equals("EMPTY")) {
                    hashSet.add(secondRow.getCell(i).getStringCellValue());
                }
            }

            if(hashSet.isEmpty()) {
                event.getHook().sendMessage("Unfortunately, all spots have been taken for this week's raid. If this is a mistake, contact an administrator.").queue();
                workbook.close();
                return;
            }

            List<SelectOption> listOfAvailableSlots = new ArrayList<>();

            hashSet.forEach(string -> {
                listOfAvailableSlots.add(SelectOption.of(string, string).withEmoji(Emoji.fromFormatted("\u2694")));
            });

            SelectMenu menu = SelectMenu.create("signupmenu").setPlaceholder("Select from available classes.").addOptions(listOfAvailableSlots).build();

            Button buttonCancel = Button.danger("cancelsignupmenu", "CANCEL")
                .withEmoji(Emoji.fromFormatted("\uD83D\uDED1"));
            Button buttonHelp = Button.primary("helpsignupmenu", "HELP")
                .withEmoji(Emoji.fromFormatted("\u2753"));

            event.getHook().sendMessage("`Select which class you wish for the player to play, or press CANCEL if you wish to cancel the action:`").addActionRows(ActionRow.of(menu), ActionRow.of(buttonCancel, buttonHelp)).queue();
            for(Object o : event.getJDA().getRegisteredListeners()) {
                if(o instanceof SignupExcelWriting) {
                    event.getJDA().removeEventListener(o);
                }
            }

            event.getJDA().addEventListener(new SignupExcelWriting(user));
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void SIGNUPCLEAR_EVENT(@NotNull SlashCommandInteractionEvent event) {

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

        SignupExcelWriting.clearSignups();

        event.getHook().sendMessage("Cleared the signups!").queue();
    }

    private void SIGNUPCHECK_EVENT(@NotNull SlashCommandInteractionEvent event) {

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

        List<Pair<String, String>> listOfPeopleSignedUp = SignupExcelWriting.getCurrentSignups();

        if(listOfPeopleSignedUp.isEmpty()) {
            event.getHook().sendMessage("Nobody signed up yet!").queue();
        } else {
            String stringToPing = "";

            for(Pair<String, String> pair : listOfPeopleSignedUp) {
                stringToPing += UserSnowflake.fromId(pair.getFirst()).getAsMention() + " - " + pair.getSecond() + "\n";
            }
    
            event.getHook().sendMessage("People signed up: \n" + stringToPing).queue();
        }
    }

    private void SIGNUPFORM_EVENT(@NotNull SlashCommandInteractionEvent event) {

        event.deferReply(false).queue();

        if(!event.isFromGuild()) {
            event.getHook().sendMessage("This command can not be used in DMs.").queue();
            return;
        }

        Role staticRole = event.getGuild().getRoleById("1007918310190501948");

        if(!event.getMember().getRoles().contains(staticRole)) {
            event.getHook().sendMessage("You cannot signup for the raid as of this time.").queue();
            return;
        }

        event.getHook().sendMessage(Constants.signUpFormMessage).queue();
    }

    private void UNSIGNUP_EVENT(@NotNull SlashCommandInteractionEvent event) {

        event.deferReply(true).queue();

        if(!event.isFromGuild()) {
            event.getHook().sendMessage("This command can not be used in DMs.").queue();
            return;
        }

        Role staticRole = event.getGuild().getRoleById("1007918310190501948");

        if(!event.getMember().getRoles().contains(staticRole)) {
            event.getHook().sendMessage("You cannot unsignup for the raid as of this time.").queue();
            return;
        }

        if(!SignupExcelWriting.checkIfUserAlreadyPresent(event.getUser())) {
            event.getHook().sendMessage("You aren't signed up for this week in the first place!").queue();
            return;
        }

        try (FileInputStream file = new FileInputStream(new File("static.xlsx"))) {
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);

            Row firstOpenRow = sheet.getRow(0);

            for(Integer i = 1; i < 11; i++) {
                if(firstOpenRow.getCell(i).getStringCellValue().equals(event.getUser().getId())) {
                    firstOpenRow.getCell(i).setCellValue("EMPTY");
                }
            }

            FileOutputStream fileOutputStream = new FileOutputStream(new File("static.xlsx"));
            workbook.write(fileOutputStream);

            fileOutputStream.close();
            workbook.close();

            event.getHook().sendMessage("You have been unsigned for this week!").queue();
        } catch(IOException e) {

        }
    }

    private void SIGNUP_EVENT(@NotNull SlashCommandInteractionEvent event) {

        event.deferReply(true).queue();

        if(!event.isFromGuild()) {
            event.getHook().sendMessage("This command can not be used in DMs.").queue();
            return;
        }

        Role staticRole = event.getGuild().getRoleById("1007918310190501948");

        if(!event.getMember().getRoles().contains(staticRole)) {
            event.getHook().sendMessage("You cannot unsign for the raid as of this time.").queue();
            return;
        }

        if(SignupExcelWriting.checkIfUserAlreadyPresent(event.getUser())) {
            event.getHook().sendMessage("You already signed up this week! Please use `/unsignup` first.").queue();
            return;
        }

        try (FileInputStream file = new FileInputStream(new File("static.xlsx"))) {
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheet("Signups");

            Row firstOpenRow = sheet.getRow(0);
            Row secondRow = sheet.getRow(1);

            HashSet<String> hashSet = new HashSet<>();

            for(Integer i = 1; i < 11; i++) {
                if(firstOpenRow.getCell(i).getStringCellValue().equals("EMPTY")) {
                    hashSet.add(secondRow.getCell(i).getStringCellValue());
                }
            }

            if(hashSet.isEmpty()) {
                event.getHook().sendMessage("Unfortunately, all spots have been taken for this week's raid. If this is a mistake, contact an administrator.").queue();
                workbook.close();
                return;
            }

            List<SelectOption> listOfAvailableSlots = new ArrayList<>();

            hashSet.forEach(string -> {
                listOfAvailableSlots.add(SelectOption.of(string, string).withEmoji(Emoji.fromFormatted("\u2694")));
            });

            SelectMenu menu = SelectMenu.create("signupmenu").setPlaceholder("Select from available classes.").addOptions(listOfAvailableSlots).build();

            Button buttonCancel = Button.danger("cancelsignupmenu", "CANCEL")
                .withEmoji(Emoji.fromFormatted("\uD83D\uDED1"));
            Button buttonHelp = Button.primary("helpsignupmenu", "HELP")
                .withEmoji(Emoji.fromFormatted("\u2753"));

            event.getHook().sendMessage("`Select which class you wish to play: `").addActionRows(ActionRow.of(menu), ActionRow.of(buttonCancel, buttonHelp)).queue();
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void STOP_STATIC_RAID_EVENT(@NotNull SlashCommandInteractionEvent event) {

        if(!event.isFromGuild()) {
            event.getHook().sendMessage("This command can not be used in DMs.").queue();
            return;
        }

        event.deferReply(true).queue();
        HttpServerHosting.stopServer();
        event.getHook().sendMessage("Stopped the server port.").queue();
    }

    private void START_STATIC_RAID_EVENT(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        Integer minutesToWait = event.getOption("minutes_to_wait").getAsInt();

        if(minutesToWait > 15 || minutesToWait < 5) {
            event.getHook().sendMessage("That is invalid time. `5 min < time < 15 min`").queue();
            return;
        }

        if(HttpServerHosting.activateServer()) {
            EmbedBuilder eb = new EmbedBuilder();
            List<Pair<String, String>> listOfPeopleSignedUp = SignupExcelWriting.getCurrentSignups();

            if(listOfPeopleSignedUp.isEmpty()) {
                event.getHook().sendMessage("Nobody signed up, but I opened the port to receive raid logs.").queue();
                return;
            }

            String string = "```";

            for(Pair<String, String> pair : listOfPeopleSignedUp) {
                String currentString = String.format("%-20s %s\n", event.getJDA().retrieveUserById(pair.getFirst()).complete().getName(), pair.getSecond());
                string += currentString;
            }

            string += "```";

            eb.setDescription(string);
            eb.setColor(Color.CYAN);
            eb.setFooter("/sqjoin NenadG   OR   /sqjoin NenadG.4682", Constants.gw2LogoNoBackground);
            eb.setFooter("Thank you for using " + Main.jda.getSelfUser().getName() + "!", Constants.gw2LogoNoBackground);

            event.getGuild().getTextChannelById(Constants.staticAnnouncementChannelID)
                .sendMessage("<@&1007918310190501948>, Static weekly clear is starting in " + minutesToWait + " minutes. Please get ready in time.")
                .setEmbeds(eb.build()).queue(message -> message.delete().queueAfter(3, TimeUnit.HOURS));
                
            event.getHook().sendMessage("Activated the server port for receiving dps reports, pinged raid static in <#" + Constants.staticAnnouncementChannelID + ">, and opened the sheet for editing.\nIn 4 hours, if the port is not closed manually, it will be closed.").queue();
        
            new Timer().schedule(new TimerTask() {
                public void run() {
                    event.getGuild().getTextChannelById(Constants.staticAnnouncementChannelID)
                        .sendMessage("<@&1007918310190501948>, Static weekly clear is starting **now**!").queue(message -> message.delete().queueAfter(180 - minutesToWait, TimeUnit.MINUTES));

                    this.cancel();
                }
            }, minutesToWait * 60 * 1000, 1);

            new Timer().schedule(new TimerTask() {
                public void run() {
                    HttpServerHosting.stopServer();
                    this.cancel();
                }
            }, 4 * 60 * 60 * 1000, 1);

        } else {
            event.getHook().sendMessage("Server is already activated there, but I pinged raid static in <#" + Constants.staticAnnouncementChannelID + ">, and opened the sheet for editing.").queue();
        
            EmbedBuilder eb = new EmbedBuilder();
            
            List<Pair<String, String>> listOfPeopleSignedUp = SignupExcelWriting.getCurrentSignups();

            for(Pair<String, String> pair : listOfPeopleSignedUp) {
                eb.addField(event.getJDA().retrieveUserById(pair.getFirst()).complete().getAsTag(), pair.getSecond(), true);
            }

            eb.setColor(Color.CYAN);

            event.getGuild().getTextChannelById(Constants.staticAnnouncementChannelID)
                .sendMessage("<@&1007918310190501948>, Static weekly clear is starting in " + minutesToWait + " minutes. Please get ready in time.")
                .setEmbeds(eb.build()).queue();

            new Timer().schedule(new TimerTask() {
                public void run() {
                    event.getGuild().getTextChannelById(Constants.staticAnnouncementChannelID)
                        .sendMessage("<@&1007918310190501948>, Static weekly clear is starting **now**!").queue();

                    this.cancel();
                }
            }, minutesToWait * 60 * 1000, 1);
        }
    }
}
