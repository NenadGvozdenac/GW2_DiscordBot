package com.gw2discordapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class DailyAchievements {
    
    private JsonElement oldAchievements, newAchievements;

    public void ReadFractalsFromApi() {

        HttpResponse<String> oldFractals = Gw2Api.GET_REQUEST("v2", "achievements/daily");
        oldAchievements = JsonParser.parseString(oldFractals.getBody());

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                HttpResponse<String> newFractals = Gw2Api.GET_REQUEST("v2", "achievements/daily");
                newAchievements = JsonParser.parseString(newFractals.getBody());

                Integer oldFirstFractalId = oldAchievements.getAsJsonObject().get("fractals").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsInt();
                Integer newFirstFractalId = newAchievements.getAsJsonObject().get("fractals").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsInt();
                
                if(!oldFirstFractalId.toString().equals(newFirstFractalId.toString())) {
                    oldAchievements = newAchievements;
                    MessageEmbed embedForSending = Gw2Dailies.getDailies();

                    WebhookClientBuilder builder = new WebhookClientBuilder("https://discord.com/api/webhooks/1009751874624303155/cl1w6PYboey4b-fdzOq7vcqTQGKX25tR7TbyiPo3nM9_dQztJ12X0_APtu4WMoCSjf60");

                    WebhookMessageBuilder messageBuilder = new WebhookMessageBuilder();
                    messageBuilder.setUsername("Guild Wars 2 Daily Achievements");
                    messageBuilder.setAvatarUrl(Main.jda.getSelfUser().getAvatarUrl());

                    WebhookEmbedBuilder embedBuilder = WebhookEmbedBuilder.fromJDA(embedForSending);
                    messageBuilder.setContent("<@&1010592048753160252>");
                    messageBuilder.addEmbeds(embedBuilder.build());

                    builder.build().send(messageBuilder.build());
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy HH:mm", Locale.US);    
                    Date resultdate = new Date(System.currentTimeMillis());

                    Logger logger = LoggerFactory.getLogger(this.getClass());
                    logger.info(sdf.format(resultdate.getTime()) + ": The fractals are the same currently.");
                }
            }
        }, 10 * 60 * 1000, 10 * 60 * 1000);
    }
}
