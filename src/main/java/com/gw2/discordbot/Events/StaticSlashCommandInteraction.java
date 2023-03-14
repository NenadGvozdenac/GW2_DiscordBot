package com.gw2.discordbot.Events;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.gw2.discordbot.DiscordBot.Constants;
import com.gw2.discordbot.DiscordBot.Token;
import com.gw2.discordbot.HttpParsing.HttpServerHosting;
import com.gw2.discordbot.Miscellaneous.RandomFunnyQuote;
import com.gw2.discordbot.Miscellaneous.SignupExcelWriting;
import com.gw2.discordbot.Miscellaneous.SignupExcelWriting.Type;

import kotlin.Pair;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.FileUpload;

public class StaticSlashCommandInteraction extends ListenerAdapter {
    
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if(!event.isFromGuild()) {
            if(!event.isAcknowledged()) {
                event.deferReply(true).queue(message -> message.sendMessage("Command isn't usable in DMs...").queue());
                return;
            } else return;
        }

        switch(event.getName()) {
            case "start_static_raid":
                START_STATIC_EVENT(event, Type.RAID);
            break;

            case "start_static_strikes":
                START_STATIC_EVENT(event, Type.STRIKES);
            break;

            case "stop_static_raid":
                STOP_STATIC_RAID_EVENT(event);
            break;

            case "raid_signup":
                SIGNUP_RAID_EVENT(event, Type.RAID);
            break;

            case "strikes_signup":
                SIGNUP_RAID_EVENT(event, Type.STRIKES);
            break;

            case "raid_unsignup":
                UNSIGNUP_RAID_EVENT(event, Type.RAID);
            break;

            case "strikes_unsignup":
                UNSIGNUP_RAID_EVENT(event, Type.STRIKES);
            break;

            case "raid_signup_form":
                SIGNUPFORM_EVENT(event, Type.RAID);
            break;

            case "strikes_signup_form":
                SIGNUPFORM_EVENT(event, Type.STRIKES);
            break;

            case "raid_signup_check":
                SIGNUPCHECK_RAID_EVENT(event, Type.RAID);
            break;

            case "strikes_signup_check":
                SIGNUPCHECK_RAID_EVENT(event, Type.STRIKES);
            break;

            case "strikes_signup_clear":
                SIGNUPCLEAR_EVENT(event, Type.STRIKES);
            break;

            case "raid_signup_player":
                SIGNUPPLAYER_RAID_EVENT(event, Type.RAID);
            break;

            case "strikes_signup_player":
                SIGNUPPLAYER_RAID_EVENT(event, Type.STRIKES);
            break;

            case "raid_signup_delete":
                SIGNUPDELETE_EVENT(event, Type.RAID);
            break;

            case "strikes_signup_delete":
                SIGNUPDELETE_EVENT(event, Type.STRIKES);
            break;

            case "signup_sheet":
                SIGNUPSHEET_EVENT(event);
            break;

            case "raid_signup_check_my_loadout":
                SIGNUP_RAID_CHECKMYLOADOUT(event, Type.RAID);
            break;

            case "strikes_signup_check_my_loadout":
                SIGNUP_RAID_CHECKMYLOADOUT(event, Type.STRIKES);
            break;

            case "upload_signupsheet":
                UPLOAD_SIGNUP_SHEET(event);
            break;
        }
    }

    private void UPLOAD_SIGNUP_SHEET(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        if(!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            event.getHook().sendMessage("You don't have permission to change this.").queue();
            return;
        }

        File file = new File("static.xlsx");

        try {
            File fileNew = event.getOption("file").getAsAttachment().getProxy().downloadToFile(file).get();

            if(fileNew.exists()) {
                event.getHook().sendMessage("Everything went good. The file has been overwritten.").queue();
            }

        } catch (InterruptedException | ExecutionException e) {
            event.getHook().sendMessage("Something went wrong with this command. Contact an administrator!").queue();
            return;
        }
    }

    private void SIGNUP_RAID_CHECKMYLOADOUT(SlashCommandInteractionEvent event, Type type) {
        event.deferReply(true).queue();

        if(!event.isFromGuild()) {
            event.getHook().sendMessage("This command can not be used in DMs.").queue();
            return;
        }

        Role staticRole;
        Role staticApplicantRole;
        Role staticBackupRole;

        if(type == Type.RAID) {
            staticRole = event.getGuild().getRoleById(Constants.staticRoleID);
            staticApplicantRole = event.getGuild().getRoleById(Constants.staticApplicantRoleID);
            staticBackupRole = event.getGuild().getRoleById(Constants.staticBackupRoleID);
        
            if(!(event.getMember().getRoles().contains(staticRole) || event.getMember().getRoles().contains(staticApplicantRole) || event.getMember().getRoles().contains(staticBackupRole))) {
                event.getHook().sendMessage("You cannot do this command as of this time.").queue();
                return;
            }
        } else {
            staticRole = event.getGuild().getRoleById(Constants.strikeStaticRoleID);
            staticBackupRole = event.getGuild().getRoleById(Constants.strikeStaticBackupID);

            if(!(event.getMember().getRoles().contains(staticRole) || event.getMember().getRoles().contains(staticBackupRole))) {
                event.getHook().sendMessage("You cannot do this command as of this time.").queue();
                return;
            }
        }

        if(!SignupExcelWriting.checkIfUserAlreadyPresent(event.getUser(), type)) {
            event.getHook().sendMessage("You are not present in the signups!").queue();
            return;
        }

        try (FileInputStream file = new FileInputStream(new File("static.xlsx"))) {
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet signupSheet = workbook.getSheetAt(0);
            XSSFSheet compositionSheet = type == Type.RAID ? workbook.getSheetAt(1) : workbook.getSheetAt(5);

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
                if(currentOpenRow == null) break;
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
            eb.setFooter(RandomFunnyQuote.getFunnyQuote(), Constants.gw2LogoNoBackground);

            event.getHook().sendMessageEmbeds(eb.build()).queue();

            workbook.close();
        } catch(IOException e) {
            event.getHook().sendMessage("There was an error accessing the database. Contact and administrator.").queue();
        }
    }

    private void SIGNUPSHEET_EVENT(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        if(!event.isFromGuild()) {
            event.getHook().sendMessage("This command can not be used in DMs.").queue();
            return;
        }

        File file = new File("static.xlsx");
        event.getHook().sendFiles(FileUpload.fromData(file)).queue();
    }

    private void SIGNUPDELETE_EVENT(SlashCommandInteractionEvent event, Type type) {

        event.deferReply(true).queue();

        if(!event.isFromGuild()) {
            event.getHook().sendMessage("This command can not be used in DMs.").queue();
            return;
        }

        Role ownerRole = event.getGuild().getRoleById("1007918142384779335");

        if(!event.getMember().getRoles().contains(ownerRole)) {
            event.getHook().sendMessage("You cannot do that command as of this time.").queue();
            return;
        }

        try (FileInputStream file = new FileInputStream(new File("static.xlsx"))) {
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);

            Row firstOpenRow;

            if(type == Type.RAID)
                firstOpenRow = sheet.getRow(0);
            else {
                firstOpenRow = sheet.getRow(3);
            }

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

            StringSelectMenu menu;

            if(type == Type.RAID) {
                menu = StringSelectMenu.create("raidsignupdeletemenu")
                    .setPlaceholder("Which person do you wish to delete from the list?").addOptions(listOfAvailableSlots).build();
            } else {
                menu = StringSelectMenu.create("strikesignupdeletemenu")
                    .setPlaceholder("Which person do you wish to delete from the list?").addOptions(listOfAvailableSlots).build();
            }

            event.getHook().sendMessage("`Select which person you wish to delete from signups:`").addActionRow(menu).queue();
            workbook.close();
        } catch(IOException e) {

        }
    }

    private void SIGNUPPLAYER_RAID_EVENT(SlashCommandInteractionEvent event, Type type) {

        event.deferReply(true).queue();

        if(!event.isFromGuild()) {
            event.getHook().sendMessage("This command can not be used in DMs.").queue();
            return;
        }

        User user = event.getOption("user").getAsUser();

        Role staticRole;
        Role staticApplicantRole;
        Role staticBackupRole;

        if(type == Type.RAID) {
            staticRole = event.getGuild().getRoleById(Constants.staticRoleID);
            staticApplicantRole = event.getGuild().getRoleById(Constants.staticApplicantRoleID);
            staticBackupRole = event.getGuild().getRoleById(Constants.staticBackupRoleID);
        
            if(!(event.getMember().getRoles().contains(staticRole) || event.getMember().getRoles().contains(staticApplicantRole) || event.getMember().getRoles().contains(staticBackupRole))) {
                event.getHook().sendMessage("You cannot sign for the raid as of this time.").queue();
                return;
            }
        } else {
            staticRole = event.getGuild().getRoleById(Constants.strikeStaticRoleID);
            staticBackupRole = event.getGuild().getRoleById(Constants.strikeStaticBackupID);

            if(!(event.getMember().getRoles().contains(staticRole) || event.getMember().getRoles().contains(staticBackupRole))) {
                event.getHook().sendMessage("You cannot sign for the raid as of this time.").queue();
                return;
            }
        }

        if(SignupExcelWriting.checkIfUserAlreadyPresent(user, type)) {
            event.getHook().sendMessage("That user is already present in the signups!").queue();
            return;
        }

        try (FileInputStream file = new FileInputStream(new File("static.xlsx"))) {
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);

            Row firstOpenRow;
            Row secondRow;

            if(type == Type.RAID) {
                firstOpenRow = sheet.getRow(0);
                secondRow = sheet.getRow(1);
            } else {
                firstOpenRow = sheet.getRow(3);
                secondRow = sheet.getRow(4);
            }

            Set<String> hashSet = new HashSet<>();

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

            Set<SelectOption> listOfAvailableSlots = new HashSet<>();

            hashSet.forEach(string -> {
                listOfAvailableSlots.add(SelectOption.of(string, string).withEmoji(Emoji.fromFormatted("\u2694")));
            });

            StringSelectMenu menu;

            if(type == Type.RAID) {
                menu = StringSelectMenu.create("raidsignupmenu").setPlaceholder("Select from available classes.").addOptions(listOfAvailableSlots).build();
            } else {
                menu = StringSelectMenu.create("strikesignupmenu").setPlaceholder("Select from available classes.").addOptions(listOfAvailableSlots).build();
            }

            Button buttonCancel = Button.danger("cancelsignupmenu", "CANCEL")
                .withEmoji(Emoji.fromFormatted("\uD83D\uDED1"));
            Button buttonHelp = Button.primary("helpsignupmenu", "HELP")
                .withEmoji(Emoji.fromFormatted("\u2753"));

            event.getHook().sendMessage("`Select which class you wish for the player to play, or press CANCEL if you wish to cancel the action:`").addActionRow(menu).addActionRow(buttonCancel, buttonHelp).queue();
            for(Object o : event.getJDA().getRegisteredListeners()) {
                if(o instanceof SignupExcelWriting) {
                    event.getJDA().removeEventListener(o);
                }
            }

            event.getJDA().addEventListener(new SignupExcelWriting(user, type));
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void SIGNUPCLEAR_EVENT(SlashCommandInteractionEvent event, Type type) {

        event.deferReply(true).queue();

        if(!event.isFromGuild()) {
            event.getHook().sendMessage("This command can not be used in DMs.").queue();
            return;
        }

        Role ownerRole = event.getGuild().getRoleById("1007918142384779335");

        if(!event.getMember().getRoles().contains(ownerRole)) {
            event.getHook().sendMessage("You cannot do that command as of this time.").queue();
            return;
        }

        SignupExcelWriting.clearSignups(type);

        event.getHook().sendMessage("Cleared the signups!").queue();
    }

    private void SIGNUPCHECK_RAID_EVENT(SlashCommandInteractionEvent event, Type type) {

        event.deferReply(true).queue();

        if(!event.isFromGuild()) {
            event.getHook().sendMessage("This command can not be used in DMs.").queue();
            return;
        }

        Role staticRole;
        Role staticApplicantRole;
        Role staticBackupRole;

        if(type == Type.RAID) {
            staticRole = event.getGuild().getRoleById(Constants.staticRoleID);
            staticApplicantRole = event.getGuild().getRoleById(Constants.staticApplicantRoleID);
            staticBackupRole = event.getGuild().getRoleById(Constants.staticBackupRoleID);
        
            if(!(event.getMember().getRoles().contains(staticRole) || event.getMember().getRoles().contains(staticApplicantRole) || event.getMember().getRoles().contains(staticBackupRole))) {
                event.getHook().sendMessage("You cannot sign for the raid as of this time.").queue();
                return;
            }
        } else {
            staticRole = event.getGuild().getRoleById(Constants.strikeStaticRoleID);
            staticBackupRole = event.getGuild().getRoleById(Constants.strikeStaticBackupID);

            if(!(event.getMember().getRoles().contains(staticRole) || event.getMember().getRoles().contains(staticBackupRole))) {
                event.getHook().sendMessage("You cannot sign for the raid as of this time.").queue();
                return;
            }
        }
        
        List<Pair<String, String>> listOfPeopleSignedUp = SignupExcelWriting.getCurrentSignups(type);

        if(listOfPeopleSignedUp.isEmpty()) {
            event.getHook().sendMessage("```Nobody signed up yet!```").queue();
        } else {
            String stringToPing = "```" + listOfPeopleSignedUp.size() + "/10 people signed up.\n\n";

            stringToPing += "+--------------------+--------------------------------------+\n";

            for(Pair<String, String> pair : listOfPeopleSignedUp) {
                stringToPing += String.format("|%s|%s|\n", CenterString(20, event.getJDA().retrieveUserById(pair.getFirst()).complete().getName()), CenterString(38, pair.getSecond()));
                stringToPing += "+--------------------+--------------------------------------+\n";
            }

            stringToPing += "```";
    
            event.getHook().sendMessage(stringToPing).queue();
        }
    }

    private void SIGNUPFORM_EVENT(SlashCommandInteractionEvent event, Type type) {

        event.deferReply(false).queue();

        if(!event.isFromGuild()) {
            event.getHook().sendMessage("This command can not be used in DMs.").queue();
            return;
        }

        Role staticRole;
        Role staticApplicantRole;
        Role staticBackupRole;

        if(type == Type.RAID) {
            staticRole = event.getGuild().getRoleById(Constants.staticRoleID);
            staticApplicantRole = event.getGuild().getRoleById(Constants.staticApplicantRoleID);
            staticBackupRole = event.getGuild().getRoleById(Constants.staticBackupRoleID);
        
            if(!(event.getMember().getRoles().contains(staticRole) || event.getMember().getRoles().contains(staticApplicantRole) || event.getMember().getRoles().contains(staticBackupRole))) {
                event.getHook().sendMessage("You cannot get the form as of this time.").queue();
                return;
            }
        } else {
            staticRole = event.getGuild().getRoleById(Constants.strikeStaticRoleID);
            staticBackupRole = event.getGuild().getRoleById(Constants.strikeStaticBackupID);

            if(!(event.getMember().getRoles().contains(staticRole) || event.getMember().getRoles().contains(staticBackupRole))) {
                event.getHook().sendMessage("You cannot get the form as of this time.").queue();
                return;
            }
        }

        event.getHook().sendMessage(Token.getSignupForm(type) == null ? "The image cannot be found. Try using `/staticaddsignupform` to add the image." : Token.getSignupForm(type)).queue();
    }

    private void UNSIGNUP_RAID_EVENT(SlashCommandInteractionEvent event, Type type) {

        event.deferReply(true).queue();

        if(!event.isFromGuild()) {
            event.getHook().sendMessage("This command can not be used in DMs.").queue();
            return;
        }

        Role staticRole;
        Role staticApplicantRole;
        Role staticBackupRole;

        if(type == Type.RAID) {
            staticRole = event.getGuild().getRoleById(Constants.staticRoleID);
            staticApplicantRole = event.getGuild().getRoleById(Constants.staticApplicantRoleID);
            staticBackupRole = event.getGuild().getRoleById(Constants.staticBackupRoleID);
        
            if(!(event.getMember().getRoles().contains(staticRole) || event.getMember().getRoles().contains(staticApplicantRole) || event.getMember().getRoles().contains(staticBackupRole))) {
                event.getHook().sendMessage("You cannot unsign for the raid as of this time.").queue();
                return;
            }
        } else {
            staticRole = event.getGuild().getRoleById(Constants.strikeStaticRoleID);
            staticBackupRole = event.getGuild().getRoleById(Constants.strikeStaticBackupID);

            if(!(event.getMember().getRoles().contains(staticRole) || event.getMember().getRoles().contains(staticBackupRole))) {
                event.getHook().sendMessage("You cannot unsign for the raid as of this time.").queue();
                return;
            }
        }

        if(!SignupExcelWriting.checkIfUserAlreadyPresent(event.getUser(), type)) {
            event.getHook().sendMessage("You aren't signed up for this week in the first place!").queue();
            return;
        }

        try (FileInputStream file = new FileInputStream(new File("static.xlsx"))) {
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);

            Row firstOpenRow;

            if(type == Type.RAID) {
                firstOpenRow = sheet.getRow(0);
            } else {
                firstOpenRow = sheet.getRow(3);
            }
            

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

    private void SIGNUP_RAID_EVENT(SlashCommandInteractionEvent event, Type type) {

        event.deferReply(true).queue();

        if(!event.isFromGuild()) {
            event.getHook().sendMessage("This command can not be used in DMs.").queue();
            return;
        }

        Role staticRole;
        Role staticApplicantRole;
        Role staticBackupRole;

        if(type == Type.RAID) {
            staticRole = event.getGuild().getRoleById(Constants.staticRoleID);
            staticApplicantRole = event.getGuild().getRoleById(Constants.staticApplicantRoleID);
            staticBackupRole = event.getGuild().getRoleById(Constants.staticBackupRoleID);
        
            if(!(event.getMember().getRoles().contains(staticRole) || event.getMember().getRoles().contains(staticApplicantRole) || event.getMember().getRoles().contains(staticBackupRole))) {
                event.getHook().sendMessage("You cannot sign for the raid as of this time.").queue();
                return;
            }
        } else {
            staticRole = event.getGuild().getRoleById(Constants.strikeStaticRoleID);
            staticBackupRole = event.getGuild().getRoleById(Constants.strikeStaticBackupID);

            if(!(event.getMember().getRoles().contains(staticRole) || event.getMember().getRoles().contains(staticBackupRole))) {
                event.getHook().sendMessage("You cannot sign for the raid as of this time.").queue();
                return;
            }
        }
        

        if(SignupExcelWriting.checkIfUserAlreadyPresent(event.getUser(), type)) {
            event.getHook().sendMessage("You already signed up this week! Please use `/unsignup` first.").queue();
            return;
        }

        try (FileInputStream file = new FileInputStream(new File("static.xlsx"))) {
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheet("Signups");

            Row firstOpenRow;
            Row secondRow;

            if(type == Type.RAID) {
                firstOpenRow = sheet.getRow(0);
                secondRow = sheet.getRow(1);
            } else {
                firstOpenRow = sheet.getRow(3);
                secondRow = sheet.getRow(4);
            }


            Set<String> hashSet = new HashSet<>();

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

            Set<SelectOption> listOfAvailableSlots = new HashSet<>();

            hashSet.forEach(string -> {
                listOfAvailableSlots.add(SelectOption.of(string, string).withEmoji(Emoji.fromFormatted("\u2694")));
            });

            StringSelectMenu menu;

            if(type == Type.RAID) {
                menu = StringSelectMenu.create("raidsignupmenu").setPlaceholder("Select from available classes.").addOptions(listOfAvailableSlots).build();
            } else {
                menu = StringSelectMenu.create("strikesignupmenu").setPlaceholder("Select from available classes.").addOptions(listOfAvailableSlots).build();
            }

            Button buttonCancel = Button.danger("cancelsignupmenu", "CANCEL")
                .withEmoji(Emoji.fromFormatted("\uD83D\uDED1"));
            Button buttonHelp = Button.primary("helpsignupmenu", "HELP")
                .withEmoji(Emoji.fromFormatted("\u2753"));

            event.getHook().sendMessage("`Select which class you wish to play: `").addActionRow(menu).addActionRow(buttonCancel, buttonHelp).queue();
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void STOP_STATIC_RAID_EVENT(SlashCommandInteractionEvent event) {

        if(!event.isFromGuild()) {
            event.getHook().sendMessage("This command can not be used in DMs.").queue();
            return;
        }

        event.deferReply(true).queue();
        HttpServerHosting.stopServer();
        event.getHook().sendMessage("Stopped the server port.").queue();
    }

    private void START_STATIC_EVENT(SlashCommandInteractionEvent event, Type type) {
        event.deferReply(true).queue();

        Integer minutesToWait = event.getOption("minutes_to_wait").getAsInt();

        if(minutesToWait > 60 || minutesToWait < 5) {
            event.getHook().sendMessage("That is invalid time. `5 min < time < 60 min`").queue();
            return;
        }

        if(minutesToWait > 15) {
            event.getHook().sendMessage("I will be pinging everyone in " + minutesToWait + " minutes.").queue();
        }

        if(minutesToWait < 15) {
            event.getHook().sendMessage("Considering the time is lower than 15 minutes, I pinged everyone immediatelly.").queue();
        }

        new Timer().schedule(new TimerTask() {
            public void run() {
                if(HttpServerHosting.activateServer()) {
                    EmbedBuilder eb = new EmbedBuilder();
                    List<Pair<String, String>> listOfPeopleSignedUp = SignupExcelWriting.getCurrentSignups(type);
        
                    if(listOfPeopleSignedUp.isEmpty()) {
                        event.getHook().sendMessage("Nobody signed up, but I opened the port to receive raid logs.").queue();
                        return;
                    }
        
                    String string = "```";
        
                    for(Pair<String, String> pair : listOfPeopleSignedUp) {
                        String currentString = String.format("%-20s %s\n", event.getJDA().retrieveUserById(pair.getFirst()).complete().getName().length() > 18 ? event.getJDA().retrieveUserById(pair.getFirst()).complete().getName().substring(0, 18) + "." : event.getJDA().retrieveUserById(pair.getFirst()).complete().getName(), pair.getSecond());
                        string += currentString;
                    }
        
                    string += "```";
        
                    eb.setDescription(string);
                    eb.setColor(type == Type.RAID ? Color.CYAN : Color.WHITE);
                    eb.setAuthor("/sqjoin NenadG.4682", null, event.getMember().getAvatarUrl());
        
                    event.getGuild().getTextChannelById(type == Type.RAID ? Constants.raidStaticAnnouncementChannelID : Constants.strikeStaticInfoID)
                        .sendMessage("<@&" + (type == Type.RAID ? Constants.staticRoleID : Constants.strikeStaticRoleID) + ">, **STATIC** weekly clear is starting in 15 minutes. We have " + listOfPeopleSignedUp.size() + "/10 people signed up!")
                        .setEmbeds(eb.build()).queue(message -> message.delete().queueAfter(3, TimeUnit.HOURS));

                    new Timer().schedule(new TimerTask() {
                        public void run() {
                            event.getGuild().getTextChannelById(type == Type.RAID ? Constants.raidStaticAnnouncementChannelID : Constants.strikeStaticInfoID)
                                .sendMessage("<@&" + (type == Type.RAID ? Constants.staticRoleID : Constants.strikeStaticRoleID) + ">, **STATIC** weekly clear is starting **now**!").queue(message -> message.delete().queueAfter(180 - minutesToWait, TimeUnit.MINUTES));
        
                            event.getUser().openPrivateChannel().queue(channel -> channel.sendMessage("Make sure to enable the autouploading tool for the static run!").queue());

                            this.cancel();
                        }
                    }, minutesToWait > 15 ? 15 * 60 * 1000 : minutesToWait * 60 * 1000);
        
                    new Timer().schedule(new TimerTask() {
                        public void run() {
                            SignupExcelWriting.getCurrentSignups(type).forEach(pair -> {
                                event.getGuild().retrieveMember(UserSnowflake.fromId(pair.getFirst())).queue(member -> {
                                    if(!(member.getRoles().contains(event.getGuild().getRoleById(Constants.staticRoleID)) || member.getRoles().contains(event.getGuild().getRoleById(Constants.staticApplicantRoleID)) || member.getRoles().contains(event.getGuild().getRoleById(Constants.strikeStaticRoleID)))) {
                                        member.getUser().openPrivateChannel().queue(channel -> channel.sendMessage("Your signup has been removed for next week's static. If you are called again, feel free to sign up again!").queue());
                                        
                                        try (FileInputStream file = new FileInputStream(new File("static.xlsx"))) {
                                            XSSFWorkbook workbook = new XSSFWorkbook(file);
                                            XSSFSheet sheet = workbook.getSheetAt(0);

                                            Row firstOpenRow;

                                            if(type == Type.RAID)
                                                firstOpenRow = sheet.getRow(0);
                                            else {
                                                firstOpenRow = sheet.getRow(3);
                                            }

                                            for(Integer i = 1; i < 11; i++) {
                                                if(firstOpenRow.getCell(i).getStringCellValue().equals(pair.getFirst())) {
                                                    firstOpenRow.getCell(i).setCellValue("EMPTY");
                                                }
                                            }
                                
                                            FileOutputStream fileOutputStream = new FileOutputStream(new File("static.xlsx"));
                                            workbook.write(fileOutputStream);
                                
                                            fileOutputStream.close();
                                            workbook.close();
        
                                        } catch(IOException e) {
                                
                                        }
                                    }
                                });
                            });
        
                            HttpServerHosting.stopServer();
        
                            event.getMember().getUser().openPrivateChannel().queue(channel -> channel.sendMessage("The server's port was closed automatically after 4 hours. I unsigned any backups (if there were any).").queue());
        
                            this.cancel();
                        }
                    }, 4 * 60 * 60 * 1000 + (minutesToWait > 15 ? 15 * 60 * 1000 : minutesToWait * 60 * 1000));
        
                } else {
                    event.getHook().sendMessage("Server is already activated there. Use `/stopstaticraid` first.").queue();
                }

                this.cancel();
            }
        }, minutesToWait > 15 ? (minutesToWait - 15) * 60 * 1000 : 0);
    }

    public String CenterString(int width, String s) {
        return String.format("%-" + width  + "s", String.format("%" + (s.length() + (width - s.length()) / 2) + "s", s));
    }
}
