package com.gw2.discordbot;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;

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

    public static CompletableFuture<HttpResponse<String>> UPLOAD_FILE(File fileTest) {
        try {
            return Unirest.post("https://dps.report/uploadContent?json=1&generator=ei")
                .field("file", fileTest)
                .asStringAsync();

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

    public static CompletableFuture<HttpResponse<String>> GET_ELITE_INSIGHTS_RESPONSE(String logPermaLink) {
        try {
            return Unirest.get("https://dps.report/getJson")
            .queryString("permalink", logPermaLink)
            .asStringAsync();

        } catch(UnirestException e) {
            return null;
        }
    }
}
