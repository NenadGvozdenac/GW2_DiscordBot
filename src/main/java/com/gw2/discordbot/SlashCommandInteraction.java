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
import java.util.stream.Collectors;

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
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu.Builder;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

public class SlashCommandInteraction extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        switch(event.getName()) {
            case "ping":
                PING_COMMAND(event);
            break;

            case "add_api":
                ADD_API_COMMAND(event);
            break;

            case "delete_api":
                DELETE_API_COMMAND(event);
            break;

            case "help":
                HELP_COMMAND(event);
            break;

            case "profile":
                PROFILE_COMMAND(event);
            break;

            case "gw2account":
                GW2_ACCOUNT_COMMAND(event);
            break;

            case "gw2character":
                GW2_CHARACTER_INFO_COMMAND(event);
            break;

            case "resetslashcommands":
                RESET_SLASH_COMMANDS(event);
            break;

            case "gw2accountraidinfo":
                GW2_ACCOUNT_RAID_INFO_COMMAND(event);
            break;

            case "gw2dailies":
                GW2_DAILIES_COMMAND(event);
            break;

            case "purge":
                PURGE_COMMAND(event);
            break;

            case "apistatus":
                API_STATUS(event);
            break;

            case "shutdown":
                SHUTDOWN_EVENT(event);
            break;

            case "test":
                TEST_COMMAND(event);
            break;

            case "contact-developer":
                CONTACT_DEVELOPER(event);
            break;

            case "invite":
                INVITE_EVENT(event);
            break;

            case "announce":
                ANNOUNCE_EVENT(event);
            break;

