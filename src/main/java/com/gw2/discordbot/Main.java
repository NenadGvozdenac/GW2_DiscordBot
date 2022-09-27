package com.gw2.discordbot;

import javax.security.auth.login.LoginException;

public class Main {
  
    public static void main(String[] args) throws LoginException, InterruptedException {		

        new DiscordBot();

        RssReaderClass readAnnouncements = new RssReaderClass();
        readAnnouncements.ReadNewsFromSite();
        readAnnouncements.ReadNewsFromForums();
        
        DailyAchievements achievementsReader = new DailyAchievements();
        achievementsReader.ReadFractalsFromApi();

        Logging.LOG(Main.class, "Bot successfully started!");
    }
}
