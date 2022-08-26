package com.gw2.discordbot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

public class DailyAchievements {

    public void ReadFractalsFromApi() {

        try {
            Main.jda.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                
                    Main.jda.getTextChannelById(Constants.dailyAchievementsChannelID).getIterableHistory().takeAsync(1).thenAcceptAsync(listOfMessages -> {
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
                    } else {
                        Logging.LOG(this.getClass(), "msgID: " + latestMessage.getId() + ", " + trimmedListOFAchievements + " does NOT contain [" + nameOfOneFractal + "].");

                        Boolean needToCreateWebhook = true;
                        Webhook webhook = null;

                        List<Webhook> availableWebhooks = Main.jda.getTextChannelById(Constants.dailyAchievementsChannelID).retrieveWebhooks().complete();

                        for(Webhook webhookName : availableWebhooks) {
                            if(webhookName.getName().equals("Guild Wars 2 Daily Achievements")) {
                                needToCreateWebhook = false;
                                webhook = webhookName;
                                break;
                            }
                        }

                        if(needToCreateWebhook) {
                            webhook = Main.jda.getTextChannelById(Constants.dailyAchievementsChannelID).createWebhook("Guild Wars 2 Daily Achievements").complete();
                        }

                        WebhookClientBuilder builder = WebhookClientBuilder.fromJDA(webhook);
                        WebhookMessageBuilder messageBuilder = new WebhookMessageBuilder();

                        MessageEmbed embedForSending = Gw2Dailies.getDailies();

                        WebhookEmbedBuilder embedBuilder = WebhookEmbedBuilder.fromJDA(embedForSending);
                        messageBuilder.setContent("<@&1010592048753160252>");
                        messageBuilder.addEmbeds(embedBuilder.build());
                        messageBuilder.setAvatarUrl(Constants.gw2LogoNoBackground);

                        builder.build().send(messageBuilder.build());
                    }
                });
            }
        }, 0 * 60 * 1000, 30 * 60 * 1000);
    }
}
