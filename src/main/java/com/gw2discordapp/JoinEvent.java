package com.gw2discordapp;

import java.awt.Color;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class JoinEvent extends ListenerAdapter {

    static String channelWelcomeId = "1007916158478987364";
    static String channelGoodbyeId = "1007916643923537970";

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {

        EmbedBuilder eb = new EmbedBuilder();
        
        eb.setFooter("Welcome to " + event.getGuild().getName() + "!");
        eb.setThumbnail(event.getMember().getEffectiveAvatarUrl());
        eb.setTitle("Welcome " + event.getMember().getEffectiveName() + "!");
        eb.setColor(Color.pink);
        eb.setDescription(
            "- Thank you for joining " + event.getGuild().getName() + "!\n" +
            "- Take a look at <#1007916790963253320> for information.\n" + 
            "- List all commands with /help\n" +
            "- Have a nice stay!\n" + 
            "\n" + 
            "**Current member count: " + (event.getGuild().getMemberCount() - 1) + 
            "**\n\u2764\uFE0F \u2764\uFE0F"
        );

        event.getGuild().getTextChannelById(channelWelcomeId).sendMessage(event.getMember().getAsMention()).queue(k -> k.delete().queue());
        event.getGuild().getTextChannelById(channelWelcomeId).sendMessageEmbeds(eb.build()).queue();
        event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("1007918477719388231")).queue();
    }

}
