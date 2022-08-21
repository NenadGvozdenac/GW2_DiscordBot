package com.gw2discordapp;

import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;

public class HelpButtonEventListener extends ListenerAdapter {

    static List<EmbedBuilder> embedBuilders;
    EmbedBuilder selectedEmbed;

    public HelpButtonEventListener(List<EmbedBuilder> embedBuilders) {
        HelpButtonEventListener.embedBuilders = embedBuilders;
    }

    public HelpButtonEventListener() {
        List<EmbedBuilder> embedBuilders = new ArrayList<EmbedBuilder>();

        try (FileReader reader = new FileReader(new File(new File("jsonFolder"), "commands.json"))) {
        
            Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .setFieldNamingStrategy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .serializeNulls()
                .create();

            Type founderTypeSet = new TypeToken<List<SlashCommandData>>(){}.getType();
            List<SlashCommandData> listCommandData = gson.fromJson(reader, founderTypeSet);

            for(int i = 0; i < Math.ceil(listCommandData.size() / 10f); i++) {
                embedBuilders.add(new EmbedBuilder());
            }

            int number2 = 0, number3 = 0;

            for(int i = 0; i < listCommandData.size(); i++) {
                embedBuilders.get(number3).addField(
                    "/" + listCommandData.get(i).getCommandName(), 
                    "\u2192 " + listCommandData.get(i).getCommandDescription(),
                    false);

                number2++;

                if(number2 == 10) {
                    number2 = 0;
                    number3++;
                }
            }

            for(EmbedBuilder eb : embedBuilders) {
                eb.setFooter("Usable in DMs also!", Constants.gw2LogoNoBackground);
                eb.setThumbnail(Constants.gw2LogoNoBackground);
                eb.setColor(Color.pink);
                eb.setTitle("HELP COMMAND");
            }

            HelpButtonEventListener.embedBuilders = embedBuilders;
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {

        if(event.getButton().getId().equals("left")) {
            for(int i = 0; i < embedBuilders.size(); i++) {
                if(embedBuilders.get(i).getFields().get(0).equals(event.getMessage().getEmbeds().get(0).getFields().get(0))) {
                    selectedEmbed = embedBuilders.get(i);
                }
            }

            selectedEmbed = embedBuilders.get(embedBuilders.indexOf(selectedEmbed) - 1);

            if(selectedEmbed.equals(embedBuilders.get(0))) {
                event.getHook().editMessageComponentsById(
                    event.getMessageId(), 
                        ActionRow.of(
                            event.getMessage().getButtonById("left").asDisabled(),
                            event.getMessage().getButtonById("right").asEnabled()
                        )
                    ).queue();
            } else {
                event.getHook().editMessageComponentsById(event.getMessageId(),
                    ActionRow.of(
                        event.getMessage().getButtonById("left").asEnabled(),
                        event.getMessage().getButtonById("right").asEnabled()
                    )
                ).queue();
            }

            event.deferEdit().setEmbeds(selectedEmbed.build()).queue();
        } else if(event.getButton().getId().equals("right")) {
            for(int i = 0; i < embedBuilders.size(); i++) {
                if(embedBuilders.get(i).getFields().get(0).equals(event.getMessage().getEmbeds().get(0).getFields().get(0))) {
                    selectedEmbed = embedBuilders.get(i);
                }
            }

            selectedEmbed = embedBuilders.get(embedBuilders.indexOf(selectedEmbed) + 1);

            if(selectedEmbed.equals(embedBuilders.get(embedBuilders.size() - 1))) {
                event.getHook().editMessageComponentsById(
                    event.getMessageId(), 
                    ActionRow.of(
                        event.getMessage().getButtonById("left").asEnabled(),
                        event.getMessage().getButtonById("right").asDisabled()
                    )
                    ).queue();

            } else {
                event.getHook().editMessageComponentsById(event.getMessageId(),
                    ActionRow.of(
                        event.getMessage().getButtonById("left").asEnabled(),
                        event.getMessage().getButtonById("right").asEnabled()
                    )
                ).queue();
            }

            event.deferEdit().setEmbeds(selectedEmbed.build()).queue();
        }
    }
}