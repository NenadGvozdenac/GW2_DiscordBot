package com.gw2.discordbot;

import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu.Builder;

public class Gw2SlashCommandInteraction extends ListenerAdapter {
    
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        
        switch(event.getName()) {
        
            case "add_api":
                ADD_API_COMMAND(event);
            break;

            case "delete_api":
                DELETE_API_COMMAND(event);
            break;

            case "get_api":
                GET_API_COMMAND(event);
            break;
        
            case "gw2account":
                GW2_ACCOUNT_COMMAND(event);
            break;

            case "gw2character":
                GW2_CHARACTER_INFO_COMMAND(event);
            break;

            case "gw2accountraidinfo":
                GW2_ACCOUNT_RAID_INFO_COMMAND(event);
            break;

            case "gw2dailies":
                GW2_DAILIES_COMMAND(event);
            break;

            case "qtpfires":
                QTP_FIRES_EVENT(event);
            break;
        }
    }

    private void QTP_FIRES_EVENT(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply(false).queue();

        String qtpFires = Constants.QTP_FIRES;
        event.getHook().sendMessage(qtpFires).queue();
    }

    private void GET_API_COMMAND(@NotNull SlashCommandInteractionEvent event) {
        Gson gson;
        String userId = event.getUser().getId();
        event.deferReply(true).queue();

        try (FileReader reader = new FileReader(new File(new File("jsonFolder"), "api.json"))) {
            
            gson = new GsonBuilder()
                        .disableHtmlEscaping()
                        .setFieldNamingStrategy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                        .setPrettyPrinting()
                        .serializeNulls()
                        .create();
            
            Type founderTypeSet = new TypeToken<List<UserApi>>(){}.getType();
            List<UserApi> listUserApi = gson.fromJson(reader, founderTypeSet);

            reader.close();

            UserApi theApiKey = null;

            for(UserApi userApi : listUserApi) {
                if(userApi.getUserId().equals(userId)) {
                    theApiKey = userApi;
                }
            }

            if(theApiKey == null) {
                event.getHook().sendMessage("Unfortunately. You have not added an API key to the bot.").queue();
                return;
            }

            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.CYAN);
            eb.setTitle(theApiKey.getUsername());
            eb.setThumbnail(Constants.gw2LogoNoBackground);
            eb.addField("USER ID", theApiKey.getUserId(), false);
            eb.addField("API KEY", theApiKey.getApiKey(), false);

            event.getHook().sendMessageEmbeds(eb.build()).queue();
        } catch(IOException | NullPointerException e) {
            event.getHook().sendMessage("Unfortunately, I couldn't write your API key. If this persists, contact an Administrator.").queue();
            e.printStackTrace();
            return;
        }
    }

    private void GW2_DAILIES_COMMAND(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        event.getHook().sendMessageEmbeds(Constants.loadingEmbedBuilder).queue(message -> 
            message.editMessageEmbeds(Gw2Dailies.getDailies()).queue()
        );
    }

    private void GW2_ACCOUNT_RAID_INFO_COMMAND(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        UserApi accountInfo = UserApi.GET_API_INFO(event.getUser().getId());

        if(accountInfo == null) {
            event.getHook().sendMessage("We do not have your API key. Please register your API key with `/add_api <key>`.").queue();
            return;
        }

        event.getHook().sendMessageEmbeds(Constants.loadingEmbedBuilder).queue(k -> {
            
            EmbedBuilder eb = new EmbedBuilder();
            
            String accountId = accountInfo.getApiKey();

            HttpResponse<String> requestAccountInfo = Gw2Api.GET_REQUEST(accountId, "v2", "account");
            JsonElement jsonAccountInfo = JsonParser.parseString(requestAccountInfo.getBody().toString());

            HttpResponse<String> requestAccountRaidInfo = Gw2Api.GET_REQUEST(accountId, "v2", "account/raids");
            JsonElement jsonAccountRaidInfo = JsonParser.parseString(requestAccountRaidInfo.getBody().toString());

            String accountName = jsonAccountInfo.getAsJsonObject().get("name").getAsString();
            JsonArray accountFinishedBosses = jsonAccountRaidInfo.getAsJsonArray();

            List<String> listOfFinishedBosses = new ArrayList<>();

            eb.setTitle(accountName);
            eb.setThumbnail(event.getUser().getAvatarUrl());
            eb.setFooter("Thank you for using " + Main.jda.getSelfUser().getName(), Constants.gw2LogoNoBackground);
            eb.setColor(Color.pink);
           
            accountFinishedBosses.forEach(boss -> listOfFinishedBosses.add(boss.getAsString()));

            eb.addField(
                "SPIRIT VALE", 
                (listOfFinishedBosses.contains("vale_guardian") ? "\u2705" : "\u274C") + " " + Constants.bossesNamesGW2.get("vale_guardian") + "\n" + 
                (listOfFinishedBosses.contains("spirit_woods") ? "\u2705" : "\u274C") + " " + Constants.bossesNamesGW2.get("spirit_woods") + "\n" +
                (listOfFinishedBosses.contains("gorseval") ? "\u2705" : "\u274C") + " " + Constants.bossesNamesGW2.get("gorseval") + "\n" + 
                (listOfFinishedBosses.contains("sabetha") ? "\u2705" : "\u274C") + " " + Constants.bossesNamesGW2.get("sabetha"),
            true);

            eb.addField(
                "SALVATION PASS", 
                (listOfFinishedBosses.contains("slothasor") ? "\u2705" : "\u274C") + " " + Constants.bossesNamesGW2.get("slothasor") + "\n" + 
                (listOfFinishedBosses.contains("bandit_trio") ? "\u2705" : "\u274C") + " " + Constants.bossesNamesGW2.get("bandit_trio") + "\n" +
                (listOfFinishedBosses.contains("matthias") ? "\u2705" : "\u274C") + " " + Constants.bossesNamesGW2.get("matthias"),
            true);

            eb.addField(
                "STRONGHOLD OF THE FAITHFUL", 
                (listOfFinishedBosses.contains("escort") ? "\u2705" : "\u274C") + " " + Constants.bossesNamesGW2.get("escort") + "\n" + 
                (listOfFinishedBosses.contains("keep_construct") ? "\u2705" : "\u274C") + " " + Constants.bossesNamesGW2.get("keep_construct") + "\n" +
                (listOfFinishedBosses.contains("twisted_castle") ? "\u2705" : "\u274C") + " " + Constants.bossesNamesGW2.get("twisted_castle") + "\n" + 
                (listOfFinishedBosses.contains("xera") ? "\u2705" : "\u274C") + " " + Constants.bossesNamesGW2.get("xera"),
            true);

            eb.addField(
                "BASTION OF THE PENITENT", 
                (listOfFinishedBosses.contains("cairn") ? "\u2705" : "\u274C") + " " + Constants.bossesNamesGW2.get("cairn") + "\n" + 
                (listOfFinishedBosses.contains("mursaat_overseer") ? "\u2705" : "\u274C") + " " + Constants.bossesNamesGW2.get("mursaat_overseer") + "\n" +
                (listOfFinishedBosses.contains("samarog") ? "\u2705" : "\u274C") + " " + Constants.bossesNamesGW2.get("samarog") + "\n" + 
                (listOfFinishedBosses.contains("deimos") ? "\u2705" : "\u274C") + " " + Constants.bossesNamesGW2.get("deimos"),
            true);

            eb.addField(
                "HALL OF CHAINS", 
                (listOfFinishedBosses.contains("soulless_horror") ? "\u2705" : "\u274C") + " " + Constants.bossesNamesGW2.get("soulless_horror") + "\n" + 
                (listOfFinishedBosses.contains("river_of_souls") ? "\u2705" : "\u274C") + " " + Constants.bossesNamesGW2.get("river_of_souls") + "\n" +
                (listOfFinishedBosses.contains("statues_of_grenth") ? "\u2705" : "\u274C") + " " + Constants.bossesNamesGW2.get("statues_of_grenth") + "\n" + 
                (listOfFinishedBosses.contains("voice_in_the_void") ? "\u2705" : "\u274C") + " " + Constants.bossesNamesGW2.get("voice_in_the_void"),
            true);

            eb.addField(
                "MYTHWRIGHT GAMBIT", 
                (listOfFinishedBosses.contains("conjured_amalgamate") ? "\u2705" : "\u274C") + " " + Constants.bossesNamesGW2.get("conjured_amalgamate") + "\n" + 
                (listOfFinishedBosses.contains("twin_largos") ? "\u2705" : "\u274C") + " " + Constants.bossesNamesGW2.get("twin_largos") + "\n" +
                (listOfFinishedBosses.contains("qadim") ? "\u2705" : "\u274C") + " " + Constants.bossesNamesGW2.get("qadim"),
            true);

            eb.addField(
                "THE KEY OF AHDASHIM", 
                (listOfFinishedBosses.contains("gate") ? "\u2705" : "\u274C") + " " + Constants.bossesNamesGW2.get("gate") + "\n" + 
                (listOfFinishedBosses.contains("adina") ? "\u2705" : "\u274C") + " " + Constants.bossesNamesGW2.get("adina") + "\n" +
                (listOfFinishedBosses.contains("sabir") ? "\u2705" : "\u274C") + " " + Constants.bossesNamesGW2.get("sabir") + "\n" + 
                (listOfFinishedBosses.contains("qadim_the_peerless") ? "\u2705" : "\u274C") + " " + Constants.bossesNamesGW2.get("qadim_the_peerless"),
            true);

            k.editMessageEmbeds(eb.build()).queue();
        });
    }

    private void GW2_CHARACTER_INFO_COMMAND(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();
        UserApi accountInfo = UserApi.GET_API_INFO(event.getUser().getId());
        
        if(accountInfo == null) {
            event.getHook().sendMessage("We do not have your API key. Please register your API key with `/add_api <key>`.").queue();
            return;
        }

        event.getHook().sendMessageEmbeds(Constants.loadingEmbedBuilder).queue(k -> {
            String accountId = accountInfo.getApiKey();

            HttpResponse<String> requestCharacters = Gw2Api.GET_REQUEST(accountId, "v2", "characters");
            JsonElement jsonElementCharacters = JsonParser.parseString(requestCharacters.getBody().toString());

            JsonArray arrayOfCharacters = jsonElementCharacters.getAsJsonArray();

            List<String> characterList = new ArrayList<>();
            arrayOfCharacters.forEach(m -> characterList.add(m.getAsString()));

            MessageEmbed embedForSending;

            if(event.isFromGuild()) {
                embedForSending = Gw2Api.GET_CHARACTER_INFO(accountId, characterList.get(0), event.getGuild().getName());
            } else {
                embedForSending = Gw2Api.GET_CHARACTER_INFO(accountId, characterList.get(0));
            }
               
            HttpResponse<String> requestCharacterInfo = Gw2Api.GET_REQUEST(accountId, "v2", "characters/" + characterList.get(0));
            JsonElement characterJsonInformation = JsonParser.parseString(requestCharacterInfo.getBody().toString());

            String characterProfession = characterJsonInformation.getAsJsonObject().get("profession").getAsString();
            String characterProfessionIcon = Constants.specializationEmojis.get(characterProfession);

            Builder characterMenu = SelectMenu.create("character_choosing");
            
            List<SelectOption> characterOptions = new ArrayList<>();

            SelectOption option = SelectOption.of(characterList.get(0), characterList.get(0).toUpperCase()).withDefault(true).withEmoji(Emoji.fromFormatted(characterProfessionIcon));
            characterOptions.add(option);

            ExecutorService es = Executors.newCachedThreadPool();

            for(int i = 1; i < characterList.size(); i++) {
                String selectedCharacter = characterList.get(i);
                
                es.execute(new Runnable() {
                    public void run() {
                        HttpResponse<String> requestCharacterInfo = Gw2Api.GET_REQUEST(accountId, "v2", "characters/" + selectedCharacter);
                        JsonElement characterJsonInformation = JsonParser.parseString(requestCharacterInfo.getBody().toString());
                        
                        String characterProfession = characterJsonInformation.getAsJsonObject().get("profession").getAsString();
                        String characterProfessionIcon = Constants.specializationEmojis.get(characterProfession);
        
                        SelectOption option = SelectOption.of(selectedCharacter, selectedCharacter.toUpperCase())
                            .withDefault(false)
                            .withEmoji(Emoji
                            .fromFormatted(characterProfessionIcon));
        
                        characterOptions.add(option);
                    }
                });
            }

            es.shutdown();

            try {
                while(!es.awaitTermination(1, TimeUnit.MINUTES));
                characterMenu.addOptions(characterOptions);

                ActionRow actionRow = ActionRow.of(characterMenu.build());
                k.editMessageEmbeds(embedForSending).setActionRows(actionRow).queue();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void GW2_ACCOUNT_COMMAND(@NotNull SlashCommandInteractionEvent event) {

        event.deferReply(true).queue();
        UserApi accountInfo = UserApi.GET_API_INFO(event.getUser().getId());

        if(accountInfo == null) {
            event.getHook().sendMessage("We do not have your API key. Please register your API key with `/add_api <key>`.").queue();
            return;
        }

        event.getHook().sendMessageEmbeds(Constants.loadingEmbedBuilder).queue(k -> {
            String accountId = accountInfo.getApiKey();
        
            ExecutorService es = Executors.newCachedThreadPool();

            HttpResponse<String> requestCharacters = Gw2Api.GET_REQUEST(accountId, "v2", "characters");
            JsonElement jsonElementCharacters = JsonParser.parseString(requestCharacters.getBody().toString());

            HttpResponse<String> requestAccount = Gw2Api.GET_REQUEST(accountId, "v2", "account");
            JsonElement jsonElementAccount = JsonParser.parseString(requestAccount.getBody().toString());

            HttpResponse<String> requestWalletInformation = Gw2Api.GET_REQUEST(accountId, "v2", "account/wallet");
            JsonElement jsonElementWallet = JsonParser.parseString(requestWalletInformation.getBody().toString());

            JsonArray arrayOfCharacters = jsonElementCharacters.getAsJsonArray();

            String accountAge = Constants.df.format(jsonElementAccount.getAsJsonObject().get("age").getAsInt() / 60f / 60f);
            Boolean accountHasCommanderTag = jsonElementAccount.getAsJsonObject().get("commander").getAsBoolean();
            String accountFractalRank = String.valueOf(jsonElementAccount.getAsJsonObject().get("fractal_level").getAsInt());
            String accountWvWLevel = String.valueOf(jsonElementAccount.getAsJsonObject().get("wvw_rank").getAsInt());
            String accountMonthlyAP = String.valueOf(jsonElementAccount.getAsJsonObject().get("monthly_ap").getAsInt());
            String accountDailyAP = String.valueOf(jsonElementAccount.getAsJsonObject().get("daily_ap").getAsInt());
            String accountName = jsonElementAccount.getAsJsonObject().get("name").getAsString();

            String accountWalletGold = String.valueOf(jsonElementWallet.getAsJsonArray().get(0).getAsJsonObject().get("value").getAsInt() / 100f / 100f);
            String accountWalletKarma = String.valueOf(jsonElementWallet.getAsJsonArray().get(1).getAsJsonObject().get("value").getAsInt());
            String accountWalletGems = String.valueOf(jsonElementWallet.getAsJsonArray().get(3).getAsJsonObject().get("value").getAsInt());

            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.pink);
            eb.setDescription("Basic GW2 Account Information.");
            eb.setFooter("Thank you for using " + Main.jda.getSelfUser().getName(), Constants.gw2LogoNoBackground);
            eb.setThumbnail(event.getUser().getAvatarUrl());
            eb.setTitle(accountName);
            eb.addField("Time Played", accountAge + " hours", true);
            eb.addField(Constants.commanderIconEmoji + " Commander", String.valueOf(accountHasCommanderTag), true);
            eb.addField(Constants.fractalRelicIconEmoji + "Fractal Level", accountFractalRank, true);
            eb.addField(Constants.wvwIconEmoji + "WvW Level", accountWvWLevel, true);
            eb.addField(Constants.achievementPointIconEmoji + "Daily AP", accountDailyAP, true);
            eb.addField(Constants.achievementPointIconEmoji + "Monthly AP", accountMonthlyAP, true);
            eb.addField(Constants.goldIconEmoji + "Wallet Gold", accountWalletGold, true);
            eb.addField(Constants.karmaIconEmoji + "Wallet Karma", accountWalletKarma, true);
            eb.addField(Constants.gemIconEmoji + "Wallet Gems", accountWalletGems, true);

            eb.addBlankField(false);

            List<String> characterList = new ArrayList<>();
            arrayOfCharacters.forEach(m -> characterList.add(m.getAsString()));

            for(String character : characterList) {
                es.execute(new Runnable() {
                    @Override
                    public void run() {
                        HttpResponse<String> requestCharacterInfo = Gw2Api.GET_REQUEST(accountId, "v2", "characters/" + character);
                        JsonElement characterJsonInformation = JsonParser.parseString(requestCharacterInfo.getBody().toString());
        
                        String characterName = characterJsonInformation.getAsJsonObject().get("name").getAsString();
                        
                        Integer characterDeaths = characterJsonInformation.getAsJsonObject().get("deaths").getAsInt();
        
                        HttpResponse<String> requestCharacterTitle = Gw2Api.GET_REQUEST(accountId, "v2", "titles/" + characterJsonInformation.getAsJsonObject().get("title"));
                        JsonElement characterJsonTitle = JsonParser.parseString(requestCharacterTitle.getBody().toString());
        
                        String characterTitle = characterJsonTitle.getAsJsonObject().get("name").getAsString();
        
                        String characterProfession = characterJsonInformation.getAsJsonObject().get("profession").getAsString();
                        String characterProfessionIcon = Constants.specializationEmojis.get(characterProfession);
        
                        Float characterTimePlayedInHours = characterJsonInformation.getAsJsonObject().get("age").getAsInt() / 60f / 60f;
                        String characterDeathPerHour = Constants.df.format(characterDeaths / characterTimePlayedInHours);
        
                        eb.addField(
                            characterProfessionIcon + " " + 
                            characterName, 
                            "→ " + characterProfession + 
                            "\n→ Title: " + characterTitle + 
                            "\n→ Age: " + characterTimePlayedInHours + " h." + 
                            "\n→ Deaths: " + characterDeaths +
                            "\n→ DpH: " + characterDeathPerHour
                        , true);
                    }
                });
            }

            es.shutdown();

            try {
                while(!es.awaitTermination(1, TimeUnit.MINUTES)) {}
                k.editMessageEmbeds(eb.build()).queue();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void DELETE_API_COMMAND(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();
        Gson gson;
        String userId = event.getUser().getId();

        try (FileReader reader = new FileReader(new File(new File("jsonFolder"), "api.json"))) {
            
            gson = new GsonBuilder()
                        .disableHtmlEscaping()
                        .setFieldNamingStrategy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                        .setPrettyPrinting()
                        .serializeNulls()
                        .create();
            
            Type founderTypeSet = new TypeToken<List<UserApi>>(){}.getType();
            List<UserApi> listUserApi = gson.fromJson(reader, founderTypeSet);

            UserApi deleting = null;

            for(UserApi userApi : listUserApi) {
                if(userApi.getUserId().equals(userId)) {
                    deleting = userApi;
                }
            }

            if(deleting == null) {
                event.getHook().sendMessage("Unfortunately. You have not added an API key to the bot.").queue();
                return;
            }

            listUserApi.remove(deleting);

            reader.close();
            FileWriter writer = new FileWriter(new File(new File("jsonFolder"), "api.json"));

            String writingString = gson.toJson(listUserApi);
            writer.write(writingString);
            writer.close();

            event.getHook().sendMessage("Deleted your API key from the bot.").queue();
        } catch(IOException | NullPointerException e) {
            event.getHook().sendMessage("Unfortunately, I couldn't write your API key. If this persists, contact an Administrator.").queue();
            e.printStackTrace();
            return;
        }
    }

    private void ADD_API_COMMAND(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();
        UserApi api;
        Gson gson;

        String apiKey = event.getOption("apikey").getAsString();

        try (FileReader reader = new FileReader(new File(new File("jsonFolder"), "api.json"))) {
            
            gson = new GsonBuilder()
                        .disableHtmlEscaping()
                        .setFieldNamingStrategy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                        .setPrettyPrinting()
                        .serializeNulls()
                        .create();
            
            Type founderTypeSet = new TypeToken<List<UserApi>>(){}.getType();
            List<UserApi> listUserApi = gson.fromJson(reader, founderTypeSet);

            api = new UserApi(event.getUser().getAsTag(), event.getUser().getId(), apiKey); 

            if(Gw2Api.CHECK_API_KEY(api.getApiKey()) == false) {
                event.getHook().sendMessage("Unfortunately, that is not a valid API key.").queue();
                return;
            } 

            UserApi deleting = null;

            for(UserApi userApi : listUserApi) {
                if(userApi.getUserId().equals(api.getUserId())) {
                    deleting = userApi;
                }
            }

            if(deleting != null) {
                listUserApi.remove(deleting);
            }

            listUserApi.add(api);

            reader.close();
            FileWriter writer = new FileWriter(new File(new File("jsonFolder"), "api.json"));

            String writingString = gson.toJson(listUserApi);
            writer.write(writingString);
            writer.close();

            event.getHook().sendMessage(deleting == null ? "Added your API key to our storage. " : "You already had one assigned API key to your name. Instead, the other API key is replaced by this one.").queue();
        } catch(IOException | NullPointerException e) {
            event.getHook().sendMessage("Unfortunately, I couldn't write your API key. If this persists, contact an Administrator.").queue();
            e.printStackTrace();
            return;
        }
    }

}
