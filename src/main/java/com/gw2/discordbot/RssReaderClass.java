package com.gw2.discordbot;

import java.io.IOException;
import java.util.stream.Stream;

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
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

public class RssReaderClass {

    public void ReadNewsFromSite() throws InterruptedException {
        DiscordBot.jda.awaitReady();
        new Timer().schedule(new TimerTask() {
            Message latestMessage;
            MessageEmbed messageEmbed;
            Field mainField;
        
            RssReader reader;
            Item newestItem;
        
            SimpleDateFormat sdf;

            String url = "https://www.guildwars2.com/en-gb/feed/";
        
            @Override
            public void run() {
                DiscordBot.jda.getTextChannelById(Constants.newsChannelID).getIterableHistory().takeAsync(1).thenAccept(listOfMessages -> {
                    latestMessage = listOfMessages.get(0);
                    messageEmbed = latestMessage.getEmbeds().get(0);

                    mainField = null;
    
                    for(Field field : messageEmbed.getFields()) {
                        if(field.getName().equals("LINK")) {
                            mainField = field;
                            break;
                        }
                    }

                    reader = new RssReader();
                    ArrayList<Item> rssNewsItems = new ArrayList<>();
            
                    try (Stream<Item> rssFeed = reader.read(url)) {
                        rssFeed.forEach(item -> rssNewsItems.add(item));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            
                    newestItem = rssNewsItems.get(0);

                    if(!newestItem.getLink().get().equals(mainField.getValue())) {

                        List<Item> listOfNewItems = new ArrayList<>();

                        for(Item t : rssNewsItems) {

                            System.out.println("ADDED " + t.getLink().get() + "\n");

                            if(!t.getLink().get().equals(mainField.getValue())) {
                                listOfNewItems.add(t);
                                break;
                            }
                        }

                        for(Item newItem : listOfNewItems) {
                            String updateLink = newItem.getLink().get();
                            String description = "```" + newItem.getDescription().get().split("<")[0] + "```";
                            String time = newItem.getPubDate().get();
        
                            EmbedBuilder eb = new EmbedBuilder();
                            eb.setColor(Color.pink);
                                    
                            eb.setTitle(newItem.getTitle().get());                        
                            eb.setDescription("`News & Announcements`: new announcement!");
                            eb.setThumbnail(Constants.gw2LogoNoBackground);
        
                            eb.addField("LINK", updateLink, false);
                            eb.addField("DESCRIPTION", description, false);
                                    
                            sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.getDefault());
        
                            Date d = null;
                            try {
                                d = sdf.parse(time);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                                    
                            String formattedTime = "<t:" + d.getTime() / 1000 + ":f>";
        
                            eb.addField("PUBLISHED", formattedTime, false);
        
                            DiscordBot.jda.getTextChannelById(Constants.newsChannelID).retrieveWebhooks().queue(availableWebhooks -> {
                                Boolean needToCreateWebhook = true;
    
                                for(Webhook webhookName : availableWebhooks) {
                                    if(webhookName.getName().equals("Guild Wars 2 News")) {
                                        WebhookClientBuilder builder = WebhookClientBuilder.fromJDA(webhookName);
                                
                                        WebhookMessageBuilder messageBuilder = new WebhookMessageBuilder();
                                        messageBuilder.setContent("<@&1010591966502862848>");
                                        messageBuilder.setAvatarUrl(Constants.gw2LogoNoBackground);
                    
                                        WebhookEmbedBuilder embedBuilder = WebhookEmbedBuilder.fromJDA(eb.build());
                                        messageBuilder.addEmbeds(embedBuilder.build());
                    
                                        builder.build().send(messageBuilder.build());
                                        return;
                                    }
                                }
            
                                if(needToCreateWebhook) {
                                    DiscordBot.jda.getTextChannelById(Constants.newsChannelID).createWebhook("Guild Wars 2 News").queue(newWebhook -> {
                                        WebhookClientBuilder builder = WebhookClientBuilder.fromJDA(newWebhook);
                                
                                        WebhookMessageBuilder messageBuilder = new WebhookMessageBuilder();
                                        messageBuilder.setContent("<@&1010591966502862848>");
                                        messageBuilder.setAvatarUrl(Constants.gw2LogoNoBackground);
                    
                                        WebhookEmbedBuilder embedBuilder = WebhookEmbedBuilder.fromJDA(eb.build());
                                        messageBuilder.addEmbeds(embedBuilder.build());
                    
                                        builder.build().send(messageBuilder.build());
                                    });
                                }
                            });
                        }
                    } else {
                        Logging.LOG(this.getClass(), "News are the same. LINK: " + newestItem.getLink().get());
                    }
                });
            }
        }, 0 * 60 * 1000, 2 * 60 * 60 * 1000); // every 2 hours
    }

    public void ReadNewsFromForums() throws InterruptedException {

        DiscordBot.jda.awaitReady();

        new Timer().schedule(new TimerTask() {

            Message latestMessage;
            MessageEmbed messageEmbed;
            Field mainField;
        
            RssReader reader;
            Item newestItem;
        
            SimpleDateFormat sdf;
        
            String url = "https://en-forum.guildwars2.com/forum/6-game-update-notes.xml/";

            @Override
            public void run() {
                DiscordBot.jda.getTextChannelById(Constants.patchNotesChannelID).getIterableHistory().takeAsync(1).thenAccept(listOfMessages -> {

                    latestMessage = listOfMessages.get(0);
                    messageEmbed = latestMessage.getEmbeds().get(0);

                    mainField = null;
    
                    for(Field field : messageEmbed.getFields()) {
                        if(field.getName().equals("LINK")) {
                            mainField = field;
                            break;
                        }
                    }

                    reader = new RssReader();
                    ArrayList<Item> rssForumsItems = new ArrayList<>();
            
                    try (Stream<Item> rssFeed = reader.read(url)) {
                        rssFeed.forEach(item -> rssForumsItems.add(item));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    newestItem = rssForumsItems.get(0);

                    if(!newestItem.getLink().get().equals(mainField.getValue())) {

                        List<Item> listOfNewItems = new ArrayList<>();

                        for(Item t : rssForumsItems) {
                            if(!t.getLink().get().equals(mainField.getValue())) {
                                listOfNewItems.add(t);
                                break;
                            }
                        }

                        for(Item newItem : listOfNewItems) {

                            String updateLink = newItem.getLink().get();
                            String time = newItem.getPubDate().get();
        
                            EmbedBuilder eb = new EmbedBuilder();
                            eb.setColor(Color.pink);
                            
                            eb.setTitle(newItem.getTitle().get());
                            eb.setDescription("`Patch Notes`: new announcement!");
                            eb.setThumbnail(Constants.gw2LogoNoBackground);
        
                            eb.addField("LINK", updateLink, false);
        
                            sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.getDefault());
        
                            Date d = null;
                            try {
                                d = sdf.parse(time);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            
                            String formattedTime = "<t:" + d.getTime() / 1000 + ":f>";
        
                            eb.addField("PUBLISHED", formattedTime, false);
    
                            DiscordBot.jda.getTextChannelById(Constants.patchNotesChannelID).retrieveWebhooks().queue(availableWebhooks -> {

                                Boolean needToCreateWebhook = true;

                                for(Webhook webhookName : availableWebhooks) {
                                    if(webhookName.getName().equals("Guild Wars 2 Patch Notes")) {
                                        WebhookClientBuilder builder = WebhookClientBuilder.fromJDA(webhookName);
                                
                                        WebhookMessageBuilder messageBuilder = new WebhookMessageBuilder();
                                        messageBuilder.setContent("<@&1010592026846314496>");
                                        messageBuilder.setAvatarUrl(Constants.gw2LogoNoBackground);
                    
                                        WebhookEmbedBuilder embedBuilder = WebhookEmbedBuilder.fromJDA(eb.build());
                                        messageBuilder.addEmbeds(embedBuilder.build());
                    
                                        builder.build().send(messageBuilder.build());
                                        return;
                                    }
                                }
            
                                if(needToCreateWebhook) {
                                    DiscordBot.jda.getTextChannelById(Constants.patchNotesChannelID).createWebhook("Guild Wars 2 Patch Notes").queue(newWebhook -> {
                                        WebhookClientBuilder builder = WebhookClientBuilder.fromJDA(newWebhook);
                                        WebhookMessageBuilder messageBuilder = new WebhookMessageBuilder();
                    
                                        WebhookEmbedBuilder embedBuilder = WebhookEmbedBuilder.fromJDA(eb.build());
                                        messageBuilder.addEmbeds(embedBuilder.build());
                                        messageBuilder.setContent("<@&1010592026846314496>");
                                        messageBuilder.setAvatarUrl(Constants.gw2LogoNoBackground);
                    
                                        builder.build().send(messageBuilder.build());
                                    });
                                }
                            }
                        );
                        }
                    } else {
                        Logging.LOG(this.getClass(), "Forums are the same. LINK: " + newestItem.getLink().get());
                    }
                });
            }
        }, 0 * 60 * 1000, 2 * 60 * 60 * 1000); 
    }
}