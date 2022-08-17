package com.gw2discordapp;

import java.io.File;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class DpsReportApi {

    public static HttpResponse<String> GET_INFORMATION_REQUEST() {
        try {
            return Unirest.get("https://dps.report/getUploads?json=1&amp;generator=ei")
            .queryString("userToken", Constants.DPS_REPORT_TOKEN)
            .asString();
        } catch (UnirestException e) {
            return null;
        }
    }

    public static HttpResponse<String> UPLOAD_FILE(File fileTest) {
        try {
            return Unirest.post("https://dps.report/uploadContent?json=1&generator=ei")
                .field("file", fileTest)
                .asString();
        } catch (UnirestException e) {
            return null;
        }
    }

    public static HttpResponse<String> GET_TOKEN() {
        try {
            return Unirest.get("https://dps.report/getUserToken")
                .asString();
        } catch(UnirestException e) {
            return null;
        }
    }

    public static HttpResponse<String> GET_ELITE_INSIGHTS_RESPONSE(String logPermaLink) {
        try {
            return Unirest.get("https://dps.report/getJson")
                .queryString("permalink", logPermaLink)
                .asString();
        } catch(UnirestException e) {
            return null;
        }
    }
}
