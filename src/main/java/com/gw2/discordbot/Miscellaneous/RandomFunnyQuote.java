package com.gw2.discordbot.Miscellaneous;

import java.util.ArrayList;
import java.util.Random;

public class RandomFunnyQuote {
    
    public static ArrayList<String> listOfFunnyFooters;
    public static Random random;

    static {
        random = new Random(0);

        listOfFunnyFooters = new ArrayList<String>() {
            {
                add("Thank you for using [BA] Helper!");
                add("Only used in [BA] server!");
                add("Funny quote eh.");
                add("Made by NenadG");
                add("List commands with /help");
                add("Raid helper!");
                add("Don't try anything sus.");
                add("Thank you airisu for making me do this.");
                add("No Globe, you can't soloheal push Desmina...");
                add("Any bugs? Smash em.");
                add("Seriously, any bugs, feel free to report.");
                add("Firebrand who?");
            }
        };
    }

    public static String getFunnyQuote() {
        Integer number = random.nextInt(listOfFunnyFooters.size());

        return listOfFunnyFooters.get(number);
    }
}
