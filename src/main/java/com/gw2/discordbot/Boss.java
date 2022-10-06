package com.gw2.discordbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Boss {
    
    String wingName;
    String bossName;
    String killTime;
    String dpsReportLink;
    Boolean isFailed;
    String startTime;
    String endTime;

    public Boss(String bossLogPermaLink, String bossLogName, String bossIsCm, Boolean bossLogSuccess,
            String bossLogTime, String startTime, String endTime) {

            this.wingName = "null";

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
                            add("Siege the Stronghold");
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
                            add("Shiverpeaks Pass");
                            add("Voice of the Fallen and Claw of the Fallen");
                            add("Fraenir of Jormag");
                            add("Boneskinner");
                            add("Whisper of Jormag");
                            add("Cold War");
                        }
                    });

                    put("End of Dragons", new ArrayList<String>() {
                        {
                            add("Aetherblade Hideout");
                            add("Xunlai Jade Junkyard");
                            add("Kaineng Overlook");
                            add("Harvest Temple");
                        }
                    });
                }
            };

            this.bossName = bossLogName;
            this.killTime = bossLogTime;
            this.isFailed = bossLogSuccess;
            this.dpsReportLink = bossLogPermaLink;
            this.startTime = startTime;
            this.endTime = endTime;

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
