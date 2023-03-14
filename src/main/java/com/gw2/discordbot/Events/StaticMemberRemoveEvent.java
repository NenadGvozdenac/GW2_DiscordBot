package com.gw2.discordbot.Events;

import com.gw2.discordbot.DiscordBot.DiscordBot;
import com.gw2.discordbot.Miscellaneous.SignupExcelWriting;
import com.gw2.discordbot.Miscellaneous.SignupExcelWriting.Type;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public  class StaticMemberRemoveEvent extends ListenerAdapter {

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {

        if(event.getComponentId().equals("raidstaticremoveplayermenu")) {
            String userId = event.getSelectedOptions().get(0).getValue();

            SignupExcelWriting.removeStaticMember(DiscordBot.jda.retrieveUserById(userId).complete(), Type.RAID);

            String returnMessage = "";

            Role role = event.getGuild().getRoleById("1007918310190501948");

            if(event.getGuild().getMember(DiscordBot.jda.retrieveUserById(userId).complete()) != null) {
                event.getGuild().removeRoleFromMember(DiscordBot.jda.retrieveUserById(userId).complete(), role).queue();
                returnMessage += "`Successfully removed the roles from the user.`\n";
            }

            returnMessage += "`Successfully removed the user from the static.`";
            event.deferEdit().setContent(returnMessage).setComponents().queue();
        } else if(event.getComponentId().equals("strikestaticremoveplayermenu")) {
            String userId = event.getSelectedOptions().get(0).getValue();

            SignupExcelWriting.removeStaticMember(DiscordBot.jda.retrieveUserById(userId).complete(), Type.STRIKES);

            String returnMessage = "";

            Role role = event.getGuild().getRoleById("1007918310190501948");

            if(event.getGuild().getMember(DiscordBot.jda.retrieveUserById(userId).complete()) != null) {
                event.getGuild().removeRoleFromMember(DiscordBot.jda.retrieveUserById(userId).complete(), role).queue();
                returnMessage += "`Successfully removed the roles from the user.`\n";
            }

            returnMessage += "`Successfully removed the user from the static.`";
            event.deferEdit().setContent(returnMessage).setComponents().queue();
        }
    }
}
