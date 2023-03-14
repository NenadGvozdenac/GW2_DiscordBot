package com.gw2.discordbot.Events;

import com.gw2.discordbot.DiscordBot.Constants;
import com.gw2.discordbot.Miscellaneous.SignupExcelWriting;
import com.gw2.discordbot.Miscellaneous.SignupExcelWriting.Type;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class StaticMemberAddEvent extends ListenerAdapter {

    private User user;
    private Type type;

    public StaticMemberAddEvent(User user, Type TYPE) {
        this.user = user;
        this.type = TYPE;
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if(event.getComponentId().equals("staticaddplayermenu")) {
            String value = event.getSelectedOptions().get(0).getValue();

            SignupExcelWriting.addStaticMember(user, value, type);

            event.deferEdit().setContent("`Added user " + user.getAsMention() + " as " + value + "!`").setComponents().queue();

            Role staticRoleToAdd = event.getGuild().getRoleById(this.type == Type.RAID ? Constants.staticRoleID : Constants.strikeStaticRoleID);

            event.getGuild().removeRoleFromMember(user, event.getGuild().getRoleById("1013185863116660838")).queue();
            event.getGuild().addRoleToMember(user, staticRoleToAdd).queue();

            user.openPrivateChannel().queue(channel -> channel.sendMessage("```You have been added as a fulltime member into the static by " + event.getUser().getAsTag() + (type == Type.RAID ? ", the raid static!" : ", the strikes static!") + " Enjoy your stay.```").queue());

            event.getJDA().removeEventListener(this);
        }
    }
}
