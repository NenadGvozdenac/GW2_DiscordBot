package com.gw2.discordbot;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@SuppressWarnings("null")
public class StaticAddTryout extends ListenerAdapter {
    
    private User user;

    StaticAddTryout(User user) {
        this.user = user;
    }

    @Override
    public void onSelectMenuInteraction(@Nonnull SelectMenuInteractionEvent event) {
        if(event.getComponentId().equals("staticaddtryout")) {
            event.deferEdit().setContent("`You successfully gave that person a tryout role.`").setComponents().queue();
            user.openPrivateChannel().queue(channel -> channel.sendMessage("```You have been assigned a static tryout role for the static [BA] Bananas. \nRole: " + event.getSelectedOptions().get(0).getValue() + "\nYou will be instructed further by an administrator.```").queue());
            Role staticApplicantRole = event.getGuild().getRoleById("1013185863116660838");
            event.getGuild().addRoleToMember(event.getGuild().retrieveMember(user).complete(), staticApplicantRole).queue();

            EmbedBuilder eb = new EmbedBuilder();
            eb.setFooter(RandomFunnyQuote.getFunnyQuote());
            eb.setTitle(user.getAsTag());
            eb.setDescription("```" + user.getAsTag() + " applied for the static.\nRole: " + event.getSelectedOptions().get(0).getValue() + ".```");

            event.getGuild().getTextChannelById(Constants.staticApplicationsChannelID).sendMessageEmbeds(eb.build()).queue();

            event.getJDA().removeEventListener(this);
        }
    }
}
