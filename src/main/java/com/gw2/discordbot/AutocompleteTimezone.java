package com.gw2.discordbot;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

@SuppressWarnings("null")
public class AutocompleteTimezone extends ListenerAdapter {

    private String[] timezone = new String[]{
        "CET",
        "GMT",
        "EET",
        "GMT+3"
    };

    @Override
    public void onCommandAutoCompleteInteraction(@Nonnull CommandAutoCompleteInteractionEvent event) {
        if(event.getName().equals("calculate_time") && event.getFocusedOption().getName().equals("timezone")) {
            List<Command.Choice> options = Stream.of(timezone)
                    .filter(word -> word.startsWith(event.getFocusedOption().getValue()))
                    .map(word -> new Command.Choice(word, word))
                    .collect(Collectors.toList());
            
            event.replyChoices(options).queue();
        }
    }
}
