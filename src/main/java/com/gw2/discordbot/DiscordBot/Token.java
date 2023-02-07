package com.gw2.discordbot.DiscordBot;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class Token {
	private String tokenName;
	private String tokenValue;

	public Token(String string, String string2) {
		this.tokenName = string;
		this.tokenValue = string2;
	}
		
	public String getTokenName() {
		return tokenName;
	}

	public String getTokenValue() {
		return tokenValue;
	}

	public void setTokenName(String tokenName) {
		this.tokenName = tokenName;
	}

	public void setTokenValue(String tokenValue) {
		this.tokenValue = tokenValue;
	}

	@Override
    public String toString() {
		return "[" + tokenName + " : " + tokenValue + "]";
	}

    public static String getLoginToken() {
        try (Reader reader = new FileReader(new File(new File("jsonFolder"), "token.json"))) {

			Gson gson = new GsonBuilder()
						 .disableHtmlEscaping()
						 .setFieldNamingStrategy(FieldNamingPolicy.UPPER_CAMEL_CASE)
						 .setPrettyPrinting()
						 .serializeNulls()
						 .create();
	
			Type founderTypeSet = new TypeToken<ArrayList<Token>>(){}.getType();
			ArrayList<Token> tokens = gson.fromJson(reader, founderTypeSet);
	
			for(Token token : tokens) {
				if(token.getTokenName().equals("loginToken")) {
					Logging.LOG(Token.class, "loginToken gotten from JSON file: " + token);
					return token.getTokenValue();
				}
			}
	
		 } catch (IOException e) {

			Scanner scanner = new Scanner(System.in);

			System.out.println("Enter your login token: ");
			String loginToken = scanner.next();
			System.out.println("Enter your dps.report token (or write \"default\" for default token): ");
			String dpsReportToken = scanner.next();

			dpsReportToken = dpsReportToken.toLowerCase();

			if(dpsReportToken.length() < 5 || dpsReportToken.length() > 30) {
				dpsReportToken = "default";
			}

			scanner.close();
			ArrayList<Token> loginTokenObj = new ArrayList<Token>();
			loginTokenObj.add(new Token("loginToken", loginToken));
			loginTokenObj.add(new Token("dpsReportsToken", dpsReportToken));

			try(FileWriter writer = new FileWriter(new File(new File("jsonFolder"), "token.json"))) {

				Gson gson = new GsonBuilder()
						 .disableHtmlEscaping()
						 .setFieldNamingStrategy(FieldNamingPolicy.UPPER_CAMEL_CASE)
						 .setPrettyPrinting()
						 .serializeNulls()
						 .create();

				writer.write(gson.toJson(loginTokenObj));

				writer.close();
				Logging.LOG(Token.class, "loginToken gotten from JSON file: " + loginToken);
				return loginToken;

			} catch(IOException e1) {
				System.out.println("Error in opening the file.");
				return null;
			}
		 }
		return null;
    }

	public static String getSignupForm() {
		try (Reader reader = new FileReader(new File(new File("jsonFolder"), "token.json"))) {

			Gson gson = new GsonBuilder()
						 .disableHtmlEscaping()
						 .setFieldNamingStrategy(FieldNamingPolicy.UPPER_CAMEL_CASE)
						 .setPrettyPrinting()
						 .serializeNulls()
						 .create();
	
			Type founderTypeSet = new TypeToken<Token[]>(){}.getType();
			Token[] tokens = gson.fromJson(reader, founderTypeSet);
	
			for(Token token : tokens) {
				if(token.getTokenName().equals("staticSignupForm")) {
					return token.getTokenValue();
				}
			}


	
		 } catch (IOException e) {
			 Map<String, String> map = System.getenv();
	
			 if(map.containsKey("token")) {
				Logging.LOG(Token.class, "Token gotten from OS env. variable: " + map.get("token"));
				return map.get("token");
			 } else return null;
		 }
		return null;	
	}

	public static void writeNewTokens(Token[] tokens) {
		try(FileWriter writer = new FileWriter(new File(new File("jsonFolder"), "token.json"))) {

			Gson gson = new GsonBuilder()
					 .disableHtmlEscaping()
					 .setFieldNamingStrategy(FieldNamingPolicy.UPPER_CAMEL_CASE)
					 .setPrettyPrinting()
					 .serializeNulls()
					 .create();

			writer.write(gson.toJson(tokens));

			writer.close();

		} catch(IOException e1) {
			System.out.println("Error...");
		}
	}

	public static ArrayList<Token> readCurrentlyAddedTokens() {
		try (Reader reader = new FileReader(new File(new File("jsonFolder"), "token.json"))) {

			Gson gson = new GsonBuilder()
						 .disableHtmlEscaping()
						 .setFieldNamingStrategy(FieldNamingPolicy.UPPER_CAMEL_CASE)
						 .setPrettyPrinting()
						 .serializeNulls()
						 .create();
	
			Type founderTypeSet = new TypeToken<ArrayList<Token>>(){}.getType();
			ArrayList<Token> tokens = gson.fromJson(reader, founderTypeSet);

			return tokens;
		} catch(IOException e) {
			System.out.println("Error accessing file.");

			return null;
		}
	}
}
