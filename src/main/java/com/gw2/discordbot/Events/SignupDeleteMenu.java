package com.gw2.discordbot.Events;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SignupDeleteMenu extends ListenerAdapter {
    
    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {

        if(event.getSelectMenu().getId().equals("raidsignupdeletemenu")) {
            try(FileInputStream file = new FileInputStream(new File("static.xlsx"))) {
                XSSFWorkbook workbook = new XSSFWorkbook(file);
                XSSFSheet sheet = workbook.getSheetAt(0);

                Row firstRow = sheet.getRow(0);

                String key = event.getSelectedOptions().get(0).getValue();

                for(int i = 1; i < 11; i++) {
                    if(firstRow.getCell(i).getStringCellValue().equals(key)) {
                        firstRow.getCell(i).setCellValue("EMPTY");
                    }
                }

                FileOutputStream fileOutputStream = new FileOutputStream(new File("static.xlsx"));
                workbook.write(fileOutputStream);

                fileOutputStream.close();
                event.deferEdit().queue(edit -> edit.editMessageById(event.getMessageId(), "Successfully deleted user with ID `" + key + "`!").setComponents().queue());

                if(!event.getUser().equals(event.getJDA().getSelfUser())) {
                    event.getJDA().retrieveUserById(key).complete().openPrivateChannel().queue(channel -> 
                        channel.sendMessage("`Your RAID static signup has been deleted by an administrator " + event.getUser().getAsTag() + "!`").queue()
                    );
                }

                workbook.close();
            } catch(IOException e) {
                
            }
        } else if(event.getSelectMenu().getId().equals("strikesignupdeletemenu")) {
            try(FileInputStream file = new FileInputStream(new File("static.xlsx"))) {
                XSSFWorkbook workbook = new XSSFWorkbook(file);
                XSSFSheet sheet = workbook.getSheetAt(0);

                Row firstRow = sheet.getRow(3);

                String key = event.getSelectedOptions().get(0).getValue();

                for(int i = 1; i < 11; i++) {
                    if(firstRow.getCell(i).getStringCellValue().equals(key)) {
                        firstRow.getCell(i).setCellValue("EMPTY");
                    }
                }

                FileOutputStream fileOutputStream = new FileOutputStream(new File("static.xlsx"));
                workbook.write(fileOutputStream);

                fileOutputStream.close();
                event.deferEdit().queue(edit -> edit.editMessageById(event.getMessageId(), "Successfully deleted user with ID `" + key + "`!").setComponents().queue());

                if(!event.getUser().equals(event.getJDA().getSelfUser())) {
                    event.getJDA().retrieveUserById(key).complete().openPrivateChannel().queue(channel -> 
                        channel.sendMessage("`Your STRIKES static signup has been deleted by an administrator " + event.getUser().getAsTag() + "!`").queue()
                    );
                }

                workbook.close();
            } catch(IOException e) {
                
            }
        }
    }
}
