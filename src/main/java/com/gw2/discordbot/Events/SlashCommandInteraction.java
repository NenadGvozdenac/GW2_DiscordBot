package com.gw2.discordbot.Events;

import java.awt.Color;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.gw2.discordbot.DiscordBot.Constants;
import com.gw2.discordbot.DiscordBot.DiscordBot;
import com.gw2.discordbot.DiscordBot.UserApi;
import com.gw2.discordbot.HttpParsing.DpsReportApi;
import com.gw2.discordbot.HttpParsing.Gw2Api;
import com.gw2.discordbot.HttpParsing.HttpServerHosting;
import com.gw2.discordbot.Miscellaneous.RandomFunnyQuote;

import kong.unirest.HttpResponse;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.utils.FileUpload;

public class SlashCommandInteraction extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if(!event.isFromGuild()) {
            if(!event.isAcknowledged()) {
                event.deferReply(true).queue(message -> message.sendMessage("Command isn't usable in DMs...").queue());
                return;
            } else return;
        }

        switch(event.getName()) {
            case "ping":
                PING_COMMAND(event);
            break;

            case "help":
                HELP_COMMAND(event);
            break;

            case "get_raid_json":
                GET_RAID_JSON_COMMAND(event);
            break;

            case "profile":
                PROFILE_COMMAND(event);
            break;

            case "resetslashcommands":
                RESET_SLASH_COMMANDS(event);
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

            case "startserver":
                START_SERVER_EVENT(event);
            break;

            case "stopserver":
                STOP_SERVER_EVENT(event);
            break;

            case "calculate_time":
                CALCULATE_TIME(event);
            break;
        }
    }

    private void CALCULATE_TIME(SlashCommandInteractionEvent event) {

        event.deferReply(false).queue();

        Integer day = event.getOption("day").getAsInt();
        Integer month = event.getOption("month").getAsInt();
        Integer year = event.getOption("year").getAsInt();
        Integer hour = event.getOption("hour").getAsInt();
        Integer minute = event.getOption("minute").getAsInt();
        String timezone = event.getOption("timezone").getAsString();

        if(timezone.contains("+")) {

            Integer plus = Character.getNumericValue(timezone.charAt(timezone.indexOf("+") + 1));

            if(hour - plus < 0) {
                hour = (hour - plus) + 24;
            } else hour = hour - plus;

            timezone = timezone.substring(0, timezone.length() - 2);
        } 

        String dateString = ((day < 10) ? ("0" + day) : day)+ "-" + month + "-" + year + " " + hour + ":" + minute + ":00" + " ";
        dateString += timezone;

        System.out.println(dateString);

        SimpleDateFormat f1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss zzz", Locale.getDefault());

        try {

            Date date = f1.parse(dateString);

            Long millis = date.getTime() / 1000L;

            String string = "<t:" + millis + ":F>";

            event.getHook().sendMessage(string + "\n`" + string + "`").queue();

        } catch (ParseException e) {
            event.getHook().sendMessage("Parsing error. Probably inserted wrong time?").queue();
            e.printStackTrace();
        }
    }

    private void STOP_SERVER_EVENT(SlashCommandInteractionEvent event) {

        event.deferReply(true).queue();
        event.getHook().sendMessage("Stopped the server.").queue();

        HttpServerHosting.stopServer();

    }

    private void START_SERVER_EVENT(SlashCommandInteractionEvent event) {

        event.deferReply(true).queue();
        
        if(HttpServerHosting.activateServer()) {
            event.getHook().sendMessage("Activated the server port.").queue();
        } else {
            event.getHook().sendMessage("Couldn't activate the server port...").queue();
        }

    }

    private void GET_RAID_JSON_COMMAND(SlashCommandInteractionEvent event) {

        File file = new File("weeklyStaticLogging.json");

        if(!file.exists()) {
            event.reply("The file is non existant...").queue();
            return;
        }

        event.replyFiles(FileUpload.fromData(file)).queue();
    }

    private void ANNOUNCE_EVENT(SlashCommandInteractionEvent event) {
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

    private void INVITE_EVENT(SlashCommandInteractionEvent event) {

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

    private void CONTACT_DEVELOPER(SlashCommandInteractionEvent event) {
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

    private void TEST_COMMAND(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        // String string = event.getOption("longstringofbosses").getAsString();
        // String[] linksOfBosses = string.split("\n");
        // Boss[] bosses = Boss.getBossArrayFromLinks(linksOfBosses);
        // event.getHook().sendMessage(Arrays.toString(bosses)).queue();
    }

    private void SHUTDOWN_EVENT(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();
        event.getHook().sendMessage("`Shutting down in 5 seconds...`").queueAfter(5, TimeUnit.SECONDS, message -> {
            event.getJDA().shutdown();
            
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.exit(0);
        });
    }

    private void API_STATUS(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("API STATUS");
        eb.setDescription("Returns the current API availability.");
        eb.setThumbnail(Constants.gw2LogoNoBackground);
        eb.setFooter(RandomFunnyQuote.getFunnyQuote(), Constants.gw2LogoNoBackground);

        event.getHook().sendMessageEmbeds(Constants.loadingEmbedBuilder).queue();

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

        event.getHook().editOriginalEmbeds(eb.build()).queue();
    }

    private void PURGE_COMMAND(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();
		
		Integer numberOfMessages = event.getOption("number").getAsInt();

        System.out.println(numberOfMessages);

        if(numberOfMessages > 100) {
            event.getHook().sendMessage("`Unfortunately. Max messages purged at once can be 100.`").queue();
            return;
        }

        if(numberOfMessages < 1) {
            event.getHook().sendMessage("`Unfortunately. Minimum messages purged at once can be 1.`").queue();
            return;
        }

		TextChannel channel = event.getChannel().asTextChannel();

		List<Message> listOfMessages = channel.getHistory().retrievePast(numberOfMessages).complete();
        channel.purgeMessages(listOfMessages);

        event.getHook().sendMessage("Successfully purged " + numberOfMessages + " message(s)!").queue();
    }

    private void RESET_SLASH_COMMANDS(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();
        DiscordBot.jda.updateCommands().queue();

        for(SlashCommandData data : DiscordBot.commandData) {
            // data.addToGuild(event.getJDA().getGuildById("1007915730928418856"));
            data.addGlobally(DiscordBot.jda);

            event.getUser().openPrivateChannel().queue(channel -> {
                channel.sendMessage("Added command `" + data.getCommandName() + "`.").queue();
            });
        }

        SlashCommandData.insertIntoJson(DiscordBot.commandData);
        event.getHook().sendMessage("I have reset the slash commands of this server, per your request.").queue();
    }

    private void PROFILE_COMMAND(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        if(!event.isFromGuild()) {
            event.getHook().sendMessage("Unfortunately, this is only usable in servers!").queue();
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.pink);
        eb.setTitle(event.getUser().getAsTag());
        eb.setThumbnail(event.getUser().getEffectiveAvatarUrl());
        eb.setFooter(RandomFunnyQuote.getFunnyQuote(), Constants.gw2LogoNoBackground);

        eb.addField("USER", event.getUser().getAsTag(), true);
        eb.addField("DAY JOINED", 
            event.getMember().getTimeJoined().getDayOfMonth() + "/" + 
            event.getMember().getTimeJoined().getMonth() + "/" + 
            event.getMember().getTimeJoined().getYear(), 
            true);
        eb.addField("ID", event.getMember().getId(), true);
        eb.addField("ACTIVITY", event.getMember().getOnlineStatus().name(), true);

        ArrayList<String> arrayOfRoleNames = new ArrayList<>();

        for(Role role : event.getMember().getRoles()) {
            arrayOfRoleNames.add(role.getName());
        }

        eb.addField("ROLES", arrayOfRoleNames.toString(), true);

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

    private void HELP_COMMAND(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        if(HelpButtonEventListener.embedBuilders.size() != 1) {

            for(Object o : event.getJDA().getRegisteredListeners()) {
                if(o instanceof HelpButtonEventListener) {
                    event.getJDA().removeEventListener(o);
                }
            }

            event.getJDA().addEventListener(new HelpButtonEventListener());

            event.getHook().sendMessageEmbeds(HelpButtonEventListener.embedBuilders.get(0).build()).addActionRow(
                Button.primary("left", "\u2B05\uFE0F \u2B05\uFE0F").asDisabled(),
                Button.primary("right", "\u27A1\uFE0F \u27A1\uFE0F")
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

    private void PING_COMMAND(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        long ping = DiscordBot.jda.getRestPing().complete();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.pink);     
    
        event.getHook().sendMessage("`PING CALCULATED...`").queue(message -> {
            eb.addField("\uD83E\uDD16 BOT PING \uD83E\uDD16", ping + " ms.", true);
            eb.addField("\uD83E\uDD16 WEBSOCKET PING \uD83E\uDD16", event.getJDA().getGatewayPing() + " ms.", true);
            message.editMessage("`BEEP BOOP, PING CALCULATED...`").setEmbeds(eb.build()).queueAfter(2, TimeUnit.SECONDS);
        });
    }
}
