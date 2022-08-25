package com.gw2.discordbot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;

import kotlin.Pair;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SignupExcelWriting extends ListenerAdapter {
    
    public User user;

    public SignupExcelWriting(User user) {
        this.user = user;
    }

    public SignupExcelWriting() {
        this.user = null;
    }

    @Override
    public void onSelectMenuInteraction(@NotNull SelectMenuInteractionEvent event) {
        if(event.getSelectMenu().getId().equals("signupmenu")) {
            String selectedItem = event.getSelectedOptions().get(0).getValue();

            try(FileInputStream file = new FileInputStream(new File("static.xlsx"))) {
                XSSFWorkbook workbook = new XSSFWorkbook(file);
                XSSFSheet sheet = workbook.getSheetAt(0);

                Row secondRow = sheet.getRow(1);
                Row firstRow = sheet.getRow(0);

                for(int i = 1; i < 11; i++) {
                    if(secondRow.getCell(i).getStringCellValue().equals(selectedItem)) {
                        firstRow.getCell(i).setCellValue(this.user == null ? event.getUser().getId() : user.getId());
                        break;
                    }
                }

                FileOutputStream fileOutputStream = new FileOutputStream(new File("static.xlsx"));
                workbook.write(fileOutputStream);

                fileOutputStream.close();
                event.deferEdit().queue(edit -> edit.editMessageById(event.getMessageId(), "`You chose " + selectedItem + "!`").setActionRows().queue());

                event.getJDA().removeEventListener(this);
                workbook.close();

                event.getJDA().addEventListener(new SignupExcelWriting());
            } catch(IOException e) {
                Logging.LOG(SignupExcelWriting.class, "Couldn't write what the user chose!!");
            }
        }
    }

    public static boolean checkIfUserAlreadyPresent(User user) {
        try (FileInputStream file = new FileInputStream(new File("static.xlsx"))) {
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);

            Row firstOpenRow = sheet.getRow(0);

            for(int i = 1; i < 11; i++) {
                if(firstOpenRow.getCell(i).getStringCellValue().equals(user.getId())) {
                    workbook.close();
                    return true;
                }
            }

            workbook.close();

            return false;
        } catch(IOException e) {

            Logging.LOG(SignupExcelWriting.class, "Couldn't check whether the user is already present!!");
            return false;
        }
    }

    public static List<Pair<String, String>> getCurrentSignups() {
        try (FileInputStream file = new FileInputStream(new File("static.xlsx"))) {
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);

            Row firstOpenRow = sheet.getRow(0), secondOpenRow = sheet.getRow(1);

            ArrayList<Pair<String, String>> arrayListOfPairs = new  ArrayList<Pair<String, String>>();

            for(int i = 1; i < 11; i++) {
                if(!firstOpenRow.getCell(i).getStringCellValue().equals("")) {
                    arrayListOfPairs.add(new Pair<String, String>(firstOpenRow.getCell(i).getStringCellValue(), secondOpenRow.getCell(i).getStringCellValue()));
                }
            }

            workbook.close();

            return arrayListOfPairs;
        } catch(IOException e) {
            Logging.LOG(SignupExcelWriting.class, "Couldn't get current signups!!");
            return Collections.emptyList();
        }

    }

    public static void clearSignups() {

        try(FileInputStream file = new FileInputStream(new File("static.xlsx"))) {
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);

            Row firstRow = sheet.getRow(0);

            for(int i = 1; i < 11; i++) {
                firstRow.getCell(i).setCellValue("");
            }

            FileOutputStream fileOutputStream = new FileOutputStream(new File("static.xlsx"));
            workbook.write(fileOutputStream);

            fileOutputStream.close();
            workbook.close();
        } catch(IOException e) {
            Logging.LOG(SignupExcelWriting.class, "Couldn't clear signups!!");
        }

    } 
}
