package com.gw2.discordbot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RaidDay {
    
    Integer id;
    String date;

    List<Boss> bosses;

    public RaidDay() {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");  
        LocalDateTime now = LocalDateTime.now();  

        this.id = new Random().nextInt(500);
        this.date = dtf.format(now);

        bosses = new ArrayList<>();
    }
}
