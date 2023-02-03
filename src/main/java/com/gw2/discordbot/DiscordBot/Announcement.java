package com.gw2.discordbot.DiscordBot;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Announcement extends ListenerAdapter {
    
    @Override
    public void onMessageReceived( MessageReceivedEvent event) {

        if(!event.getGuild().retrieveMember(event.getMember()).complete().isOwner())
            return;

        if(event.getMessage().getContentRaw().equals("!sendMessage")) {

            String firstMessage = "```ROLES```";
            String secondMessage = "- <@&1010591966502862848> is a **NEWS** role. You will be pinged about news from Guild Wars 2! Click \uD83D\uDCF0.\n";
            String thirdMessage = "- <@&1010592026846314496> is a **FORUMS** role. You will be pinged when new patch notes drop live! Click \u265F.\n ";
            String fourthMessage = "- <@&1010592048753160252> is a **DAILIES** role. You will be pinged when new dailies drop, and what they are! Click " + Constants.fractalIconEmoji + ".";
            String fifthMessage = "- Click any emote to get that emoji!";
            
            event.getChannel().sendMessage(firstMessage).queue();
            event.getChannel().sendMessage(secondMessage).queue();
            event.getChannel().sendMessage(thirdMessage).queue();
            event.getChannel().sendMessage(fourthMessage).queue();
            event.getChannel().sendMessage(fifthMessage).queue(message -> {
                message.addReaction(Emoji.fromFormatted("\uD83D\uDCF0")).queue();
                message.addReaction(Emoji.fromFormatted("\u265F")).queue();
                message.addReaction(Emoji.fromFormatted(Constants.fractalIconEmoji)).queue();
            });
        }
    }
}
