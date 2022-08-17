package com.gw2discordapp;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
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

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                event.getMessage().getAttachments().forEach(attachment -> {
                    if(attachment.getFileExtension().equals("zevtc")) {

                        event.getMessage().reply("`Uploaded your log to the dps.report.`").queue(message -> {
                            message.delete().queueAfter(5, TimeUnit.SECONDS);
                        });

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
                            Integer logTimeTaken = jsonResponseElement.getAsJsonObject().get("encounter").getAsJsonObject().get("duration").getAsInt();
                            Integer logCollectedDps = jsonResponseElement.getAsJsonObject().get("encounter").getAsJsonObject().get("compDps").getAsInt();
    
                            EmbedBuilder eb = new EmbedBuilder();
                            eb.setColor(Color.pink);
                            eb.setDescription(logPermaLink);
                            eb.setFooter(event.getGuild().getName());
                            eb.addField("BOSS NAME", logBossName, true);
                            eb.addField("IS CM?", logBossIsCm == true ? "\u2705" : "\u274C", true);
                            eb.addField("SUCCESS?", logIsSuccess == true ? "\u2705" : "\u274C", true);
                            eb.addField("TIME TAKEN", logTimeTaken.toString() + " s.", true);
                            eb.addField("COLLECTIVE DPS", logCollectedDps.toString(), true);
    
                            HttpResponse<String> eliteInsightsResponse = DpsReportApi.GET_ELITE_INSIGHTS_RESPONSE(logPermaLink);
                            JsonElement jsonEliteInsightsResponse = JsonParser.parseString(eliteInsightsResponse.getBody().toString());
                            
                            String logTimeStart = jsonEliteInsightsResponse.getAsJsonObject().get("timeStart").getAsString();
                            String logTimeEnd = jsonEliteInsightsResponse.getAsJsonObject().get("timeEnd").getAsString();
                            String arcDpsVersion = jsonEliteInsightsResponse.getAsJsonObject().get("arcVersion").getAsString();
                            String gw2Version = jsonEliteInsightsResponse.getAsJsonObject().get("gW2Build").getAsString();
                            String recordedBy = jsonEliteInsightsResponse.getAsJsonObject().get("recordedBy").getAsString();
                            
                            JsonArray logPlayers = jsonEliteInsightsResponse.getAsJsonObject().get("players").getAsJsonArray();
    
                            ArrayList<String> playerAccountNames = new ArrayList<>();
                            ArrayList<String> playerProfession = new ArrayList<>();
    
                            logPlayers.forEach(player -> {
                                Boolean isCommander = player.getAsJsonObject().get("hasCommanderTag").getAsBoolean();
    
                                if(isCommander) {
                                    playerAccountNames.add(Constants.commanderIconEmoji + " " + player.getAsJsonObject().get("account").getAsString());
                                } else
                                    playerAccountNames.add(player.getAsJsonObject().get("account").getAsString());
    
                                playerProfession.add(player.getAsJsonObject().get("profession").getAsString().toLowerCase());
                            });
    
                            eb.setTitle(recordedBy);
                            eb.addField("GW2 VERSION", gw2Version, true);
                            eb.addField("START TIME", logTimeStart.split(" ")[0] + "\n" + logTimeStart.split(" ")[1], true);
                            eb.addField("END TIME", logTimeEnd.split(" ")[0] + "\n" + logTimeEnd.split(" ")[1], true);
                            eb.addField("ARCDPS VERSION", arcDpsVersion, true);
    
                            eb.addBlankField(false);
    
                            for(int i = 0; i < playerAccountNames.size(); i++) {
                                eb.addField(playerAccountNames.get(i), "→ " + playerProfession.get(i).toUpperCase(), true);
                            }

                            if(playerAccountNames.size() % 3 == 2) {
                                eb.addBlankField(true);
                            }
    
                            eb.setThumbnail(Constants.gw2LogoNoBackground);

                            WebhookEmbedBuilder embedForSending;
                            embedForSending = WebhookEmbedBuilder.fromJDA(eb.build());

                            WebhookMessageBuilder messageBuilder = new WebhookMessageBuilder();
                            messageBuilder.addEmbeds(embedForSending.build());
                            messageBuilder.setAvatarUrl(event.getJDA().getSelfUser().getAvatarUrl());

                            WebhookClientBuilder clientBuilder = null;

                            if(event.getChannel().getId().equals("1007917782601572352")) {
                                messageBuilder.setContent("New log! <@&1007918310190501948>");
                                clientBuilder = new WebhookClientBuilder("https://discord.com/api/webhooks/1008713235031142471/KYSpy3mK6H8iY-ahwk3sLBB6plhpoEOasQmr1MdCrWziRB5aXhrnFcY_0uCGtIe_1ieD");
                            } else {
                                messageBuilder.setContent("New log! " + event.getAuthor().getAsMention());
                                clientBuilder = new WebhookClientBuilder("https://discord.com/api/webhooks/1009064667655712870/CsVfE-xd3gHX-VCbyDQ1Ui9vEoVHKVEXVm7F5i1GWhsIVnUbs11XDjl0KEH07Ilf7WYG");
                            }
                                
                            clientBuilder.build().send(messageBuilder.build());

                        } catch (InterruptedException | ExecutionException | IOException e) {
                            e.printStackTrace();
                        }

                        
                    }
                });
            }
        });

        thread.run();
    }

}
