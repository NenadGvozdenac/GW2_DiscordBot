package com.gw2.discordbot.HttpParsing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.gw2.discordbot.DiscordBot.Constants;
import com.gw2.discordbot.DiscordBot.DiscordBot;
import com.gw2.discordbot.DiscordBot.Logging;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import kong.unirest.HttpResponse;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

public class DailyAchievements {

    public void ReadFractalsFromApi() {

        try {
            DiscordBot.jda.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                DiscordBot.jda.getTextChannelById(Constants.dailyAchievementsChannelID).getIterableHistory().takeAsync(1).thenAccept(listOfMessages -> {
                    Message latestMessage = listOfMessages.get(0);
                    MessageEmbed messageEmbed = latestMessage.getEmbeds().get(0);

                    Field mainField = null;
    
                    for(Field field : messageEmbed.getFields()) {
                        if(field.getName().equals("DAILY FRACTALS")) {
                            mainField = field;
                            break;
                        }
                    }

                    List<String> listOfAchievements = Arrays.asList((mainField.getValue().split("\n")));
                    List<String> trimmedListOFAchievements = new ArrayList<>();
        
                    for(String s : listOfAchievements) {
                        trimmedListOFAchievements.add(s.replace("<:Fractals:1008640406373810237>", ""));
                    }
        
                    HttpResponse<String> newFractals = Gw2Api.GET_REQUEST("v2", "achievements/daily");
                    JsonElement newAchievements = JsonParser.parseString(newFractals.getBody());
        
                    Integer firstFractalId = newAchievements.getAsJsonObject().get("fractals").getAsJsonArray().get(newAchievements.getAsJsonObject().get("fractals").getAsJsonArray().size() - 1).getAsJsonObject().get("id").getAsInt();
        
                    HttpResponse<String> newFractalNameResponse = Gw2Api.GET_REQUEST("v2", "achievements/" + firstFractalId);
                    JsonElement achievementName = JsonParser.parseString((newFractalNameResponse.getBody()));
        
                    String nameOfOneFractal = achievementName.getAsJsonObject().get("name").getAsString();
        
                    if(trimmedListOFAchievements.contains(nameOfOneFractal)) {
                        Logging.LOG(this.getClass(), "msgID: " + latestMessage.getId() + ", " + trimmedListOFAchievements + " contains [" + nameOfOneFractal + "].");
                        return;
                    } else {
                        Logging.LOG(this.getClass(), "msgID: " + latestMessage.getId() + ", " + trimmedListOFAchievements + " does NOT contain [" + nameOfOneFractal + "].");
                        DiscordBot.jda.getTextChannelById(Constants.dailyAchievementsChannelID).retrieveWebhooks().queue(availableWebhooks -> {

                            Boolean needToCreateWebhook = true;
                            Webhook webhook = null;

                            for(Webhook webhookName : availableWebhooks) {
                                if(webhookName.getName().equals("Guild Wars 2 Daily Achievements")) {
                                    needToCreateWebhook = false;
                                    webhook = webhookName;
                                    break;
                                }
                            }
    
                            if(needToCreateWebhook) {
                                webhook = DiscordBot.jda.getTextChannelById(Constants.dailyAchievementsChannelID).createWebhook("Guild Wars 2 Daily Achievements").complete();
                            }
    
                            WebhookClientBuilder builder = WebhookClientBuilder.fromJDA(webhook);
                            WebhookMessageBuilder messageBuilder = new WebhookMessageBuilder();

                            Gw2Dailies dailies = new Gw2Dailies();
                            CompletableFuture<MessageEmbed> embedForSending = dailies.getDailies();
                            dailies = null;
    
                            WebhookEmbedBuilder embedBuilder;
                            try {
                                embedBuilder = WebhookEmbedBuilder.fromJDA(embedForSending.get());

                                messageBuilder.setContent("<@&1010592048753160252>");
                                messageBuilder.addEmbeds(embedBuilder.build());
                                messageBuilder.setAvatarUrl(Constants.gw2LogoNoBackground);
        
                                builder.build().send(messageBuilder.build());
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }).exceptionally(throwable -> {
                    System.out.print("ERROR: " + throwable.getMessage());
                    return null;
                });                       
            }
        }, 0 * 60 * 1000, 60 * 60 * 1000);  // every 60 minutes
    }
}
