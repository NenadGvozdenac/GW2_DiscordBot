package com.gw2.discordbot.Miscellaneous;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.gw2.discordbot.DiscordBot.Constants;
import com.gw2.discordbot.HttpParsing.Gw2Api;

import kong.unirest.HttpResponse;

public class Boss {
    
    public String wingName;
    public String bossName;
    public String killTime;
    public String dpsReportLink;
    public String emoji;
    public Boolean isFailed;
    public String startTime;
    public String endTime;
    public Boolean bossIsCm;

    public Boss(String bossLogPermaLink, String bossLogName, String bossIsCm, Boolean bossLogSuccess,
            String bossLogTime, String startTime, String endTime) {

            this.wingName = "null";
            this.emoji = "null";

            this.bossName = bossLogName;

            String time = "00h " + bossLogTime;
            time = time.substring(0, (time.indexOf("s")) + 1);

            String[] parts = time.split(" "); 

            for(int i = 0; i < parts.length; i++) {
                if(parts[i].length() != 3) {
                    parts[i] = "0" + parts[i];
                }
            }

            time = "";

            for(String part : parts) {
                time += part + " ";
            }

            time = time.substring(0, time.length() - 1);
            
            this.killTime = time;
            this.isFailed = bossLogSuccess;
            this.dpsReportLink = bossLogPermaLink;
            this.startTime = startTime;
            this.endTime = endTime;

            for(Entry<String, String> entry : Constants.listOfEmojisAndBosses.entrySet()) {
                if(bossLogName.contains(entry.getKey())) {
                    this.emoji = entry.getValue();
                    break;
                }
            }

            for(Entry<String, ArrayList<String>> entry : Constants.listOfBossesAndWings.entrySet()) {
                for(String boss : entry.getValue()) {
                    if(bossLogName.contains(boss)) {
                        this.wingName = entry.getKey();
                        return;
                    }
                }
            }
    }

    public Boss(String endName, String dpsReportLink) throws Exception {
        
        try {
            this.bossName = this.getBossName(endName);
            this.wingName = this.getBossWingName();
            this.emoji = this.getBossEmoji();
            this.dpsReportLink = dpsReportLink;

            HttpResponse<String> request = Gw2Api.GET_INFO_FROM_LINK("https://dps.report/getUploadMetadata?permalink=" + dpsReportLink);
            JsonElement je = JsonParser.parseString(request.getBody().toString());
            
            this.killTime = String.valueOf(je.getAsJsonObject().get("encounter").getAsJsonObject().get("duration").getAsFloat());
            this.isFailed = !je.getAsJsonObject().get("encounter").getAsJsonObject().get("success").getAsBoolean();
            this.bossIsCm = je.getAsJsonObject().get("encounter").getAsJsonObject().get("isCm").getAsBoolean();

            Instant instant = Instant.ofEpochSecond((long) je.getAsJsonObject().get("encounterTime").getAsInt());
            Date date = Date.from(instant);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss X");
            this.startTime = sdf.format(date);

            instant = Instant.ofEpochSecond((long) je.getAsJsonObject().get("encounterTime").getAsInt() + (long)Float.parseFloat(this.killTime));
            date = Date.from(instant);

            this.endTime = sdf.format(date);
        } catch(NullPointerException | JsonParseException e) {
            throw new Exception("Error Parsing Log");
        }
    }

    private String getBossName(String endName) {
        for(Entry<String, String> entry : Constants.listOfShortNamesOfBosses.entrySet()) {
            if(entry.getValue().equalsIgnoreCase(endName)) {
                return entry.getKey();
            }
        }

        return "Unidentified Name";
    }

    private String getBossWingName() {
        for(Entry<String, ArrayList<String>> entry : Constants.listOfBossesAndWings.entrySet()) {
            for(String string : entry.getValue()) {
                if(string.equalsIgnoreCase(this.bossName)) {
                    return entry.getKey();
                }
            }
        }

        return "Unidentified";
    }

    private String getBossEmoji() {
         for(Entry<String, String> entry : Constants.listOfEmojisAndBosses.entrySet()) {
            if(entry.getKey().equalsIgnoreCase(this.bossName)) {
                return entry.getValue();
            }
        }

        return "null";
    }

    @Override
    public String toString() {
        return "[Time: " + this.killTime + ", Wing: " + this.wingName + ", Boss: " + this.bossName + ", Link: " + this.dpsReportLink + ", isFailed: " + this.isFailed + "]";
    }

    public static ArrayList<Boss> getBossArrayFromLinks(ArrayList<String> arrayOfBossesLinks) throws Exception {

        ArrayList<Boss> arrayListOfBosses = new ArrayList<>();

        for(String dpsreportlink : arrayOfBossesLinks) {
            String endName = dpsreportlink.split("_")[dpsreportlink.split("_").length - 1];
            Boss boss = new Boss(endName, dpsreportlink);
            arrayListOfBosses.add(boss);
            System.out.println(boss);
        }

        return arrayListOfBosses;
    }

    public static Boss getFirstBoss(ArrayList<Boss> listOfBosses) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss X", Locale.getDefault());

        long earliestBossTime = Long.MAX_VALUE;
        Boss earliestBoss = null;

        for(Boss boss : listOfBosses) {
            try {
                long startMillis = sdf.parse(boss.startTime).getTime();

                if(startMillis < earliestBossTime) {
                    earliestBossTime = startMillis;
                    earliestBoss = boss;
                }
                   
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return earliestBoss;
    }

    public static Boss getLastBoss(ArrayList<Boss> listOfBosses) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss X", Locale.getDefault());

        long latestBossTime = 0;
        Boss latestBoss = null;

        for(Boss boss : listOfBosses) {
            try {
                long endMillis = sdf.parse(boss.endTime).getTime();

                if(endMillis > latestBossTime) {
                    latestBossTime = endMillis;
                    latestBoss = boss;
                }
                   
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return latestBoss;
    }
}
