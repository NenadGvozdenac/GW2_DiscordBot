package com.gw2.discordbot;

import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Webhook;

@SuppressWarnings("null")
public class HttpServerHosting {

    static long currentMilis;
    
    public static HttpServer server;

    public static Boolean activateServer() {
        try {
            server = HttpServer.create(new InetSocketAddress(25639), 0);
            // server.createContext("/staticFileUpload", new StaticFileHandler());
            server.createContext("/personalFileUpload", new PersonalFileHandler(new RaidDay()));
            server.createContext("/stopFileUpload", new StopFileUpload("1027132780825559090"));

            server.setExecutor(null); 
            server.start();	

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");  
            LocalDateTime now = LocalDateTime.now();  

            String currentTime = dtf.format(now);

            currentMilis = System.currentTimeMillis();

            // DiscordBot.jda.getTextChannelById("1007917782601572352").sendMessage("DAY: `" + currentTime + "`!").queue();

            System.out.println("Started the server!");

            return true;
        } catch (IOException e) {
            System.out.println("Address already in use!");
            return false;
        } 
    }

    public static void stopServer() {
        server.stop(0);
        System.out.println("Stopped the server!");
    }

    static class StopFileUpload implements HttpHandler {

        String id;

        StopFileUpload(String id) {
            this.id = id;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            
            System.out.println("Handling " + exchange.getRequestURI());

            String response = "Stopped listening!";					
            exchange.sendResponseHeaders(200, response.length());							
            OutputStream os = exchange.getResponseBody();								
            os.write(response.getBytes());											
            os.close();		

            try(FileReader reader = new FileReader(new File("./weeklyStaticLogging.json"))) {
                Gson gson = new GsonBuilder()
                            .disableHtmlEscaping()
                            .setPrettyPrinting()
                            .serializeNulls()
                            .create();
    
                Type founderTypeSet = new TypeToken<RaidDay>(){}.getType();
                RaidDay raidDay = gson.fromJson(reader, founderTypeSet);
    
                EmbedBuilder eb = new EmbedBuilder();
                eb.setColor(Color.CYAN);
                eb.setTitle(raidDay.date);
    
                List<Boss> listOfBosses = raidDay.bosses;
    
                for(Boss boss : listOfBosses) {
                    if(boss.bossName.equals("Twisted Castle"))  continue;

                    eb.addField(boss.bossName, ":wrench: [Report](" + boss.dpsReportLink + ")\n" + ":clock1: " + (boss.killTime.contains("00m") ? boss.killTime.substring(4) : boss.killTime), false);
                }
    
                eb.setFooter("Cleared in: " + TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - currentMilis) + " minute(s).");
                
                currentMilis = 0;

                DiscordBot.jda.getTextChannelById(id).sendMessageEmbeds(eb.build()).queue();

                stopServer();

            } catch(IOException e) {
    
            }
        }
    }

    static class PersonalFileHandler implements HttpHandler {

        public RaidDay raidDay;

        public PersonalFileHandler(RaidDay day) throws IOException {
            this.raidDay = day;

            try(FileWriter writer = new FileWriter(new File("./weeklyStaticLogging.json"))) {

                Gson gson = new GsonBuilder()
                        .disableHtmlEscaping()
                        .setPrettyPrinting()
                        .serializeNulls()
                        .create();

                String writing = gson.toJson(raidDay);

                System.out.println(writing);
                
                writer.write(writing);

                writer.close();
            }
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            
            System.out.println("Handling " + exchange.getRequestURI());

            if(exchange.getRequestHeaders().getFirst("bosslog") != null) { 

                String response = exchange.getRequestHeaders().getFirst("bosslog") + " was uploaded!";					
                exchange.sendResponseHeaders(200, response.length());							
                OutputStream os = exchange.getResponseBody();								
                os.write(response.getBytes());											
                os.close();																

                String bossLogPermaLink = exchange.getRequestHeaders().getFirst("bosslog");
                String bossLogTime = exchange.getRequestHeaders().getFirst("bosstime");
                Boolean bossLogSuccess = exchange.getRequestHeaders().getFirst("bosssuccess").equals("true") ? false : true;
                String bossLogName = exchange.getRequestHeaders().getFirst("bossname");
                String bossIsCm = exchange.getRequestHeaders().getFirst("bosscm");
                String bossIcon = exchange.getRequestHeaders().getFirst("bossicon");

                Boss currentBoss = new Boss(bossLogPermaLink, bossLogName, bossIcon, bossIsCm, bossLogSuccess, bossLogTime);

                try(FileReader reader = new FileReader(new File("./weeklyStaticLogging.json"))) {
                    Gson gson = new GsonBuilder()
                        .disableHtmlEscaping()
                        .setPrettyPrinting()
                        .serializeNulls()
                        .create();

                    Type founderTypeSet = new TypeToken<RaidDay>(){}.getType();
                    RaidDay raidDay = gson.fromJson(reader, founderTypeSet);

                    raidDay.bosses.add(currentBoss);

                    String writing = gson.toJson(raidDay);

                    FileWriter writer = new FileWriter(new File("./weeklyStaticLogging.json"));

                    System.out.println(writing);

                    writer.write(writing);

                    reader.close();
                    writer.close();
                }
            }
        }
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("Handling " + exchange.getRequestURI());

            if(exchange.getRequestHeaders().getFirst("bosslog") != null) {
                String response = exchange.getRequestHeaders().getFirst("bosslog") + " was uploaded!";					
                exchange.sendResponseHeaders(200, response.length());							
                OutputStream os = exchange.getResponseBody();								
                os.write(response.getBytes());											
                os.close();																

                String bossLogPermaLink = exchange.getRequestHeaders().getFirst("bosslog");
                String bossLogTime = exchange.getRequestHeaders().getFirst("bosstime");
                String bossLogSuccess = exchange.getRequestHeaders().getFirst("bosssuccess");
                String bossLogName = exchange.getRequestHeaders().getFirst("bossname");
                String bossIsCm = exchange.getRequestHeaders().getFirst("bosscm");

                Boolean needToCreateWebhook = true;
                Webhook webhook = null;

                List<Webhook> availableWebhooks = DiscordBot.jda.getTextChannelById(Constants.staticLogUploadsChannelID).retrieveWebhooks().complete();

                for(Webhook webhookName : availableWebhooks) {
                    if(webhookName.getName().equals("Guild Wars 2 Autouploader")) {
                        needToCreateWebhook = false;
                        webhook = webhookName;
                        break;
                    }
                }

                if(needToCreateWebhook) {
                    webhook = DiscordBot.jda.getTextChannelById(Constants.staticLogUploadsChannelID).createWebhook("Guild Wars 2 Autouploader").complete();
                }

                WebhookClientBuilder builder = WebhookClientBuilder.fromJDA(webhook);
                
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle(bossLogName);

                String bossDayOfKill = LocalDateTime.now().atZone(ZoneId.of("America/New_York")).format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(ZoneOffset.ofHours(-2)));

                eb.setDescription(String.format("%s\n```%-9s %s\n%-9s %s\n%-9s %s\n%-9s %s (CEST)```", bossLogPermaLink, "DURATION:", bossLogTime, "SUCCESS:", bossLogSuccess, "CM:", bossIsCm, "TIME:", bossDayOfKill));
                eb.setColor(bossLogSuccess.equals("true") ? Color.GREEN : Color.RED);

                WebhookMessageBuilder messageBuilder = new WebhookMessageBuilder();

                WebhookEmbedBuilder embedBuilder = WebhookEmbedBuilder.fromJDA(eb.build());
                messageBuilder.addEmbeds(embedBuilder.build());
                messageBuilder.setAvatarUrl(Constants.gw2LogoNoBackground);

                builder.build().send(messageBuilder.build());
            } else {
                String response = "DIDN't manage to upload the log!";					
                exchange.sendResponseHeaders(500, response.length());							
                OutputStream os = exchange.getResponseBody();								
                os.write(response.getBytes());											
                os.close();																
            }
        }
    }
}
