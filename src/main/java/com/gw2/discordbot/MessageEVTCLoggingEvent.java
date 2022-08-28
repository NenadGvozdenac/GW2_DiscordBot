package com.gw2.discordbot;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Webhook;
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

                    JsonElement jsonResponseElement = JsonParser.parseString(response.getBody().toString());

                    String logPermaLink = jsonResponseElement.getAsJsonObject().get("permalink").getAsString();
                    String logBossName = jsonResponseElement.getAsJsonObject().get("encounter").getAsJsonObject().get("boss").getAsString();
                    Boolean logBossIsCm = jsonResponseElement.getAsJsonObject().get("encounter").getAsJsonObject().get("isCm").getAsBoolean();
                    Boolean logIsSuccess = jsonResponseElement.getAsJsonObject().get("encounter").getAsJsonObject().get("success").getAsBoolean();
                    Integer logCollectedDps = jsonResponseElement.getAsJsonObject().get("encounter").getAsJsonObject().get("compDps").getAsInt();

                    HttpResponse<String> eliteInsightsResponse = DpsReportApi.GET_ELITE_INSIGHTS_RESPONSE(logPermaLink);
                    JsonElement jsonEliteInsightsResponse = JsonParser.parseString(eliteInsightsResponse.getBody().toString());
                    
                    String logTimeStart = jsonEliteInsightsResponse.getAsJsonObject().get("timeStart").getAsString();
                    String logTimeEnd = jsonEliteInsightsResponse.getAsJsonObject().get("timeEnd").getAsString();
                    String arcDpsVersion = jsonEliteInsightsResponse.getAsJsonObject().get("arcVersion").getAsString();
                    String gw2Version = jsonEliteInsightsResponse.getAsJsonObject().get("gW2Build").getAsString();
                    String recordedBy = jsonEliteInsightsResponse.getAsJsonObject().get("recordedBy").getAsString();
                    String logTimeTaken = jsonEliteInsightsResponse.getAsJsonObject().get("duration").getAsString();
                    String iconUrl = jsonEliteInsightsResponse.getAsJsonObject().get("fightIcon").getAsString();

                    JsonArray logPlayers = jsonEliteInsightsResponse.getAsJsonObject().get("players").getAsJsonArray();

                    ArrayList<String> playerProfession = new ArrayList<>();
                    ArrayList<String> playerCharacterNames = new ArrayList<>();
                    ArrayList<Integer> playerDps = new ArrayList<>();

                    logPlayers.forEach(player -> {
                        playerProfession.add(player.getAsJsonObject().get("profession").getAsString().toLowerCase());
                        playerCharacterNames.add(player.getAsJsonObject().get("name").getAsString());

                        Integer i = player.getAsJsonObject().get("dpsAll").getAsJsonArray().get(0).getAsJsonObject().get("dps").getAsInt();
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

                    String stringToSend = "```";

                    for(int i = 0; i < playerCharacterNames.size(); i++) {
                        String currentString = String.format("%-20s | %-10s | %-5s DPS\n", playerCharacterNames.get(i), playerProfession.get(i).toUpperCase(), String.valueOf(playerDps.get(i)));
                        stringToSend += currentString;
                    }

                    stringToSend += "```";

                    eb.addField("", stringToSend, false);

                    WebhookEmbedBuilder embedForSending;
                    embedForSending = WebhookEmbedBuilder.fromJDA(eb.build());

                    WebhookMessageBuilder messageBuilder = new WebhookMessageBuilder();
                    messageBuilder.addEmbeds(embedForSending.build());
                    messageBuilder.setAvatarUrl(event.getJDA().getSelfUser().getAvatarUrl());

                    WebhookClientBuilder clientBuilder = null;

                    if(event.getChannel().getId().equals(Constants.staticChatChannelID)) {
                        Boolean needToCreateWebhook = true;
                        Webhook webhook = null;
    
                        List<Webhook> availableWebhooks = Main.jda.getTextChannelById(Constants.staticChatChannelID).retrieveWebhooks().complete();
    
                        for(Webhook webhookName : availableWebhooks) {
                            if(webhookName.getName().equals("Guild Wars 2 Logs")) {
                                needToCreateWebhook = false;
                                webhook = webhookName;
                                break;
                            }
                        }

                        if(needToCreateWebhook) {
                            webhook = Main.jda.getTextChannelById(Constants.staticChatChannelID).createWebhook("Guild Wars 2 Logs").complete();
                        }

                        messageBuilder.setContent("New log! " + event.getAuthor().getAsMention());
                        clientBuilder = WebhookClientBuilder.fromJDA(webhook);
                    } else {
                        Boolean needToCreateWebhook = true;
                        Webhook webhook = null;
    
                        List<Webhook> availableWebhooks = Main.jda.getTextChannelById(Constants.generalLogUploadsChannelID).retrieveWebhooks().complete();
    
                        for(Webhook webhookName : availableWebhooks) {
                            if(webhookName.getName().equals("Guild Wars 2 Logs")) {
                                needToCreateWebhook = false;
                                webhook = webhookName;
                                break;
                            }
                        }
    
                        if(needToCreateWebhook) {
                            webhook = Main.jda.getTextChannelById(Constants.generalLogUploadsChannelID).createWebhook("Guild Wars 2 Logs").complete();
                        }
    
                        messageBuilder.setContent("New log! " + event.getMember().getAsMention());
                        clientBuilder = WebhookClientBuilder.fromJDA(webhook);
                    }
                        
                    clientBuilder.build().send(messageBuilder.build());
                } catch(InterruptedException | IOException | ExecutionException e) {

                }
            }
        });
    
    }

}
