package com.gw2.discordbot;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed.EmbedTitle;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Webhook;

public class HttpServerHosting {
    
    public static HttpServer server;

    public static Boolean activateServer() {
        try {
            server = HttpServer.create(new InetSocketAddress(25639), 0);
            server.createContext("/staticFileUpload", new FileHandler());

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

    static class FileHandler implements HttpHandler {
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

                List<Webhook> availableWebhooks = Main.jda.getTextChannelById(Constants.staticLogUploadsChannelID).retrieveWebhooks().complete();

                for(Webhook webhookName : availableWebhooks) {
                    if(webhookName.getName().equals("Guild Wars 2 Autouploader")) {
                        needToCreateWebhook = false;
                        webhook = webhookName;
                        break;
                    }
                }

                if(needToCreateWebhook) {
                    webhook = Main.jda.getTextChannelById(Constants.staticLogUploadsChannelID).createWebhook("Guild Wars 2 Autouploader").complete();
                }

                WebhookClientBuilder builder = WebhookClientBuilder.fromJDA(webhook);
                
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle(bossLogName);
                eb.setDescription(String.format("%s\n```TIME: %-10s\nSUCCESS: %-10s\nCM: %-10s```", bossLogPermaLink, bossLogTime, bossLogSuccess, bossIsCm));
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
