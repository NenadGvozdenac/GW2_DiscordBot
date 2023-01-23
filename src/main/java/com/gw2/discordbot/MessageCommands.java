package com.gw2.discordbot;

import java.awt.Color;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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

        EmbedBuilder eb = new EmbedBuilder();

        for(String string : listOfDpsReportLinks) {
            if(!string.startsWith("https://dps.report/") && !string.startsWith("?logs")) {
                eb.setColor(Color.RED);
                eb.setTitle("Parsing error!");
                eb.setDescription("**Command Usage:**\n```?logs\nhttps://dps.report/...\nhttps://dps.report/...\nhttps://dps.report/...```");
                message.editMessageEmbeds(eb.build()).queue();
                return;
            }
        }

        for(int i = 1; i < listOfDpsReportLinks.length; i++) {
            listOfDpsReports.add(listOfDpsReportLinks[i]);
        }
        
        ArrayList<Boss> listOfBosses = Boss.getBossArrayFromLinks(listOfDpsReports);
        eb.setColor(Color.MAGENTA);
    
        LinkedHashMap<String, ArrayList<Boss>> wings = new LinkedHashMap<>() {
            {
                put("Spirit Vale", new ArrayList<>());
                put("Salvation Pass", new ArrayList<>());
                put("Stronghold of the Faithful", new ArrayList<>());
                put("Bastion of the Penitent", new ArrayList<>());
                put("Hall of Chains", new ArrayList<>());
                put("Mythwright Gambit", new ArrayList<>());
                put("The Key of Ahdashim", new ArrayList<>());
                put("Sunqua Peak", new ArrayList<>());
                put("Shattered Observatory", new ArrayList<>());
                put("Nightmare", new ArrayList<>());
                put("Practice Room", new ArrayList<>());
                put("Icebrood Saga", new ArrayList<>());
                put("End of Dragons", new ArrayList<>());
                put("Unidentified", new ArrayList<>());
            }
        };

        Boss firstBoss = listOfBosses.get(0);
        Boss lastBoss = listOfBosses.get(listOfBosses.size() - 1);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss X", Locale.getDefault());

        long startMilis;
        long endMilis;
        try {
            endMilis = sdf.parse(lastBoss.endTime).getTime();
            startMilis = sdf.parse(firstBoss.startTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        float minutes = TimeUnit.MILLISECONDS.toMinutes(endMilis - startMilis);

        for(Boss boss : listOfBosses) {
            if(wings.containsKey(boss.wingName)) {
                wings.get(boss.wingName).add(boss);
            }
        }

        String title = minutes > 60 ? (int)Math.floor((minutes / 60)) + " hours, " + (int)Math.floor((minutes % 60)) + " minutes clear" : (int)Math.floor(minutes) + " minutes clear";
        eb.setTitle(title);

        List<Boss> failedBosses = new ArrayList<>();

        listOfBosses.forEach(boss -> {
            if(boss.isFailed) {
                failedBosses.add(boss);
            }
        });

        for(ArrayList<Boss> wing : new ArrayList<>(wings.values())) {

            if(wing.isEmpty()) {
                continue;
            }

            String string = "";

            boolean toAddField = false;

            title = wing.get(0).wingName;

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

            eb1.setColor(Color.MAGENTA);

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

            eb1.setTitle(failedBosses.size() + " fail(s). Time taken: " + minutes1 + " minutes, " + seconds1 + " seconds.");

            eb1.setDescription(failure);
            message.getChannel().sendMessageEmbeds(eb1.build()).queue();
        }

        event.getMessage().delete().queue();
    }
}
