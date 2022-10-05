package com.gw2.discordbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Boss {
    
    String wingName;
    String bossName;
    String killTime;
    String dpsReportLink;
    String bossIcon;
    Boolean isFailed;

    public Boss(String bossLogPermaLink, String bossLogName, String bossIcon2, String bossIsCm, Boolean bossLogSuccess,
            String bossLogTime) {

            this.wingName = "null";

            HashMap<String, List<String>> listOfBossesAndWings = new HashMap<>(){
                {
                    put("Wing One", new ArrayList<String>() {
                        {
                            add("Vale Guardian");
                            add("Gorseval the Multifarious");
                            add("Sabetha the Saboteur");
                        }
                    });

                    put("Wing Two", new ArrayList<String>() {
                        {
                            add("Slothasor");
                            add("Bandit Trio");
                            add("Matthias Gabrel");
                        }
                    });

                    put("Wing Three", new ArrayList<String>() {
                        {
                            add("Siege the Stronghold");
                            add("Keep Construct");
                            add("Xera");
                        }
                    });

                    put("Wing Four", new ArrayList<String>() {
                        {
                            add("Cairn");
                            add("Mursaat Overseer");
                            add("Samarog");
                            add("Deimos");
                        }
                    });

                    put("Wing Five", new ArrayList<String>() {
                        {
                            add("Soulless Horror");
                            add("River of Souls");
                            add("Statue of Ice");
                            add("Statue of Death");
                            add("Statue of Darkness");
                            add("Dhuum");
                        }
                    });

                    put("Wing Six", new ArrayList<String>() {
                        {
                            add("Conjured Amalgamate");
                            add("Twin Largos");
                            add("Qadim");
                        }
                    });

                    put("Wing Seven", new ArrayList<String>() {
                        {
                            add("Cardinal Adina");
                            add("Cardinal Sabir");
                            add("Qadim the Peerless");
                        }
                    });
                }
            };

            listOfBossesAndWings.forEach((wingName, wingBosses) -> {
                wingBosses.forEach(boss -> {
                    if(boss.contains(bossLogName)) {
                        this.wingName = wingName;
                    }
                });
            });

            this.bossName = bossLogName;
            this.bossIcon = bossIcon2;
            this.killTime = bossLogTime;
            this.isFailed = bossLogSuccess;
            this.dpsReportLink = bossLogPermaLink;
    }
}
