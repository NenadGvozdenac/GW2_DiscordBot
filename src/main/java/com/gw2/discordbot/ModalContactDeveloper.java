package com.gw2.discordbot;

import java.awt.Color;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@SuppressWarnings("null")
public class ModalContactDeveloper extends ListenerAdapter {
    
    @Override
    public void onModalInteraction(@Nonnull ModalInteractionEvent event) {
        if(event.getModalId().equals("contact_developer")) {
            event.deferReply().queue();
            String subject = event.getValue("subject").getAsString();
            String body = event.getValue("body").getAsString();

            createSupportTicket(subject, body, event.getUser());
            event.getHook().sendMessage("Thank you for sending your support ticket. It is in <#1010617354293616712>.").setEphemeral(true).queue();
        }
    }

    private void createSupportTicket(String subject, String body, User user) {

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.cyan);
        eb.setAuthor("By: " + user.getName());
        eb.setTitle(subject);
        eb.setDescription(body);
        eb.setThumbnail(Constants.gw2LogoNoBackground);
        eb.setFooter(user.getName() + " used /contact-developer", Constants.gw2LogoNoBackground);

        DiscordBot.jda.getGuildById("1007915730928418856").getTextChannelById("1010617354293616712").sendMessageEmbeds(eb.build()).queue();
    }
}
