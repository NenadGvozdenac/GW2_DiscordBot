package com.gw2.discordbot;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class LeaveEvent extends ListenerAdapter {

    static String channelWelcomeId = "1007916158478987364";
    static String channelGoodbyeId = "1007916643923537970";

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setFooter("Goodbye from " + event.getGuild().getName() + "!");
        eb.setThumbnail(event.getUser().getAvatarUrl());
        eb.setTitle("Goodbye " + event.getUser().getName() + "!");
        eb.setColor(Color.pink);

        eb.setDescription(
            "- Member " + event.getUser().getName() + " just left the guild.\n" +
            "- Hopefully we see them again!\n\n" +
            "**Current member count: " + (event.getGuild().getMemberCount() - 1) + 
            "**\n\uD83D\uDE1E \uD83D\uDE1E"
        );

        event.getGuild().getTextChannelById(channelGoodbyeId).sendMessageEmbeds(eb.build()).queue();
    }
}
