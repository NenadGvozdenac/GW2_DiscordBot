package com.gw2.discordbot;

import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
    
    public static HttpServer server;

    public static Boolean activateServer() {
        try {
            server = HttpServer.create(new InetSocketAddress(25639), 0);
            // server.createContext("/staticFileUpload", new StaticFileHandler());
            server.createContext("/personalFileUpload", new PersonalFileHandler(new RaidDay()));
            server.createContext("/stopFileUpload", new StopFileUpload());

            server.setExecutor(null); 
            server.start();	

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

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            
            System.out.println("Handling " + exchange.getRequestURI());

            String response = "Stopped listening!";					
            exchange.sendResponseHeaders(200, response.length());							
            OutputStream os = exchange.getResponseBody();								
            os.write(response.getBytes());											
            os.close();		

            String channelForSending = exchange.getRequestHeaders().getFirst("channelid");

            System.out.println(channelForSending + " is the id of the channel.");

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
    
                List<Boss> listOfBosses = raidDay.bosses;
    
                List<List<Boss>> Wings = new ArrayList<>() {
                    {
                        add(new ArrayList<>());
                    }
                };

                for(int i = 0, j = 0; i < listOfBosses.size(); i++) {
                    if(i == listOfBosses.size() - 1) {
                        Wings.get(j).add(listOfBosses.get(i));
                    } else if(listOfBosses.get(i).wingName.equals(listOfBosses.get(i+1).wingName)) {
                        Wings.get(j).add(listOfBosses.get(i));
                    } else {
                        Wings.get(j).add(listOfBosses.get(i));
                        Wings.add(new ArrayList<>());
                        j++;
                    }
                }

                List<Boss> failedBosses = new ArrayList<>();

                listOfBosses.forEach(boss -> {
                    if(boss.isFailed) {
                        failedBosses.add(boss);
                    }
                });

                for(List<Boss> wing : Wings) {

                    String string = "";

                    boolean toAddField = false;

                    String title = wing.get(0).wingName;

                    for(Boss boss : wing) {
                        if(boss.isFailed) continue;
                        toAddField = true;
                        string += (boss.emoji.equals("null") ? "" : boss.emoji + " ") + "[" + boss.bossName + "](" + boss.dpsReportLink + ") " + (boss.killTime.contains("00m ") ? boss.killTime.substring(4, 7) : boss.killTime.substring(0, 7)) + "\n";
                    }

                    if(toAddField)
                        eb.addField(title, string, false);
                }
    
                Boss firstBoss = listOfBosses.get(0);
                Boss lastBoss = listOfBosses.get(listOfBosses.size() - 1);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss X", Locale.getDefault());

                long startMilis = sdf.parse(firstBoss.startTime).getTime();
                long endMilis = sdf.parse(lastBoss.endTime).getTime();

                long minutes = TimeUnit.MILLISECONDS.toMinutes(endMilis - startMilis);

                eb.setTitle((minutes > 60 ? (minutes / 60) + " hours, " + (minutes % 60) + " minutes clear." : minutes + " minutes clear."));

                DiscordBot.jda.getTextChannelById(channelForSending).sendMessageEmbeds(eb.build()).queue();

                if(failedBosses.size() != 0) {
                    EmbedBuilder eb1 = new EmbedBuilder();

                    eb1.setColor(Color.CYAN);
    
                    String failure = "";
    
                    long timeWiping = 0;
                    
                    DateTimeFormatter f = DateTimeFormatter.ofPattern("HH'h' mm'm' ss's' SSS'ms'");

                    for(Boss boss : failedBosses) {
                        failure += (boss.emoji.equals("null") ? "" : boss.emoji + " ") + "[" + boss.bossName + "](" + boss.dpsReportLink + ") " + (boss.killTime.contains("00m ") ? boss.killTime.substring(4, 7) : boss.killTime.substring(0, 7)) + "\n";
                    
                        String time = "00h " + boss.killTime;
                        long bossWipeTime = LocalTime.parse(time, f).toSecondOfDay();

                        timeWiping += bossWipeTime;
                    }

                    long minutes1 = timeWiping / 60;

                    timeWiping %= 60;

                    long seconds1 = timeWiping;

                    eb1.setTitle(failedBosses.size() + " fails. Time taken: " + minutes1 + " minutes, " + seconds1 + " seconds.");
    
                    eb1.setDescription(failure);
    
                    DiscordBot.jda.getTextChannelById(channelForSending).sendMessageEmbeds(eb1.build()).queue();
                }

                stopServer();

            } catch(IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
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
                String bossStartTime = exchange.getRequestHeaders().getFirst("bossstart");
                String bossEndTime = exchange.getRequestHeaders().getFirst("bossend");

                Boss currentBoss = new Boss(bossLogPermaLink, bossLogName, bossIsCm, bossLogSuccess, bossLogTime, bossStartTime, bossEndTime);

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
