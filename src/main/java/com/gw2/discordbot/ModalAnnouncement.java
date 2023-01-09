package com.gw2.discordbot;

import java.awt.Color;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ModalAnnouncement extends ListenerAdapter {
    
    @Override
    public void onModalInteraction(@Nonnull ModalInteractionEvent event) {
        if(event.getModalId().equals("announcementmodal")) {
            String title = event.getValue("title").getAsString();
            String content = event.getValue("content").getAsString();
            String footer = "Announced by @" + event.getUser().getAsTag() + "!";
    
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(title);
            eb.setDescription(content);
            eb.setFooter(footer);
            eb.setColor(Color.cyan);
            eb.setThumbnail(Constants.gw2LogoNoBackground);
        
            event.deferReply(true).queue();
            event.getHook().sendMessage("Successfully sent the announcement!").queue();
            event.getGuild().getTextChannelById(Constants.announcementChannelID).sendMessageEmbeds(eb.build()).queue();
        }
    }
}
