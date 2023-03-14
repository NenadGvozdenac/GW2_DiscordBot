package com.gw2.discordbot.DiscordBot;

import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.gw2.discordbot.Miscellaneous.RandomFunnyQuote;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class Constants {

    public static final DecimalFormat df = new DecimalFormat("0.00");

    public static final String commanderIconEmoji = "<:Commander:1008363131904466975>";
    public static final String fractalRelicIconEmoji = "<:Fractal_Relic:1008365970177400842>";
    public static final String achievementPointIconEmoji = "<:Achievement_Point:1008366225891536966>";
    public static final String wvwIconEmoji = "<:WvW_Castle:1008366636983660554>";
    public static final String goldIconEmoji = "<:Gold:1008370020516114563>";
    public static final String karmaIconEmoji = "<:Karma:1008370018641260614>";
    public static final String gemIconEmoji = "<:Gems:1008370017106141344>";
    public static final String fractalIconEmoji = "<:Fractals:1008640406373810237>";
    public static final String gw2IconEmoji = "<:gw2:1009081342916571196>";
    public static final String gw2LogoNoBackground = "https://cdn.discordapp.com/attachments/1007924091015143474/1010499402735030332/kisspng-guild-wars-2-heart-of-thorns-guild-wars-2-path-o-5b37c7ef657558.1878307415303823194156.png";
    public static final String loadingIconImage = "https://emoji.discord.st/emojis/8104LoadingEmote.gif";

    public static final String guildID = "1007915730928418856";
    public static final String newsChannelID = "1007916728648478730";
    public static final String dailyAchievementsChannelID = "1009751860258820136";
    public static final String patchNotesChannelID = "1007916992197558282";

    public static final String generalLogUploadsChannelID = "1009064617038843914";
    public static final String raidStaticLogUploadsChannelID = "1007917782601572352";
    public static final String announcementChannelID = "1011718027080904725";
    public static final String raidStaticAnnouncementChannelID = "1007917722413322300";
    public static final String raidStaticChatChannelID = "1007917741711294504";
    public static final String raidStaticApplicationsChannelID = "1013186195376832522";

    public static final String strikeStaticInfoID = "1084871545400336435";

    public static final String QTP_FIRES = "https://cdn.discordapp.com/attachments/1007924091015143474/1012710546224709692/download_1.png";

    public static final String staticRoleID = "1007918310190501948";
    public static final String staticApplicantRoleID = "1013185863116660838";
    public static final String staticBackupRoleID = "1013696001023942656";

    public static final String strikeStaticRoleID = "1084865852467327066";
    public static final String strikeStaticBackupID = "1084956671455010906";

    public static final Map<String, String> specializationEmojis;

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

    public static final HashMap<String, ArrayList<String>> listOfBossesAndWings = new HashMap<>(){
        {
            put("Spirit Vale", new ArrayList<String>() {
                {
                    add("Vale Guardian");
                    add("Gorseval the Multifarious");
                    add("Sabetha the Saboteur");
                }
            });

            put("Salvation Pass", new ArrayList<String>() {
                {
                    add("Slothasor");
                    add("Bandit Trio");
                    add("Matthias Gabrel");
                }
            });

            put("Stronghold of the Faithful", new ArrayList<String>() {
                {
                    add("McLeod the Silent");
                    add("Keep Construct");
                    add("Twisted Castle");
                    add("Xera");
                }
            });

            put("Bastion of the Penitent", new ArrayList<String>() {
                {
                    add("Cairn");
                    add("Mursaat Overseer");
                    add("Samarog");
                    add("Deimos");
                }
            });

            put("Hall of Chains", new ArrayList<String>() {
                {
                    add("Soulless Horror");
                    add("River of Souls");
                    add("Statue of Ice");
                    add("Statue of Death");
                    add("Statue of Darkness");
                    add("Dhuum");
                }
            });
                                
            put("The Key of Ahdashim", new ArrayList<String>() {
                {
                    add("Cardinal Adina");
                    add("Cardinal Sabir");
                    add("Qadim the Peerless");
                }
            });

            put("Mythwright Gambit", new ArrayList<String>() {
                {
                    add("Conjured Amalgamate");
                    add("Twin Largos");
                    add("Qadim");
                }
            });


            put("Sunqua Peak", new ArrayList<String>() {
                {
                    add("Elemental Ai, Keeper of the Peak");
                    add("Dark Ai, Keeper of the Peak");
                }
            });

            put("Shattered Observatory", new ArrayList<String>() {
                {
                    add("Skorvald");
                    add("Artsariiv");
                    add("Arkk");
                }
            });

            put("Nightmare", new ArrayList<String>() {
                {
                    add("MAMA");
                    add("Nightmare Oratuss");
                    add("Ensolyss of the Endless Torment");
                }
            });

            put("Practice Room", new ArrayList<String>() {
                {
                    add("Standard Kitty Golem");
                    add("Medium Kitty Golem");
                    add("Large Kitty Golem");
                }
            });

            put("Icebrood Saga", new ArrayList<String>() {
                {
                    add("Super Kodan Brothers");
                    add("Fraenir of Jormag");
                    add("Boneskinner");
                    add("Whisper of Jormag");
                    add("Icebrood Construct");
                    add("Cold War");
                    add("Forging Steel");
                }
            });

            put("End of Dragons", new ArrayList<String>() {
                {
                    add("Aetherblade Hideout");
                    add("Ankka");
                    add("Minister Li");
                    add("The Dragonvoid");
                }
            });
        }
    };

    public static final HashMap<String, String> listOfShortNamesOfBosses = new HashMap<>() {
        {
            put("Vale Guardian", "vg");
            put("Gorseval the Multifarious", "gors");
            put("Sabetha the Saboteur", "sab");
            put("Slothasor", "sloth");
            put("Bandit Trio", "trio");
            put("Matthias Gabrel", "matt");
            put("McLeod the Silent", "esc");
            put("Keep Construct", "kc");
            put("Twisted Castle", "tc");
            put("Xera", "xera");
            put("Cairn", "cairn");
            put("Mursaat Overseer", "mo");
            put("Samarog", "sam");
            put("Deimos", "dei");
            put("Soulless Horror", "sh");
            put("River of Souls", "river");
            put("Statue of Ice", "bk");
            put("Statue of Death", "se");
            put("Statue of Darkness", "eyes");
            put("Dhuum", "dhuum");
            put("Conjured Amalgamate", "ca");
            put("Twin Largos", "twins");
            put("Cardinal Adina", "adina");
            put("Cardinal Sabir", "sabir");
            put("Qadim the Peerless", "qpeer");
            put("Qadim", "qadim");
            put("Elemental Ai, Keeper of the Peak", "ai");
            put("Skorvald", "skor");
            put("Artsariiv", "arriv");
            put("Arkk", "arkk");
            put("MAMA", "mama");
            put("Nightmare Oratuss", "siax");
            put("Ensolyss of the Endless Torment", "enso");
        }
    };

    public static final HashMap<String, String> listOfEmojisAndBosses = new HashMap<>(){
        {
            put("Vale Guardian", "<:valeguardian:1027914648361644053>");
            put("Gorseval the Multifarious", "<:gorseval:1027914649825456128>");
            put("Sabetha the Saboteur", "<:sabetha:1027914651075346483>");
            put("Slothasor", "<:slothasor:1027914668053897266>");
            put("Bandit Trio", "<:trio:1027914665394720869>");
            put("Matthias Gabrel", "<:matthias:1027914666875297843>");
            put("McLeod the Silent", "<:escort:1062388796051705956>");
            put("Keep Construct", "<:kc:1027914683996454972>");
            put("Twisted Castle", "<:twistedcastle:1027914681203052585>");
            put("Xera", "<:xera:1027914682641698926>");
            put("Cairn", "<:cairn:1027914701755125840>");
            put("Mursaat Overseer", "<:mursaat:1027914699037229076>");
            put("Samarog", "<:samarog:1027914700396175441>");
            put("Deimos", "<:deimos:1027914697367887942>");
            put("Soulless Horror", "<:soullesshorror:1027914720411406367>");
            put("River of Souls", "<:river:1027914718666575885>");
            put("Statue of Ice", "<:brokenking:1027914722189770863>");
            put("Statue of Death", "<:eaterofsouls:1027914725515874375>");
            put("Statue of Darkness", "<:eyes:1027914717634764851>");
            put("Dhuum", "<:dhuum:1027914723875885087>");
            put("Conjured Amalgamate", "<:ca:1027914735435386931>");
            put("Twin Largos", "<:largos:1027914737046016080>");
            put("Cardinal Adina", "<:adina:1027914748097998969>");
            put("Cardinal Sabir", "<:sabir:1027914746772606976>");
            put("Qadim the Peerless", "<:qtp:1027914749662482492>");
            put("Qadim", "<:qadim:1027914734000939038>");
            put("Elemental Ai, Keeper of the Peak", "<:lightai:1027914790951198750>");
            put("Dark Ai, Keeper of the Peak", "<:darkai:1027914792180121640>");
            put("Skorvald", "<:skorvald:1027914778594783252>");
            put("Artsariiv", "<:artsariiv:1027914781153316894>");
            put("Arkk", "<:arkk:1027914779790151721>");
            put("MAMA", "<:mama:1027914765927989298>");
            put("Nightmare Oratuss", "<:siax:1027914767274360913>");
            put("Ensolyss of the Endless Torment", "<:ensolyss:1027914768717201428>");
            put("Super Kodan Brothers", "<:voiceandclaw:1027914842692124672>");
            put("Fraenir of Jormag", "<:fraenir:1027914839009534002>");
            put("Boneskinner", "<:boneskinner:1027914845540057240>");
            put("Whisper of Jormag", "<:whipserofjormag:1027914844046901299>");
            put("Icebrood Construct", "<:construct:1027914836279046194>");
            put("Cold War", "<:coldwar:1027914847167459348>");
            put("Forging Steel", "<:forgingsteel:1027914837545717780>");
            put("Aetherblade Hideout", "<:maitrin:1027914811713015921>");
            put("Minister Li", "<:ministerli:1027914813424287867>");
            put("Ankka", "<:ankka:1027914815001346088>");
            put("The Dragonvoid", "<:dragonvoid:1027914824862158858>");
        }
    };

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

            for(Token token : tokens) {
                if(token != null && token.getTokenName().equals("dpsReportsToken")) {
                    DPS_REPORT_TOKEN = token;
                    break;
                }
            }

            if(DPS_REPORT_TOKEN == null) {
                System.out.println("Enter your DPS.REPORT token: ");
                Scanner sc = new Scanner(System.in);
                String dpsReportToken = sc.next();
                sc.close();
                DPS_REPORT_TOKEN = new Token("dpsReportsToken", dpsReportToken);
                tokens[1] = DPS_REPORT_TOKEN;
                Token.writeNewTokens(tokens);
            }

            Logging.LOG(Main.class, "DPSREPORTTOKEN gotten from .JSON file: " + DPS_REPORT_TOKEN);

        } catch(IOException e) {
            Map<String, String> envVariables = System.getenv();

            DPS_REPORT_TOKEN = null;

            if(envVariables.containsKey("dps_reports_token")) {
                String tokenValue = envVariables.get("dps_reports_token");
                String tokenName = "dpsReportToken";
                DPS_REPORT_TOKEN = new Token(tokenName, tokenValue);
            }

            Logging.LOG(Main.class, "DPSREPORTTOKEN gotten from env. variable: " + DPS_REPORT_TOKEN);
        }
    }

    public static final MessageEmbed loadingEmbedBuilder;

    static {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.yellow);
        eb.setTitle("`LOADING INFO...`");
        eb.setDescription("`THANK YOU FOR WAITING...`");
        eb.setThumbnail(Constants.loadingIconImage);
        eb.setFooter(RandomFunnyQuote.getFunnyQuote(), Constants.gw2LogoNoBackground);
        loadingEmbedBuilder = eb.build();
    }
}