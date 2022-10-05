package com.gw2.discordbot;

import java.awt.Color;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

@SuppressWarnings("null")
public class Gw2Api {

    public static HttpResponse<String> GET_REQUEST(String accountId, String version, String type) {
        try {
            return Unirest.get("https://api.guildwars2.com/" + version + "/" + type)
            .queryString("access_token", accountId)
            .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
            return null;
        }
    }

    
    public static HttpResponse<String> GET_REQUEST(String version, String type) {
        try {
            return Unirest.get("https://api.guildwars2.com/" + version + "/" + type)
            .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Boolean CHECK_API_KEY(String accountId) {
        try {
            HttpResponse<String> request = Unirest.get("https://api.guildwars2.com/v2/account")
            .queryString("access_token", accountId)
            .asString();

            JsonElement je = JsonParser.parseString(request.getBody().toString());

            return je.getAsJsonObject().get("text").getAsString().equals("Invalid access token") ? false : true;
        } catch (UnirestException e) {
            e.printStackTrace();
            return false;
        } catch(NullPointerException e1) {
            return true;
        }
    }

    public static MessageEmbed GET_CHARACTER_INFO(String accountId, String character, String guild) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(character);
        eb.setColor(Color.pink);
        eb.setDescription("GW2 Character Information.");
        eb.setFooter(guild);

        HttpResponse<String> requestCharacters = Gw2Api.GET_REQUEST(accountId, "v2", "characters/" + character);
        JsonElement jsonElementCharacters = JsonParser.parseString(requestCharacters.getBody().toString());

        HttpResponse<String> requestCharactersBuildTab = Gw2Api.GET_REQUEST(accountId, "v2", "characters/" + character + "/buildtabs/active");
        JsonElement jsonElementCharactersBuildTab = JsonParser.parseString(requestCharactersBuildTab.getBody().toString());

        HttpResponse<String> requestCharactersEquipmentTab = Gw2Api.GET_REQUEST(accountId, "v2", "characters/" + character + "/equipmenttabs/active");
        JsonElement jsonElementCharactersEquipmentTab = JsonParser.parseString(requestCharactersEquipmentTab.getBody().toString());

        String characterName = jsonElementCharacters.getAsJsonObject().get("name").getAsString();
        String characterGender = jsonElementCharacters.getAsJsonObject().get("gender").getAsString();
        String characterRace = jsonElementCharacters.getAsJsonObject().get("race").getAsString();
        String characterProfession = jsonElementCharacters.getAsJsonObject().get("profession").getAsString();
        String characterPlayTime = String.valueOf(jsonElementCharacters.getAsJsonObject().get("age").getAsFloat() / 60f / 60f);
        String characterDeaths = String.valueOf(jsonElementCharacters.getAsJsonObject().get("deaths").getAsInt());
        String characterLevel = String.valueOf(jsonElementCharacters.getAsJsonObject().get("level").getAsInt());
        
        String characterActiveTemplateTab = String.valueOf(jsonElementCharactersBuildTab.getAsJsonObject().get("tab").getAsInt());
        
        JsonElement characterActiveBuild = jsonElementCharactersBuildTab.getAsJsonObject().get("build");
        String characterActiveBuildName = characterActiveBuild.getAsJsonObject().get("name").getAsString();
        
        JsonArray characterActiveSpecializations = characterActiveBuild.getAsJsonObject().get("specializations").getAsJsonArray();

        Integer characterActiveSpecializationId = characterActiveSpecializations.get(2).getAsJsonObject().get("id").getAsInt();

        HttpResponse<String> requestAPISpecializations = Gw2Api.GET_REQUEST("v2", "specializations/" + characterActiveSpecializationId);
        JsonElement jsonAPISpecializations = JsonParser.parseString(requestAPISpecializations.getBody().toString());

        String characterEliteProfession = jsonAPISpecializations.getAsJsonObject().get("name").getAsString();
        String characterEliteProfessionImageUrl = jsonAPISpecializations.getAsJsonObject().get("icon").getAsString();

        String characterActiveEquipmentTab = jsonElementCharactersEquipmentTab.getAsJsonObject().get("tab").getAsString();
        String characterActiveEquipmentName = jsonElementCharactersEquipmentTab.getAsJsonObject().get("name").getAsString();

        eb.setThumbnail(characterEliteProfessionImageUrl);

        eb.addField("\u2B50\uFE0F NAME", characterName, true);
        eb.addField("→ GENDER", characterGender, true);
        eb.addField("→ RACE", characterRace, true);

        eb.addField("\u2B50\uFE0F PROFESSION", Constants.specializationEmojis.get(characterProfession) + " " + characterProfession, true);
        eb.addField("\u2B50\uFE0F ELITE PROFESSION", characterEliteProfession, true);
        eb.addField("→ DEATHS", characterDeaths, true);

        eb.addField("→ LEVEL", characterLevel, true);
        eb.addBlankField(true);
        eb.addField("\u2B50\uFE0F PLAY TIME", characterPlayTime + " hours", true);

        eb.addBlankField(false);

        eb.addField("→ ACTIVE BUILD TAB", characterActiveTemplateTab, true);
        eb.addBlankField(true);
        eb.addField("→ ACTIVE EQUIPMENT TAB", characterActiveEquipmentTab, true);

        eb.addField("\u2B50\uFE0F ACTIVE BUILD NAME", characterActiveBuildName.equals("") ? "Default Name" : characterActiveBuildName, true);
        eb.addBlankField(true);
        eb.addField("\u2B50\uFE0F ACTIVE EQUIPMENT NAME", characterActiveEquipmentName.equals("") ? "Default Name" : characterActiveEquipmentName, true);

        return eb.build();
    }


    public static MessageEmbed GET_CHARACTER_INFO(String accountId, String character) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(character);
        eb.setColor(Color.pink);
        eb.setDescription("GW2 Character Information.");
        eb.setFooter(RandomFunnyQuote.getFunnyQuote(), Constants.gw2LogoNoBackground);

        HttpResponse<String> requestCharacters = Gw2Api.GET_REQUEST(accountId, "v2", "characters/" + character);
        JsonElement jsonElementCharacters = JsonParser.parseString(requestCharacters.getBody().toString());

        HttpResponse<String> requestCharactersBuildTab = Gw2Api.GET_REQUEST(accountId, "v2", "characters/" + character + "/buildtabs/active");
        JsonElement jsonElementCharactersBuildTab = JsonParser.parseString(requestCharactersBuildTab.getBody().toString());

        HttpResponse<String> requestCharactersEquipmentTab = Gw2Api.GET_REQUEST(accountId, "v2", "characters/" + character + "/equipmenttabs/active");
        JsonElement jsonElementCharactersEquipmentTab = JsonParser.parseString(requestCharactersEquipmentTab.getBody().toString());

        String characterName = jsonElementCharacters.getAsJsonObject().get("name").getAsString();
        String characterGender = jsonElementCharacters.getAsJsonObject().get("gender").getAsString();
        String characterRace = jsonElementCharacters.getAsJsonObject().get("race").getAsString();
        String characterProfession = jsonElementCharacters.getAsJsonObject().get("profession").getAsString();
        String characterPlayTime = String.valueOf(jsonElementCharacters.getAsJsonObject().get("age").getAsFloat() / 60f / 60f);
        String characterDeaths = String.valueOf(jsonElementCharacters.getAsJsonObject().get("deaths").getAsInt());
        String characterLevel = String.valueOf(jsonElementCharacters.getAsJsonObject().get("level").getAsInt());
        
        String characterActiveTemplateTab = String.valueOf(jsonElementCharactersBuildTab.getAsJsonObject().get("tab").getAsInt());
        
        JsonElement characterActiveBuild = jsonElementCharactersBuildTab.getAsJsonObject().get("build");
        String characterActiveBuildName = characterActiveBuild.getAsJsonObject().get("name").getAsString();
        
        JsonArray characterActiveSpecializations = characterActiveBuild.getAsJsonObject().get("specializations").getAsJsonArray();

        Integer characterActiveSpecializationId = characterActiveSpecializations.get(2).getAsJsonObject().get("id").getAsInt();

        HttpResponse<String> requestAPISpecializations = Gw2Api.GET_REQUEST("v2", "specializations/" + characterActiveSpecializationId);
        JsonElement jsonAPISpecializations = JsonParser.parseString(requestAPISpecializations.getBody().toString());

        String characterEliteProfession = jsonAPISpecializations.getAsJsonObject().get("name").getAsString();
        String characterEliteProfessionImageUrl = jsonAPISpecializations.getAsJsonObject().get("icon").getAsString();

        String characterActiveEquipmentTab = jsonElementCharactersEquipmentTab.getAsJsonObject().get("tab").getAsString();
        String characterActiveEquipmentName = jsonElementCharactersEquipmentTab.getAsJsonObject().get("name").getAsString();

        eb.setThumbnail(characterEliteProfessionImageUrl);

        eb.addField("\u2B50\uFE0F NAME", characterName, true);
        eb.addField("→ GENDER", characterGender, true);
        eb.addField("→ RACE", characterRace, true);

        eb.addField("\u2B50\uFE0F PROFESSION", Constants.specializationEmojis.get(characterProfession) + " " + characterProfession, true);
        eb.addField("\u2B50\uFE0F ELITE PROFESSION", characterEliteProfession, true);
        eb.addField("→ DEATHS", characterDeaths, true);

        eb.addField("→ LEVEL", characterLevel, true);
        eb.addBlankField(true);
        eb.addField("\u2B50\uFE0F PLAY TIME", characterPlayTime + " hours", true);

        eb.addBlankField(false);

        eb.addField("→ ACTIVE BUILD TAB", characterActiveTemplateTab, true);
        eb.addBlankField(true);
        eb.addField("→ ACTIVE EQUIPMENT TAB", characterActiveEquipmentTab, true);

        eb.addField("\u2B50\uFE0F ACTIVE BUILD NAME", characterActiveBuildName.equals("") ? "Default Name" : characterActiveBuildName, true);
        eb.addBlankField(true);
        eb.addField("\u2B50\uFE0F ACTIVE EQUIPMENT NAME", characterActiveEquipmentName.equals("") ? "Default Name" : characterActiveEquipmentName, true);

        return eb.build();
    }
}