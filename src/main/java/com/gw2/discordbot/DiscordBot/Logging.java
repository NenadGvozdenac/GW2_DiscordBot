package com.gw2.discordbot.DiscordBot;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Logging {
    @SuppressWarnings("rawtypes")
    public static void LOG(Class classForLogging, String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy HH:mm", Locale.US);    
        Date resultdate = new Date(System.currentTimeMillis());

        Logger logger = LoggerFactory.getLogger(classForLogging.getName());
        logger.info(sdf.format(resultdate.getTime()) + ": " + message);
    }
}