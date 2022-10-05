package com.gw2.discordbot;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;

public class CharacterChoosingSelectMenu extends ListenerAdapter {

    @Override
    @SuppressWarnings("null")
    public void onSelectMenuInteraction(@Nonnull SelectMenuInteractionEvent event) {

        if(event.getSelectMenu().getId().equals("character_choosing")) {

            event.deferEdit().queue();

            UserApi api = UserApi.GET_API_INFO(event.getUser().getId());

            MessageEmbed embedForEditing;

            if(event.isFromGuild())
                embedForEditing = Gw2Api.GET_CHARACTER_INFO(api.getApiKey(), event.getSelectedOptions().get(0).getLabel(), event.getGuild().getName());
            else
                embedForEditing = Gw2Api.GET_CHARACTER_INFO(api.getApiKey(), event.getSelectedOptions().get(0).getLabel());

            SelectMenu selectMenu = event.getSelectMenu();
            SelectMenu.Builder selectMenuBuilder = selectMenu.createCopy();

            selectMenuBuilder.setDefaultOptions(event.getSelectedOptions());
            event.getHook().editOriginalEmbeds(embedForEditing).setActionRow(selectMenuBuilder.build()).queue();
        }
    }    
}
