package com.gw2.discordbot;

import java.util.ArrayList;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import kong.unirest.HttpResponse;

public class Boss {
    
    String wingName;
    String bossName;
    String killTime;
    String dpsReportLink;
    String emoji;
    Boolean isFailed;
    String startTime;
    String endTime;

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

    public Boss(String endName, String dpsReportLink) {
        this.bossName = this.getBossName(endName);
        this.wingName = this.getBossWingName();
        this.emoji = this.getBossEmoji();
        this.dpsReportLink = dpsReportLink;

        HttpResponse<String> request = Gw2Api.GET_INFO_FROM_LINK("https://dps.report/getUploadMetadata?permalink=" + dpsReportLink);
        JsonElement je = JsonParser.parseString(request.getBody().toString());
        
        this.killTime = String.valueOf(je.getAsJsonObject().get("encounter").getAsJsonObject().get("duration").getAsFloat());
        this.isFailed = !je.getAsJsonObject().get("encounter").getAsJsonObject().get("success").getAsBoolean();
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

    public static ArrayList<Boss> getBossArrayFromLinks(ArrayList<String> arrayOfBossesLinks) {

        ArrayList<Boss> arrayListOfBosses = new ArrayList<>();

        for(String dpsreportlink : arrayOfBossesLinks) {
            String endName = dpsreportlink.split("_")[dpsreportlink.split("_").length - 1];
            Boss boss = new Boss(endName, dpsreportlink);
            arrayListOfBosses.add(boss);
            System.out.println(boss);
        }

        return arrayListOfBosses;
    }
}
