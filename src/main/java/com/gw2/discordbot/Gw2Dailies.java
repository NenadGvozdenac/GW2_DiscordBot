package com.gw2.discordbot;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import kong.unirest.HttpResponse;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class Gw2Dailies {

    public CompletableFuture<MessageEmbed> getDailies() {
            CompletableFuture<MessageEmbed> future = CompletableFuture.supplyAsync(new Supplier<MessageEmbed>() {
                @Override
                public MessageEmbed get() {

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
        
                    eb.setFooter(RandomFunnyQuote.getFunnyQuote(), Constants.gw2LogoNoBackground);

                    String tierFourFractalsString = "";
                    String recommendedFractalsString = "";

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

                    eb.addField("DAILY FRACTALS", tierFourFractalsString, false);
                    eb.addField("RECOMMENDED FRACTALS", recommendedFractalsString, false);    

                    String pveAchievementsString = "";

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

                    eb.addField("DAILY PVE ACHIEVEMENTS", pveAchievementsString, false);

                    String pvpAchievementsString = "";
                            
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

                    eb.addField("DAILY PVP ACHIEVEMENTS", pvpAchievementsString, false);

                    // WVW
                    String wvwAchievementsString = "";

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

                    eb.addField("DAILY WVW ACHIEVEMENTS", wvwAchievementsString, false);

                    String strikesAchievementsString = "";

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

                    eb.addField("DAILY STRIKES", strikesAchievementsString, false);

                    String eodAchievementsString = "";

                    HttpResponse<String> responseEODeInfo = Gw2Api.GET_REQUEST("v2", "achievements/categories/321");
                    JsonElement dailyEOD = JsonParser.parseString(responseEODeInfo.getBody());

                    JsonArray listOfAchievements1 = dailyEOD.getAsJsonObject().get("achievements").getAsJsonArray();

                    List<Integer> dailyEodIds = new ArrayList<>();
                    List<String> dailyEodNames = new ArrayList<>();

                    listOfAchievements1.forEach(achievement -> dailyEodIds.add(achievement.getAsInt()));

                    for(Integer integer : dailyEodIds) {
                        HttpResponse<String> responseEodInfo = Gw2Api.GET_REQUEST("v2", "achievements/" + integer);
                        JsonElement achievementInfo = JsonParser.parseString(responseEodInfo.getBody());

                        String achievementName = achievementInfo.getAsJsonObject().get("name").getAsString();

                        dailyEodNames.add(achievementName);
                    }

                    for(String string : dailyEodNames) {
                        eodAchievementsString += Constants.fractalIconEmoji + string + "\n";
                    }

                    eb.addField("DAILY END OF DRAGONS", eodAchievementsString, false);
                    
                    return eb.build();
                }
            }
        );

        return future;
    }
}
