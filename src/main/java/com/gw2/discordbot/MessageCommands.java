package com.gw2.discordbot;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageCommands extends ListenerAdapter {

    public static String prefix = "?";

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        
        if(!event.getMessage().getContentRaw().startsWith(prefix)) return;
        if(event.getAuthor().isBot()) return;

        String[] args = event.getMessage().getContentRaw().split("\n");
        String firstArgument = args[0].trim().substring(1, args[0].trim().length());

        switch(firstArgument) {
            case "logs":
                LOGS_EVENT_MESSAGE(event);
            break;
        }
    }

    private void LOGS_EVENT_MESSAGE(MessageReceivedEvent event) {

        Message message = event.getMessage().replyEmbeds(Constants.loadingEmbedBuilder).complete();

        String[] listOfDpsReportLinks = event.getMessage().getContentRaw().split("\n");
        ArrayList<String> listOfDpsReports = new ArrayList<>();

        for(int i = 1; i < listOfDpsReportLinks.length; i++) {
            listOfDpsReports.add(listOfDpsReportLinks[i]);
        }
        
        ArrayList<Boss> listOfBosses = Boss.getBossArrayFromLinks(listOfDpsReports);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.MAGENTA);
    
        List<List<Boss>> Wings = new ArrayList<>() {
            {
                add(new ArrayList<>());
            }
        };

        for(int i = 0, j = 0; i < listOfBosses.size(); i++) {
            if(i == listOfBosses.size() - 1) {
                Wings.get(j).add(listOfBosses.get(i));
            } else if(listOfBosses.get(i).wingName.equals(listOfBosses.get(i+1).wingName)) {
                Wings.get(j).add(listOfBosses.get(i));
            } else {
                Wings.get(j).add(listOfBosses.get(i));
                Wings.add(new ArrayList<>());
                j++;
            }
        }

        List<Boss> failedBosses = new ArrayList<>();

        listOfBosses.forEach(boss -> {
            if(boss.isFailed) {
                failedBosses.add(boss);
            }
        });

        for(List<Boss> wing : Wings) {

            String string = "";

            boolean toAddField = false;

            String title = wing.get(0).wingName;

            for(Boss boss : wing) {
                if(boss.isFailed) continue;
                toAddField = true;

                String time = "";

                float secondsTakenForKill = Math.round(Float.parseFloat(boss.killTime));

                if(secondsTakenForKill > 60) {
                    float minutesTakenForKill = secondsTakenForKill / 60;
                    secondsTakenForKill %= 60;

                    time += (int)Math.floor(minutesTakenForKill) + "m " + (int)Math.floor(secondsTakenForKill) + "s";
                } else {
                    time += (int)Math.floor(secondsTakenForKill) + "s";
                }

                string += (boss.emoji.equals("null") ? "" : boss.emoji + " ") + "[" + boss.bossName + "](" + boss.dpsReportLink + ") " + time + "\n";
            }

            if(toAddField)
                eb.addField(title.equals("null") ? "Unidentified" : title, string, false);
        }

        message.editMessageEmbeds(eb.build()).queue();
        if(!failedBosses.isEmpty()) {
            EmbedBuilder eb1 = new EmbedBuilder();

            eb1.setColor(Color.CYAN);

            String failure = "";

            long timeWiping = 0;

            for(Boss boss : failedBosses) {
                String time = "";

                float secondsTakenForKill = Math.round(Float.parseFloat(boss.killTime));

                if(secondsTakenForKill > 60) {
                    float minutesTakenForKill = secondsTakenForKill / 60;
                    secondsTakenForKill %= 60;

                    time += (int)Math.floor(minutesTakenForKill) + "m " + (int)Math.floor(secondsTakenForKill) + "s";
                } else {
                    time += (int)Math.floor(secondsTakenForKill) + "s";
                }

                failure += (boss.emoji.equals("null") ? "" : boss.emoji + " ") + "[" + boss.bossName + "](" + boss.dpsReportLink + ") " + time + "\n";
                timeWiping += Float.parseFloat(boss.killTime);
            }

            long minutes1 = timeWiping / 60;

            System.out.println("FAILURE : '" + failure + "'");

            timeWiping %= 60;

            long seconds1 = timeWiping;

            eb1.setTitle(failedBosses.size() + " fails. Time taken: " + minutes1 + " minutes, " + seconds1 + " seconds.");

            eb1.setDescription(failure);
            message.replyEmbeds(eb.build()).queue();
        }

        event.getMessage().delete().queue();
    }
}