            default:
                event.deferReply(true).queue();
                event.getHook().sendMessage("Unfortunately, I cannot find that command.").queue();
        }
    }
    
    private void ANNOUNCE_EVENT(@NotNull SlashCommandInteractionEvent event) {
        Modal modal = Modal.create("announcementmodal", "#" + event.getGuild().getTextChannelById(Constants.announcementChannelID).getName())
            .addActionRows(
                ActionRow.of(
                    TextInput.create("title", "Announcement Title", TextInputStyle.SHORT)
                    .setMinLength(5)
                    .setPlaceholder("The title for the announcement!")
                    .setRequired(true).build()
                ),
                ActionRow.of(
                    TextInput.create("content", "Announcement body", TextInputStyle.PARAGRAPH)
                    .setMinLength(5)
                    .setPlaceholder("The body for the announcement!")
                    .setRequired(true).build()
                )
                ).build();
                
        event.replyModal(modal).queue();
    }

    private void INVITE_EVENT(@NotNull SlashCommandInteractionEvent event) {

        if(event.getOptions().isEmpty()) {
            event.reply("Invite to this guild: " + event.getGuild().retrieveInvites().complete().get(0).getUrl()).queue();
            return;
        }

        Boolean isPrivate = event.getOption("private").getAsBoolean();

        if(isPrivate) {
            event.deferReply(true).queue();
            event.getHook().sendMessage("Invite to this guild: " + event.getGuild().retrieveInvites().complete().get(0).getUrl()).queue();
            return;
        } else {
            event.reply("Invite to this guild: " + event.getGuild().retrieveInvites().complete().get(0).getUrl()).queue();
            return;
        }
    }

    private void CONTACT_DEVELOPER(@NotNull SlashCommandInteractionEvent event) {
        TextInput subject = TextInput.create("subject", "Subject", TextInputStyle.SHORT)
            .setPlaceholder("Enter the subject")
            .setRequired(true)
            .setMaxLength(30).build();

        TextInput body = TextInput.create("body", "Body", TextInputStyle.PARAGRAPH)
            .setPlaceholder("Enter the body")
            .setRequired(true)
            .setMaxLength(500).build();

        Modal modal = Modal.create("contact_developer", "Contact Developer")
            .addActionRows(ActionRow.of(subject), ActionRow.of(body)).build();

        event.replyModal(modal).queue();
    }

    private void TEST_COMMAND(@NotNull SlashCommandInteractionEvent event) {
        
    }

    private void SHUTDOWN_EVENT(@NotNull SlashCommandInteractionEvent event) {
        event.getJDA().shutdown();
    }

    private void API_STATUS(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("API STATUS");
        eb.setDescription("Returns the current API availability.");
        eb.setThumbnail(Constants.gw2LogoNoBackground);
        eb.setFooter("Usable in DMs also!", Constants.gw2LogoNoBackground);

        event.getHook().sendMessageEmbeds(Constants.loadingEmbedBuilder).queue(message -> {
            Boolean everythingOK = true;
    
            HttpResponse<String> responseGW2 = Gw2Api.GET_REQUEST("v2", "");
        
            if(responseGW2.getStatus() == 200) {
                eb.addField("GW2 API ?", "\u2705\u2705\u2705", false);   // if success
            } else {
                everythingOK = false;
                eb.addField("GW2 API ?", "\u274C\u274C\u274C", false);   // if not success
            }
    
            HttpResponse<String> responseDPSREPORT = DpsReportApi.GET_TOKEN();

            if(responseDPSREPORT.getStatus() == 200) {
                eb.addField("DPS.REPORT API ?", "\u2705\u2705\u2705", false);
            } else {
                everythingOK = false;
                eb.addField("DPS.REPORT API ?", "\u274C\u274C\u274C", false);   // if not success
            }
    
            if(everythingOK) {
                eb.setColor(Color.GREEN);
            } else {
                eb.setColor(Color.RED);
            }
    
            message.editMessageEmbeds(eb.build()).queue();
        });
    }

    private void PURGE_COMMAND(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();
		
		Integer numberOfMessages = event.getOption("number").getAsInt();

        if(numberOfMessages > 100) {
            event.getHook().sendMessage("`Unfortunately. Max messages purged -- at once --  can be 100.`").queue();
            return;
        }

		TextChannel channel = event.getChannel().asTextChannel();

		if(numberOfMessages == 1) {
			String message = channel.getLatestMessageId();
			channel.deleteMessageById(message).queue();
		} else {
			List<Message> messages = channel.getHistory().retrievePast(numberOfMessages).complete();
			channel.deleteMessages(messages).queue();
		}

        event.getHook().sendMessage("Successfully purged " + numberOfMessages + " messages!").queue();
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

    private void RESET_SLASH_COMMANDS(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();
        Main.jda.updateCommands().queue();

        for(SlashCommandData data : Main.commandData) {
            // data.addToGuild(event.getJDA().getGuildById("1007915730928418856"));
            data.addGlobally(Main.jda);

            event.getUser().openPrivateChannel().queue(channel -> {
                channel.sendMessage("Added command `" + data.getCommandName() + "`.").queue();
            });
        }

        SlashCommandData.insertIntoJson(Main.commandData);
        event.getHook().sendMessage("I have reset the slash commands of this server, per your request.").queue();
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

    private void PROFILE_COMMAND(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        if(!event.isFromGuild()) {
            event.getHook().sendMessage("Unfortunately, this is only usable in servers!").queue();
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.pink);
        eb.setTitle(event.getUser().getAsTag());
        eb.setThumbnail(event.getUser().getEffectiveAvatarUrl());
        eb.setFooter("Thank you for using " + Main.jda.getSelfUser().getName(), Constants.gw2LogoNoBackground);

        eb.addField("USER", event.getUser().getAsTag(), true);
        eb.addField("DAY JOINED", 
            event.getMember().getTimeJoined().getDayOfMonth() + "/" + 
            event.getMember().getTimeJoined().getMonth() + "/" + 
            event.getMember().getTimeJoined().getYear(), 
            true);
        eb.addField("ID", event.getMember().getId(), true);
        eb.addField("ACTIVITY", event.getMember().getOnlineStatus().name(), true);
        eb.addField("ROLES", event.getMember().getRoles().stream().map(n -> n.getName()).collect(Collectors.joining("\n")), true);

        UserApi accountInfo = UserApi.GET_API_INFO(event.getUser().getId());

        if(accountInfo == null) {
           eb.addField("API KEY", "NONE", true);
         } else {
            eb.addField("API KEY", "||" + accountInfo.getApiKey() + "||", true);
            
            String accountId = accountInfo.getApiKey();
            HttpResponse<String> request = Gw2Api.GET_REQUEST(accountId, "v2", "account");
            JsonElement je = JsonParser.parseString(request.getBody().toString());

            eb.addField("LINKED ACCOUNT", je.getAsJsonObject().get("name").getAsString(), true);
        }

        event.getHook().sendMessageEmbeds(eb.build()).queue();
    }

    private void HELP_COMMAND(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        if(HelpButtonEventListener.embedBuilders.size() != 1) {

            for(Object o : event.getJDA().getRegisteredListeners()) {
                if(o instanceof HelpButtonEventListener) {
                    event.getJDA().removeEventListener(o);
                }
            }

            event.getJDA().addEventListener(new HelpButtonEventListener());

            event.getHook().sendMessageEmbeds(HelpButtonEventListener.embedBuilders.get(0).build()).addActionRows(
                ActionRow.of(
                    Button.primary("left", "\u2B05\uFE0F \u2B05\uFE0F").asDisabled(),
                    Button.primary("right", "\u27A1\uFE0F \u27A1\uFE0F")
                )
            ).queue();
        } else {

            for(Object o : event.getJDA().getRegisteredListeners()) {
                if(o instanceof HelpButtonEventListener) {
                    event.getJDA().removeEventListener(o);
                }
            }

            event.getJDA().addEventListener(new HelpButtonEventListener());
            event.getHook().sendMessageEmbeds(HelpButtonEventListener.embedBuilders.get(0).build()).queue();
        }
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

    private void PING_COMMAND(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();
        event.getHook().sendMessage("`BEEP BOOP, CALCULATING PING...`").queue(message -> {
        
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.pink);     

            Main.jda.getRestPing().queue(ping -> {
                eb.addField("\uD83E\uDD16 BOT PING \uD83E\uDD16", ping + " ms.", true);
                eb.addField("\uD83E\uDD16 WEBSOCKET PING \uD83E\uDD16", event.getJDA().getGatewayPing() + " ms.", true);
                message.editMessage("`BEEP BOOP, PING CALCULATED...`").setEmbeds(eb.build()).queueAfter(2, TimeUnit.SECONDS);
            });
        });
    }
}
