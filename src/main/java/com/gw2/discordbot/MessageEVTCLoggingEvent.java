package com.gw2.discordbot;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kong.unirest.HttpResponse;

import kotlin.Pair;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageEVTCLoggingEvent extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        if(event.getMessage().getAttachments().isEmpty())
            return;

        Boolean activateTheThread = false;

        for(Attachment attachment : event.getMessage().getAttachments()) {
            if(attachment.getFileExtension().equals("zevtc")) {
                activateTheThread = true;
                break;
            }
        }

        if(activateTheThread == false) {
            return;
        }

        event.getMessage().getAttachments().forEach(attachment -> {
            if(attachment.getFileExtension().equals("zevtc")) {

                try (InputStream file = attachment.getProxy().download().get()) {
                    File fileTest = new File(attachment.getFileName());

                    if(!fileTest.exists()) {
                        Files.createFile(Path.of(attachment.getFileName()));
                    }

                    Files.copy(file, fileTest.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    HttpResponse<String> response = DpsReportApi.UPLOAD_FILE(fileTest);

                    fileTest.delete();

                    JsonObject jsonResponseElement = JsonParser.parseString(response.getBody().toString()).getAsJsonObject();

                    String logPermaLink = jsonResponseElement.get("permalink").getAsString();
                    String logBossName = jsonResponseElement.get("encounter").getAsJsonObject().get("boss").getAsString();
                    boolean logBossIsCm = jsonResponseElement.get("encounter").getAsJsonObject().get("isCm").getAsBoolean();
                    boolean logIsSuccess = jsonResponseElement.get("encounter").getAsJsonObject().get("success").getAsBoolean();
                    Integer logCollectedDps = jsonResponseElement.get("encounter").getAsJsonObject().get("compDps").getAsInt();

                    HttpResponse<String> eliteInsightsResponse = DpsReportApi.GET_ELITE_INSIGHTS_RESPONSE(logPermaLink);
                    JsonObject jsonEliteInsightsResponse = JsonParser.parseString(eliteInsightsResponse.getBody().toString()).getAsJsonObject();
                    
                    String logTimeStart = jsonEliteInsightsResponse.get("timeStart").getAsString();
                    String logTimeEnd = jsonEliteInsightsResponse.get("timeEnd").getAsString();
                    String arcDpsVersion = jsonEliteInsightsResponse.get("arcVersion").getAsString();
                    String gw2Version = jsonEliteInsightsResponse.get("gW2Build").getAsString();
                    String recordedBy = jsonEliteInsightsResponse.get("recordedBy").getAsString();
                    String logTimeTaken = jsonEliteInsightsResponse.get("duration").getAsString();
                    String iconUrl = jsonEliteInsightsResponse.get("fightIcon").getAsString();

                    JsonArray logPlayers = jsonEliteInsightsResponse.get("players").getAsJsonArray();

                    ArrayList<String> playerProfession = new ArrayList<>();
                    ArrayList<String> playerCharacterNames = new ArrayList<>();
                    ArrayList<Integer> playerDps = new ArrayList<>();

                    logPlayers.forEach(player -> {
                        JsonObject object = player.getAsJsonObject();
                        playerProfession.add(object.get("profession").getAsString().toLowerCase());
                        playerCharacterNames.add(object.get("name").getAsString());

                        Integer i = object.get("dpsAll").getAsJsonArray().get(0).getAsJsonObject().get("dps").getAsInt();
                        playerDps.add(i);
                    });

                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setColor(Color.pink);
                    eb.setDescription(logPermaLink);
                    eb.setTitle(recordedBy);
                    eb.setThumbnail(iconUrl);
                    eb.setFooter("Thank you for using " + Main.jda.getSelfUser().getName() + "!", Constants.gw2LogoNoBackground);
                    eb.addField("BOSS NAME", logBossName, true);
                    eb.addField("IS CM?", logBossIsCm == true ? "\u2705" : "\u274C", true);
                    eb.addField("SUCCESS?", logIsSuccess == true ? "\u2705" : "\u274C", true);
                    eb.addField("TIME TAKEN", logTimeTaken.toString(), true);
                    eb.addField("COLLECTIVE DPS", logCollectedDps.toString(), true);
                    eb.addField("GW2 VERSION", gw2Version, true);
                    eb.addField("START TIME", logTimeStart.split(" ")[0] + "\n" + logTimeStart.split(" ")[1], true);
                    eb.addField("END TIME", logTimeEnd.split(" ")[0] + "\n" + logTimeEnd.split(" ")[1], true);
                    eb.addField("ARCDPS VERSION", arcDpsVersion, true);

                    ArrayList<Pair<String, Integer>> listOfPairs = new ArrayList<>();

                    for(int i = 0; i < playerCharacterNames.size(); i++) {
                        listOfPairs.add(new Pair<String, Integer>(playerCharacterNames.get(i), playerDps.get(i)));
                    }

                    file.close();
                    File fileChart = ChartGenerator.generateChart("DPS STATS", listOfPairs);

                    event.getMessage().replyEmbeds(eb.setImage("attachment://bitmapsave.png").build()).addFile(ChartGenerator.generateChart("DPS STATS", listOfPairs)).queue();
                    fileChart.delete();
                } catch(InterruptedException | IOException | ExecutionException e) {
                    System.out.println("Couldn't do that!");
                }
            }
        });
    }

}
