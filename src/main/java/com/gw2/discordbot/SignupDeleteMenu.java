package com.gw2.discordbot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SignupDeleteMenu extends ListenerAdapter {
    
    @Override
    public void onSelectMenuInteraction(@NotNull SelectMenuInteractionEvent event) {

        if(event.getSelectMenu().getId().equals("signupdeletemenu")) {

            try(FileInputStream file = new FileInputStream(new File("static.xlsx"))) {
                XSSFWorkbook workbook = new XSSFWorkbook(file);
                XSSFSheet sheet = workbook.getSheetAt(0);

                Row firstRow = sheet.getRow(0);

                String key = event.getSelectedOptions().get(0).getValue();

                for(int i = 1; i < 11; i++) {
                    if(firstRow.getCell(i).getStringCellValue().equals(key)) {
                        firstRow.getCell(i).setCellValue("");
                    }
                }

                FileOutputStream fileOutputStream = new FileOutputStream(new File("static.xlsx"));
                workbook.write(fileOutputStream);

                fileOutputStream.close();
                event.deferEdit().queue(edit -> edit.editMessageById(event.getMessageId(), "Successfully deleted user with ID `" + key + "`!").setActionRows().queue());

                workbook.close();
            } catch(IOException e) {
                
            }
        }
    }
}