package com.gw2.discordbot;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import kong.unirest.HttpResponse;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class Gw2Dailies {
    
    static String wvwAchievementsString = "";
    static String pvpAchievementsString = "";
    static String pveAchievementsString = "";    
    static String tierFourFractalsString = "";
    static String recommendedFractalsString = ""; 
    static String strikesAchievementsString = "";
    static String eodAchievementsString = "";

    public static MessageEmbed getDailies() {
        
            HttpResponse<String> responseInfo = Gw2Api.GET_REQUEST("v2", "achievements/daily");
            JsonElement jsonInfo = JsonParser.parseString(responseInfo.getBody().toString());
    
            JsonArray arrayOfDailyPVEAchievements = jsonInfo.getAsJsonObject().get("pve").getAsJsonArray();
            JsonArray arrayOfDailyPVPAchievements = jsonInfo.getAsJsonObject().get("pvp").getAsJsonArray();
            JsonArray arrayOfDailyWVWAchievements = jsonInfo.getAsJsonObject().get("wvw").getAsJsonArray();

            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.cyan);
            eb.setTitle("DAILY ACHIEVEMENTS");
            eb.setThumbnail(Constants.gw2LogoNoBackground);
            eb.setDescription("<t:" + System.currentTimeMillis() / 1000 + ":d>");

            eb.setFooter("Thank you for using " + Main.jda.getSelfUser().getName() + "!", Constants.gw2LogoNoBackground);

            ExecutorService es = Executors.newCachedThreadPool();
                
            // FRACTALS
            es.execute(new Runnable() {
                public void run() {

                    Gw2Dailies.tierFourFractalsString = "";
                    Gw2Dailies.recommendedFractalsString = "";

                    JsonArray arrayOfDailyFractalAchievements = jsonInfo.getAsJsonObject().get("fractals").getAsJsonArray();
                    List<JsonElement> tierFourFractals = new ArrayList<>();
                    List<JsonElement> recommendedFractals = new ArrayList<>();
                    
                    for(int i = 0, j = 1; i < 3; i++, j++) {
                        recommendedFractals.add(arrayOfDailyFractalAchievements.get(i));
                        tierFourFractals.add(arrayOfDailyFractalAchievements.get(arrayOfDailyFractalAchievements.size() - 4 * j + 3));
                    }

                    List<Integer> tierFourFractalsIds = new ArrayList<>();
                    List<Integer> recommendedFractalsIds = new ArrayList<>();
                    
                    for(int i = 0; i < tierFourFractals.size(); i++) {
                        tierFourFractalsIds.add(tierFourFractals.get(i).getAsJsonObject().get("id").getAsInt());
                        recommendedFractalsIds.add(recommendedFractals.get(i).getAsJsonObject().get("id").getAsInt());
                    }

                    List<String> tierFourFractalsNames = new ArrayList<>();
                    List<String> recommendedFractalsNames = new ArrayList<>();
                    
                    for(Integer integer : tierFourFractalsIds) {
                        HttpResponse<String> responseFractalInfoById = Gw2Api.GET_REQUEST("v2", "achievements/" + integer);
                        JsonElement jsonFractalInfoById = JsonParser.parseString(responseFractalInfoById.getBody().toString());
            
                        String fractalName = jsonFractalInfoById.getAsJsonObject().get("name").getAsString();
                        tierFourFractalsNames.add(fractalName);
                    }
        
                    for(Integer integer : recommendedFractalsIds) {
                        HttpResponse<String> responseFractalInfoById = Gw2Api.GET_REQUEST("v2", "achievements/" + integer);
                        JsonElement jsonFractalInfoById = JsonParser.parseString(responseFractalInfoById.getBody().toString());
            
                        String fractalName = jsonFractalInfoById.getAsJsonObject().get("name").getAsString();
                        recommendedFractalsNames.add(fractalName);
                    }

                    for(String fractalName : tierFourFractalsNames) {
                        tierFourFractalsString += Constants.fractalIconEmoji + fractalName + "\n";
                    }
        
                    for(String fractalName : recommendedFractalsNames) {
                        recommendedFractalsString += Constants.fractalIconEmoji + fractalName + "\n";
                    }    
                }
            });

            // PVE
            es.execute(new Runnable() {
                public void run() {

                    Gw2Dailies.pveAchievementsString = "";

                    List<JsonElement> pveAchievements = new ArrayList<>();
            
                    for(int i = 0; i < arrayOfDailyPVEAchievements.size(); i++) {
                        pveAchievements.add(arrayOfDailyPVEAchievements.get(i));
                    }

                    List<Integer> pveAchievementsIds = new ArrayList<>();
                    
                    pveAchievements.forEach(pveAchievement -> {
                        pveAchievementsIds.add(pveAchievement.getAsJsonObject().get("id").getAsInt());
                    });
        
                    List<String> pveAchievementsNames = new ArrayList<>();
            
                    for(Integer integer : pveAchievementsIds) {
                        HttpResponse<String> responsePVEAchievementInfoById = Gw2Api.GET_REQUEST("v2", "achievements/" + integer);
                        JsonElement jsonPVEAchievementInfo = JsonParser.parseString(responsePVEAchievementInfoById.getBody().toString());
        
                        String achievementName = jsonPVEAchievementInfo.getAsJsonObject().get("name").getAsString();
                        pveAchievementsNames.add(achievementName);
                    }

                    for(String achievementName : pveAchievementsNames) {
                        pveAchievementsString += Constants.fractalIconEmoji + achievementName + "\n";
                    }
                }
            });

            // PVP
            es.execute(new Runnable() {
                public void run() {

                    Gw2Dailies.pvpAchievementsString = "";
                    
                    List<JsonElement> pvpAchievements = new ArrayList<>();
            
                    for(int i = 0; i < arrayOfDailyPVPAchievements.size(); i++) {
                        pvpAchievements.add(arrayOfDailyPVPAchievements.get(i));
                    }

                    List<Integer> pvpAchievementsIds = new ArrayList<>();

                    pvpAchievements.forEach(pvpAchievement -> {
                        pvpAchievementsIds.add(pvpAchievement.getAsJsonObject().get("id").getAsInt());
                    });

                    List<String> pvpAchievementsNames = new ArrayList<>();
            
                    for(Integer integer : pvpAchievementsIds) {
                        HttpResponse<String> responsePVPAchievementInfoById = Gw2Api.GET_REQUEST("v2", "achievements/" + integer);
                        JsonElement jsonPVPAchievementInfo = JsonParser.parseString(responsePVPAchievementInfoById.getBody().toString());
        
                        String achievementName = jsonPVPAchievementInfo.getAsJsonObject().get("name").getAsString();
                        pvpAchievementsNames.add(achievementName);
                    }
                    
                
                    for(String achievementName : pvpAchievementsNames) {
                        pvpAchievementsString += Constants.fractalIconEmoji + achievementName + "\n";
                    }    
                }
            });

            // WVW
            es.execute(new Runnable() {
                public void run() {

                    Gw2Dailies.wvwAchievementsString = "";

                    List<JsonElement> wvwAchievements = new ArrayList<>();

                    for(int i = 0; i < arrayOfDailyWVWAchievements.size(); i++) {
                        wvwAchievements.add(arrayOfDailyWVWAchievements.get(i));
                    }
        
                    List<Integer> wvwAchievementsIds = new ArrayList<>();
        
                    wvwAchievements.forEach(wvwAchievement -> {
                        wvwAchievementsIds.add(wvwAchievement.getAsJsonObject().get("id").getAsInt());
                    });
        
                    List<String> wvwAchievementsNames = new ArrayList<>();
        
                    for(Integer integer : wvwAchievementsIds) {
                        HttpResponse<String> responseWVWAchievementInfoById = Gw2Api.GET_REQUEST("v2", "achievements/" + integer);
                        JsonElement jsonWVWAchievementInfo = JsonParser.parseString(responseWVWAchievementInfoById.getBody().toString());
        
                        String achievementName = jsonWVWAchievementInfo.getAsJsonObject().get("name").getAsString();
                        wvwAchievementsNames.add(achievementName);
                    }
        
                    for(String achievementName : wvwAchievementsNames) {
                        wvwAchievementsString += Constants.fractalIconEmoji + achievementName + "\n";
                    }
                }
            });

            // Strikes
            es.execute(new Runnable() {

                @Override
                public void run() {

                    Gw2Dailies.strikesAchievementsString = "";

                    HttpResponse<String> responseStrikeInfo = Gw2Api.GET_REQUEST("v2", "achievements/categories/250");
                    JsonElement dailyStrikes = JsonParser.parseString(responseStrikeInfo.getBody());

                    JsonArray listOfAchievements = dailyStrikes.getAsJsonObject().get("achievements").getAsJsonArray();

                    List<Integer> primaryStrikes = new ArrayList<>();
                    List<String> primaryStrikeNames = new ArrayList<>();

                    listOfAchievements.forEach(achievement -> primaryStrikes.add(achievement.getAsInt()));

                    for(Integer integer : primaryStrikes) {
                        HttpResponse<String> responsePrimaryStrikeInfo = Gw2Api.GET_REQUEST("v2", "achievements/" + integer);
                        JsonElement achievementInfo = JsonParser.parseString(responsePrimaryStrikeInfo.getBody());

                        String achievementName = achievementInfo.getAsJsonObject().get("name").getAsString();

                        primaryStrikeNames.add(achievementName);
                    }

                    for(String string : primaryStrikeNames) {
                        strikesAchievementsString += Constants.fractalIconEmoji + string + "\n";
                    }
                }
            });

            // EOD
            es.execute(new Runnable() {
               
                @Override
                public void run() {

                    Gw2Dailies.eodAchievementsString = "";

                    HttpResponse<String> responseEODeInfo = Gw2Api.GET_REQUEST("v2", "achievements/categories/321");
                    JsonElement dailyEOD = JsonParser.parseString(responseEODeInfo.getBody());

                    JsonArray listOfAchievements = dailyEOD.getAsJsonObject().get("achievements").getAsJsonArray();

                    List<Integer> dailyEodIds = new ArrayList<>();
                    List<String> dailyEodNames = new ArrayList<>();

                    listOfAchievements.forEach(achievement -> dailyEodIds.add(achievement.getAsInt()));

                    for(Integer integer : dailyEodIds) {
                        HttpResponse<String> responseEodInfo = Gw2Api.GET_REQUEST("v2", "achievements/" + integer);
                        JsonElement achievementInfo = JsonParser.parseString(responseEodInfo.getBody());

                        String achievementName = achievementInfo.getAsJsonObject().get("name").getAsString();

                        dailyEodNames.add(achievementName);
                    }

                    for(String string : dailyEodNames) {
                        eodAchievementsString += Constants.fractalIconEmoji + string + "\n";
                    }
                }

            });
            es.shutdown();

            try {
                while(!es.awaitTermination(1, TimeUnit.MINUTES));
                eb.addField("DAILY PVE ACHIEVEMENTS", pveAchievementsString, false);
                eb.addField("DAILY PVP ACHIEVEMENTS", pvpAchievementsString, false);
                eb.addField("DAILY WVW ACHIEVEMENTS", wvwAchievementsString, false);
                eb.addField("DAILY END OF DRAGONS", eodAchievementsString, false);
                eb.addField("DAILY STRIKES", strikesAchievementsString, false);
                eb.addField("DAILY FRACTALS", tierFourFractalsString, false);
                eb.addField("RECOMMENDED FRACTALS", recommendedFractalsString, false);    

                return eb.build();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
    }
}
