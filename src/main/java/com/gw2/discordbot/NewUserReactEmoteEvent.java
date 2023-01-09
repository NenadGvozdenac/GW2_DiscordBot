package com.gw2.discordbot;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class NewUserReactEmoteEvent extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        if(event.getReaction().getEmoji().getFormatted().equals(Constants.achievementPointIconEmoji)) {
            if(event.getChannel().asTextChannel().equals(event.getGuild().getTextChannelById("1007916790963253320"))) {
                event.getGuild().addRoleToMember(event.getUser(), event.getGuild().getRoleById("1007918477719388231")).queue(
                    action -> event.getGuild().removeRoleFromMember(event.getUser(), event.getGuild().getRoleById("1010591706883838042")).queue()
                );
            }
        }

        if(event.getReaction().getEmoji().getFormatted().equals("\uD83D\uDCF0")) {
            if(event.getChannel().asTextChannel().equals(event.getGuild().getTextChannelById("1010596768209190992"))) {
                event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("1010591966502862848")).queue();
            }
        }

        if(event.getReaction().getEmoji().getFormatted().equals("\u265F")) {
            if(event.getChannel().asTextChannel().equals(event.getGuild().getTextChannelById("1010596768209190992"))) {
                event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("1010592026846314496")).queue();
            }
        }

        if(event.getReaction().getEmoji().getFormatted().equals(Constants.fractalIconEmoji)) {
            if(event.getChannel().asTextChannel().equals(event.getGuild().getTextChannelById("1010596768209190992"))) {
                event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById("1010592048753160252")).queue();
            }
        }
    }

    @Override
    public void onMessageReactionRemove(@Nonnull MessageReactionRemoveEvent event) {

        if(event.getReaction().getEmoji().getFormatted().equals("\uD83D\uDCF0")) {
            if(event.getChannel().asTextChannel().equals(event.getGuild().getTextChannelById("1010596768209190992"))) {
                event.getGuild().removeRoleFromMember(UserSnowflake.fromId(event.getUserId()), event.getGuild().getRoleById("1010591966502862848")).queue();
            }
        }

        if(event.getReaction().getEmoji().getFormatted().equals("\u265F")) {
            if(event.getChannel().asTextChannel().equals(event.getGuild().getTextChannelById("1010596768209190992"))) {
                event.getGuild().removeRoleFromMember(UserSnowflake.fromId(event.getUserId()), event.getGuild().getRoleById("1010592026846314496")).queue();
            }
        }

        if(event.getReaction().getEmoji().getFormatted().equals(Constants.fractalIconEmoji)) {
            if(event.getChannel().asTextChannel().equals(event.getGuild().getTextChannelById("1010596768209190992"))) {
                event.getGuild().removeRoleFromMember(UserSnowflake.fromId(event.getUserId()), event.getGuild().getRoleById("1010592048753160252")).queue();
            }
        }
    }
}