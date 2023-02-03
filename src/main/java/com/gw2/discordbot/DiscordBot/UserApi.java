package com.gw2.discordbot.DiscordBot;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class UserApi {
    private String username;
    private String userId;
    private String apiKey;

    public UserApi(String username, String userId, String apiKey) {
        this.username = username;
        this.apiKey = apiKey;
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getUserId() {
        return userId;
    }

    public static UserApi GET_API_INFO(String id) {

        try (FileReader reader = new FileReader(new File(new File("jsonFolder"), "api.json"))) {
        
            Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .setFieldNamingStrategy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .serializeNulls()
                .create();

            Type founderTypeSet = new TypeToken<List<UserApi>>(){}.getType();
            List<UserApi> listUserApi = gson.fromJson(reader, founderTypeSet);

            for(UserApi api : listUserApi) {
                if(api.getUserId().equals(id)) {
                    return api;
                }
            }

            return null;
        } catch(IOException e) {
            return null;
        }
    }
}
