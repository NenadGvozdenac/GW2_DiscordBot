package com.gw2.discordbot;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class StaticMemberAddEvent extends ListenerAdapter {

    private User user;

    public StaticMemberAddEvent(User user) {
        this.user = user;
    }

    @Override
    public void onSelectMenuInteraction(@NotNull SelectMenuInteractionEvent event) {
        if(event.getComponentId().equals("staticaddplayermenu")) {
            String value = event.getSelectedOptions().get(0).getValue();

            SignupExcelWriting.addStaticMember(user, value);

            event.deferEdit().setContent("`Added user " + user.getAsMention() + " as " + value + "!`").setActionRows().queue();

            event.getGuild().removeRoleFromMember(user, event.getGuild().getRoleById("1013185863116660838")).queue();
            event.getGuild().addRoleToMember(user, event.getGuild().getRoleById("1007918310190501948")).queue();
            event.getJDA().removeEventListener(this);
        }
    }
}
