package com.gw2.discordbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

            HashMap<String, List<String>> listOfBossesAndWings = new HashMap<>(){
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

            HashMap<String, String> listOfEmojisAndBosses = new HashMap<>(){
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

            for(Map.Entry<String, String> entry : listOfEmojisAndBosses.entrySet()) {
                if(bossLogName.contains(entry.getKey())) {
                    this.emoji = entry.getValue();
                    break;
                }
            }

            for(Map.Entry<String, List<String>> entry : listOfBossesAndWings.entrySet()) {
                for(String boss : entry.getValue()) {
                    if(bossLogName.contains(boss)) {
                        this.wingName = entry.getKey();
                        return;
                    }
                }
            }
    }

    @Override
    public String toString() {
        return this.bossName + "::" +this.dpsReportLink;
    }
}
