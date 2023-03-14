package com.gw2.discordbot.Events;

import com.gw2.discordbot.Miscellaneous.SignupExcelWriting;
import com.gw2.discordbot.Miscellaneous.SignupExcelWriting.Type;

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

            event.getGuild().removeRoleFromMember(user, event.getGuild().getRoleById("1013185863116660838")).queue();
            event.getGuild().addRoleToMember(user, event.getGuild().getRoleById("1007918310190501948")).queue();

            user.openPrivateChannel().queue(channel -> channel.sendMessage("```You have been added as a fulltime member into the static by " + event.getUser().getAsTag() + "! Enjoy your stay.```").queue());

            event.getJDA().removeEventListener(this);
        }
    }
}
