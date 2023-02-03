package com.gw2.discordbot.Events;

import com.gw2.discordbot.Miscellaneous.SignupExcelWriting;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class StaticMemberAddEvent extends ListenerAdapter {

    private User user;

    public StaticMemberAddEvent(User user) {
        this.user = user;
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if(event.getComponentId().equals("staticaddplayermenu")) {
            String value = event.getSelectedOptions().get(0).getValue();

            SignupExcelWriting.addStaticMember(user, value);

            event.deferEdit().setContent("`Added user " + user.getAsMention() + " as " + value + "!`").setComponents().queue();

            event.getGuild().removeRoleFromMember(user, event.getGuild().getRoleById("1013185863116660838")).queue();
            event.getGuild().addRoleToMember(user, event.getGuild().getRoleById("1007918310190501948")).queue();

            user.openPrivateChannel().queue(channel -> channel.sendMessage("```You have been added as a fulltime member into the static by " + event.getUser().getAsTag() + "! Enjoy your stay.```").queue());

            event.getJDA().removeEventListener(this);
        }
    }
}
