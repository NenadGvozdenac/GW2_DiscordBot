package com.gw2.discordbot;

import java.awt.Color;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@SuppressWarnings("null")
public class JoinEvent extends ListenerAdapter {

    static String channelWelcomeId = "1007916158478987364";
    static String channelGoodbyeId = "1007916643923537970";

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {

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

        event.getGuild().getTextChannelById(channelWelcomeId).sendMessageEmbeds(eb.build()).queue();
        event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("1010591706883838042")).queue();

        event.getUser().openPrivateChannel().queue(channel -> {
            EmbedBuilder embedBuilder = new EmbedBuilder();

            embedBuilder.setFooter("Welcome to [BA] Bananas!");
            embedBuilder.setThumbnail(event.getUser().getAvatarUrl());
            embedBuilder.setDescription("- Welcome to the server [BA]!\n- Enjoy your stay!\n- Please take a look at <#1007916790963253320> for information.\n- If you are here for the static group, you will be given your permissions soon.\n- Once you read <#1007916790963253320>, click on the emoji on the bottom!\nFor any concerns, ask <@374913214636359681>!\n\n- Thank you for joining!");
    
            channel.sendMessageEmbeds(embedBuilder.build()).queue();
        });
    }

}
