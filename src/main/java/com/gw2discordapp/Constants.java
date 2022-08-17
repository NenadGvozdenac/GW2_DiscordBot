package com.gw2discordapp;

import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class Constants {

    public static final DecimalFormat df = new DecimalFormat("0.00");

    public static String commanderIconEmoji = "<:Commander:1008363131904466975>";
    public static String fractalRelicIconEmoji = "<:Fractal_Relic:1008365970177400842>";
    public static String achievementPointIconEmoji = "<:Achievement_Point:1008366225891536966>";
    public static String wvwIconEmoji = "<:WvW_Castle:1008366636983660554>";
    public static String goldIconEmoji = "<:Gold:1008370020516114563>";
    public static String karmaIconEmoji = "<:Karma:1008370018641260614>";
    public static String gemIconEmoji = "<:Gems:1008370017106141344>";
    public static String fractalIconEmoji = "<:Fractals:1008640406373810237>";
    public static String gw2IconEmoji = "<:gw2:1009081342916571196>";
    public static String gw2LogoNoBackground = "https://cdn.discordapp.com/attachments/1007917782601572352/1009068839574716426/kisspng-guild-wars-2-heart-of-thorns-guild-wars-2-path-o-5b37c7ef657558.1878307415303823194156.png";
    public static String loadingIconImage = "https://emoji.discord.st/emojis/8104LoadingEmote.gif";

    public static String guildID = "1007915730928418856";
    public static String newsChannelID = "1007916728648478730";

    public static Map<String, String> specializationEmojis;

    static {
        specializationEmojis = new HashMap<String, String>();
        specializationEmojis.put("Elementalist", "<:Elementalist:1008349977778606091>");
        specializationEmojis.put("Engineer", "<:Engineer:1008349988683776041>");
        specializationEmojis.put("Guardian", "<:Guardian:1008349986204954634>");
        specializationEmojis.put("Mesmer", "<:Mesmer:1008349980899164210>");
        specializationEmojis.put("Necromancer", "<:Necromancer:1008349993456906260>");
        specializationEmojis.put("Ranger", "<:Ranger:1008341643520397382>");
        specializationEmojis.put("Revenant", "<:Revenant:1008349987949793290>");
        specializationEmojis.put("Thief", "<:Thief:1008349990806102046>");
        specializationEmojis.put("Warrior", "<:Warrior:1008349995512103084>");
    }

    public static HashMap<String, String> bossesNamesGW2;

    static {
        bossesNamesGW2 = new HashMap<String, String>();
        bossesNamesGW2.put("vale_guardian", "Vale Guardian");
        bossesNamesGW2.put("spirit_woods", "Spirit Woods");
        bossesNamesGW2.put("gorseval", "Gorseval");
        bossesNamesGW2.put("sabetha", "Sabetha");

        bossesNamesGW2.put("slothasor", "Slothasor");
        bossesNamesGW2.put("bandit_trio", "Bandit Trio");
        bossesNamesGW2.put("matthias", "Matthias");

        bossesNamesGW2.put("escort", "Escort");
        bossesNamesGW2.put("keep_construct", "Keep Construct");
        bossesNamesGW2.put("twisted_castle", "Twisted Castle");
        bossesNamesGW2.put("xera", "Xera");

        bossesNamesGW2.put("cairn", "Cairn");
        bossesNamesGW2.put("mursaat_overseer", "Mursaat Overseer");
        bossesNamesGW2.put("samarog", "Samarog");
        bossesNamesGW2.put("deimos", "Deimos");

        bossesNamesGW2.put("soulless_horror", "Soulless Horror");
        bossesNamesGW2.put("river_of_souls", "River of Souls");
        bossesNamesGW2.put("statues_of_grenth", "Statues of Grenth");
        bossesNamesGW2.put("voice_in_the_void", "Dhuum");

        bossesNamesGW2.put("conjured_amalgamate", "Conjured Amalgamate");
        bossesNamesGW2.put("twin_largos", "Twin Largos");
        bossesNamesGW2.put("qadim", "Qadim");

        bossesNamesGW2.put("gate", "Gate");
        bossesNamesGW2.put("adina", "Adina");
        bossesNamesGW2.put("sabir", "Sabir");
        bossesNamesGW2.put("qadim_the_peerless", "Qadim the Peerless");
    }

    public static Token DPS_REPORT_TOKEN;

    static {
        try (FileReader reader = new FileReader(new File(new File("jsonFolder"), "token.json"))) {
            Gson gson = new GsonBuilder()
                     .disableHtmlEscaping()
                     .setFieldNamingStrategy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                     .setPrettyPrinting()
                     .serializeNulls()
                     .create();

            Type founderTypeSet = new TypeToken<Token[]>(){}.getType();
            Token[] tokens = gson.fromJson(reader, founderTypeSet);

            DPS_REPORT_TOKEN = null;

            for(Token token : tokens) {
                if(token.getTokenName().equals("dpsReportsToken")) {
                    DPS_REPORT_TOKEN = token;
                    break;
                }
            }

            Main.mainLogger.info("Gotten DPSREPORTTOKEN from .JSON file: " + DPS_REPORT_TOKEN);

        } catch(IOException e) {
            Map<String, String> envVariables = System.getenv();

            DPS_REPORT_TOKEN = null;

            if(envVariables.containsKey("dps_reports_token")) {
                String tokenValue = envVariables.get("dps_reports_token");
                String tokenName = "dpsReportToken";
                DPS_REPORT_TOKEN = new Token(tokenName, tokenValue);
            }

            Main.mainLogger.info("Gotten DPSREPORTTOKEN from env. variable: " + DPS_REPORT_TOKEN);
        }
    }

    public static MessageEmbed loadingEmbedBuilder;

    static {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.yellow);
        eb.setTitle("`LOADING INFO...`");
        eb.setDescription("`THANK YOU FOR WAITING...`");
        eb.setThumbnail(Constants.loadingIconImage);
        eb.setFooter("[GW2 Raid Static]", Constants.gw2LogoNoBackground);
        loadingEmbedBuilder = eb.build();
    }
}