package com.gw2.discordbot;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ButtonSignUpPressEvent extends ListenerAdapter {
    
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if(event.getComponentId().equals("cancelsignupmenu")) {
            event.deferEdit().setContent("`You decided to cancel the action.`").setActionRows().queue();
            return;
        }

        if(event.getComponentId().equals("helpsignupmenu")) {
            event.deferEdit().setContent("```This is an interaction to select roles for this week's static raid. \nWhatever you wish to select here, will be stored in our database. \nWhen the raid starts, you will be pinged on the role that you selected.\nIf you have any questions, feel free to ask in static chat.\nIf you wish to continue, run this command again.```").setActionRows().queue();
            return;
        }
    }
}
