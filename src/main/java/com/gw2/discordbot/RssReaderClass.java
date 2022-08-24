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
    
    private String url;
    private List<Item> rssNewsItems;
    private List<Item> rssForumsItems;
    private RssReader reader;

    public RssReaderClass(String url) {
        reader = new RssReader();
        this.url = url;
        rssNewsItems = new ArrayList<>();
        rssForumsItems = new ArrayList<>();
    }

    public void ReadNewsFromSite() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                try {
                    Main.jda.awaitReady();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Boolean isNewsAlreadySent = getIsNewsAlreadySent(Constants.newsChannelID);

                if(!isNewsAlreadySent) {
                    Item newItem = null;

                    try (Stream<Item> rssFeed = reader.read(url)) {
                        ArrayList<Item> listOfItems = new ArrayList<>();
                        rssFeed.forEach(item -> listOfItems.add(item));
                        newItem = listOfItems.get(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

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
                            
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.getDefault());

                    Date d = null;
                    try {
                        d = sdf.parse(time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                            
                    String formattedTime = "<t:" + d.getTime() / 1000 + ":f>";

                    eb.addField("PUBLISHED", formattedTime, false);

                    Boolean needToCreateWebhook = true;
                    Webhook webhook = null;
                    List<Webhook> availableWebhooks = Main.jda.getTextChannelById(Constants.newsChannelID).retrieveWebhooks().complete();

                    for(Webhook webhookName : availableWebhooks) {
                        if(webhookName.getName().equals("Guild Wars 2 News")) {
                            needToCreateWebhook = false;
                            webhook = webhookName;
                            break;
                        }
                    }

                    if(needToCreateWebhook) {
                        webhook = Main.jda.getTextChannelById(Constants.newsChannelID).createWebhook("Guild Wars 2 News").complete();
                    }

                    WebhookClientBuilder builder = WebhookClientBuilder.fromJDA(webhook);
                    
                    WebhookMessageBuilder messageBuilder = new WebhookMessageBuilder();
                    messageBuilder.setContent("<@&1010591966502862848>");
                    messageBuilder.setAvatarUrl(Constants.gw2LogoNoBackground);

                    WebhookEmbedBuilder embedBuilder = WebhookEmbedBuilder.fromJDA(eb.build());
                    messageBuilder.addEmbeds(embedBuilder.build());

                    builder.build().send(messageBuilder.build());
                } 
            }
        }, 0 * 60 * 1000, 2 * 60 * 1000); // every 15 minutes check for new update
    }

    public void ReadNewsFromForums() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                try {
                    Main.jda.awaitReady();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Boolean isForumsAlreadySent = getIsForumsAlreadySent(Constants.patchNotesChannelID);

                if(!isForumsAlreadySent) {

                    Item newItem = null;

                    try (Stream<Item> rssFeed = reader.read(url)) {
                        ArrayList<Item> listOfItems = new ArrayList<>();
                        rssFeed.forEach(item -> listOfItems.add(item));
                        newItem = listOfItems.get(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String updateLink = newItem.getLink().get();
                    String time = newItem.getPubDate().get();

                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setColor(Color.pink);
                    
                    eb.setTitle(newItem.getTitle().get());
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

                    Boolean needToCreateWebhook = true;
                    Webhook webhook = null;

                    List<Webhook> availableWebhooks = Main.jda.getTextChannelById(Constants.patchNotesChannelID).retrieveWebhooks().complete();

                    for(Webhook webhookName : availableWebhooks) {
                        if(webhookName.getName().equals("Guild Wars 2 Patch Notes")) {
                            needToCreateWebhook = false;
                            webhook = webhookName;
                            break;
                        }
                    }

                    if(needToCreateWebhook) {
                        webhook = Main.jda.getTextChannelById(Constants.patchNotesChannelID).createWebhook("Guild Wars 2 Patch Notes").complete();
                    }

                    WebhookClientBuilder builder = WebhookClientBuilder.fromJDA(webhook);
                    WebhookMessageBuilder messageBuilder = new WebhookMessageBuilder();

                    WebhookEmbedBuilder embedBuilder = WebhookEmbedBuilder.fromJDA(eb.build());
                    messageBuilder.addEmbeds(embedBuilder.build());
                    messageBuilder.setContent("<@&1010592026846314496>");
                    messageBuilder.setAvatarUrl(Constants.gw2LogoNoBackground);

                    builder.build().send(messageBuilder.build());
                } 
            }
        }, 0 * 60 * 1000, 2 * 60 * 1000); // every 2 minutes check for new update
    }

    protected Boolean getIsNewsAlreadySent(String newsChannelID) {
        RssReader reader = new RssReader();
        rssNewsItems = new ArrayList<>();

        try (Stream<Item> rssFeed = reader.read(url)) {
            rssFeed.forEach(item -> rssNewsItems.add(item));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Item newestItem = rssNewsItems.get(0);

        Message latestMessage = Main.jda.getTextChannelById(newsChannelID).getHistory().retrievePast(1).complete().get(0);
        MessageEmbed messageEmbed = latestMessage.getEmbeds().get(0);

        Field mainField = null;

        if(messageEmbed == null) {
            return false;
        }

        for(Field field : messageEmbed.getFields()) {
            if(field.getName().equals("LINK")) {
                mainField = field;
                break;
            }
        }

        if(newestItem.getLink().get().equals(mainField.getValue())) {
            return true;
        } else {
            return false;
        }
    }

    protected Boolean getIsForumsAlreadySent(String patchNotesChannelID) {
        RssReader reader = new RssReader();
        rssForumsItems = new ArrayList<>();

        try (Stream<Item> rssFeed = reader.read(url)) {
            rssFeed.forEach(item -> rssForumsItems.add(item));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Item newestItem = rssForumsItems.get(0);

        Message latestMessage = Main.jda.getTextChannelById(patchNotesChannelID).getHistory().retrievePast(1).complete().get(0);
        MessageEmbed messageEmbed = latestMessage.getEmbeds().get(0);

        Field mainField = null;

        if(messageEmbed == null) {
            return false;
        }

        for(Field field : messageEmbed.getFields()) {
            if(field.getName().equals("LINK")) {
                mainField = field;
                break;
            }
        }

        if(newestItem.getLink().get().equals(mainField.getValue())) {
            return true;
        } else {
            return false;
        }
    }
}