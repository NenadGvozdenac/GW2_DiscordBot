package com.gw2discordapp;

import java.io.IOException;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apptasticsoftware.rssreader.Item;
import com.apptasticsoftware.rssreader.RssReader;

import java.awt.Color;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.EmbedBuilder;

public class RssReaderClass {
    
    private String url;
    private List<Item> rssNewsItems;
    private List<Item> rssForumsItems;

    private Item oldItem, newItem;
    private String newTitle, oldTitle;

    public RssReaderClass(String url) {
        this.url = url;
        rssNewsItems = new ArrayList<>();
        rssForumsItems = new ArrayList<>();
    }

    public void ReadNewsFromSite() {
        RssReader reader = new RssReader();

        try (Stream<Item> rssFeed = reader.read(url)) {
            rssFeed.forEach(item -> rssNewsItems.add(item));
        } catch (IOException e) {
            e.printStackTrace();
        }

        oldItem = rssNewsItems.get(0);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
               
                oldTitle = oldItem.getTitle().get();
                        
                try (Stream<Item> rssFeed = reader.read(url)) {
                    ArrayList<Item> listOfItems = new ArrayList<>();
                    rssFeed.forEach(item -> listOfItems.add(item));
                    newItem = listOfItems.get(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                newTitle = newItem.getTitle().get();
                
                if(!oldTitle.equals(newTitle)) {
                    String updateLink = newItem.getLink().get();
                    String description = "```" + newItem.getDescription().get().split("<")[0] + "```";
                    String time = newItem.getPubDate().get();

                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setColor(Color.pink);
                            
                    eb.setTitle(newTitle);                        
                    eb.setDescription("`News & Announcements`: new announcement!");
                    eb.setThumbnail(Constants.gw2LogoNoBackground);

                    eb.addField("LINK", updateLink, false);
                    eb.addField("DESCRIPTION", description, false);
                            
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.getDefault());

                    Date d = null;
                    try {
                        d = sdf.parse(time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                            
                    String formattedTime = "<t:" + d.getTime() / 1000 + ":f>";

                    eb.addField("PUBLISHED", formattedTime, false);

                    oldItem = newItem;

                    WebhookClientBuilder builder = new WebhookClientBuilder("https://discord.com/api/webhooks/1009161190636798105/W9OEfvPOxy9oF5G5_QTgI5QxvQUrUQL3G7GD8ibHr_yUDjQttFcf77FKqIjURWUSl4W4");

                    WebhookMessageBuilder messageBuilder = new WebhookMessageBuilder();
                    messageBuilder.setUsername("Guild Wars 2 News");
                    messageBuilder.setAvatarUrl(Main.jda.getSelfUser().getAvatarUrl());
                    messageBuilder.setContent("<@&1010591966502862848>");

                    WebhookEmbedBuilder embedBuilder = WebhookEmbedBuilder.fromJDA(eb.build());
                    messageBuilder.addEmbeds(embedBuilder.build());

                    builder.build().send(messageBuilder.build());

                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy HH:mm", Locale.US);    
                    Date resultdate = new Date(System.currentTimeMillis());

                    Logger logger = LoggerFactory.getLogger(this.getClass());
                    logger.info(sdf.format(resultdate.getTime()) + ": The news are the same currently.");
                }
            }
        }, 10 * 60 * 1000, 10 * 60 * 1000); // every 30 minutes check for new update
    }

    public void ReadNewsFromForums() {
        RssReader reader = new RssReader();

        try (Stream<Item> rssFeed = reader.read(url)) {

            rssFeed.forEach(item -> rssForumsItems.add(item));

        } catch (IOException e) {
            e.printStackTrace();
        }

        oldItem = rssForumsItems.get(0);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                oldTitle = oldItem.getTitle().get();
               
                try (Stream<Item> rssFeed = reader.read(url)) {
                    ArrayList<Item> listOfItems = new ArrayList<>();
                    rssFeed.forEach(item -> listOfItems.add(item));
                    newItem = listOfItems.get(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                newTitle = newItem.getTitle().get();

                if(!oldTitle.equals(newTitle)) {
                    String updateLink = newItem.getLink().get();
                    String time = newItem.getPubDate().get();

                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setColor(Color.pink);
                    
                    eb.setTitle(newTitle);
                    eb.setDescription("`Patch Notes`: new announcement!");
                    eb.setThumbnail(Constants.gw2LogoNoBackground);

                    eb.addField("LINK", updateLink, false);

                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.getDefault());

                    Date d = null;
                    try {
                        d = sdf.parse(time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    
                    String formattedTime = "<t:" + d.getTime() / 1000 + ":f>";

                    eb.addField("PUBLISHED", formattedTime, false);

                    oldItem = newItem;

                    WebhookClientBuilder builder = new WebhookClientBuilder("https://discord.com/api/webhooks/1009416623335149638/EXFg6O2HshHzWRicWkbNdq8FuSdrqD5zTXZim6DTIHsWCSht4AuOVhhmysH7m-VWX3-_");

                    WebhookMessageBuilder messageBuilder = new WebhookMessageBuilder();
                    messageBuilder.setUsername("Guild Wars 2 News");
                    messageBuilder.setAvatarUrl(Main.jda.getSelfUser().getAvatarUrl());

                    WebhookEmbedBuilder embedBuilder = WebhookEmbedBuilder.fromJDA(eb.build());
                    messageBuilder.addEmbeds(embedBuilder.build());
                    messageBuilder.setContent("<@&1010592026846314496>");

                    builder.build().send(messageBuilder.build());
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy HH:mm", Locale.US);    
                    Date resultdate = new Date(System.currentTimeMillis());

                    Logger logger = LoggerFactory.getLogger(this.getClass());
                    logger.info(sdf.format(resultdate.getTime()) + ": The forums are the same currently.");
                }
            }
        }, 10 * 60 * 1000, 10 * 60 * 1000); // every 30 minutes check for new update
    }
}