package com.gw2.discordbot;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public  class StaticMemberRemoveEvent extends ListenerAdapter {

    @Override
    public void onSelectMenuInteraction(@Nonnull SelectMenuInteractionEvent event) {

        if(event.getComponentId().equals("staticremoveplayermenu")) {
            String userId = event.getSelectedOptions().get(0).getValue();

            SignupExcelWriting.removeStaticMember(event.getGuild().retrieveMemberById(userId).complete().getUser());

            event.getGuild().removeRoleFromMember(event.getGuild().retrieveMemberById(userId).complete(), event.getGuild().getRoleById("1007918310190501948")).queue();
            event.deferEdit().setContent("`Successfully removed the user from the static.`").setComponents().queue();
        }
    }
}
