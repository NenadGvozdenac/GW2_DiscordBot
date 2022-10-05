package com.gw2.discordbot;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
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
	
			Type founderTypeSet = new TypeToken<Token[]>(){}.getType();
			Token[] tokens = gson.fromJson(reader, founderTypeSet);
	
			for(Token token : tokens) {
				if(token.getTokenName().equals("loginToken")) {
					Logging.LOG(Token.class, "Token gotten from JSON file: " + token);
					return token.getTokenValue();
				}
			}
	
		 } catch (IOException e) {
			try(FileWriter writer = new FileWriter(new File(new File("jsonFolder"), "token.json"))) {
				
				System.out.println("Enter your token: ");
				Scanner scanner = new Scanner(System.in);
				
				String token = scanner.next();

				Gson gson = new GsonBuilder()
						 .disableHtmlEscaping()
						 .setFieldNamingStrategy(FieldNamingPolicy.UPPER_CAMEL_CASE)
						 .setPrettyPrinting()
						 .serializeNulls()
						 .create();

				Token loginToken = new Token("loginToken", token);

				writer.write(gson.toJson(loginToken));

				writer.close();
				scanner.close();

			} catch(IOException e1) {
				System.out.println("Error...");
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
			 }
		 }
		return null;	
	}
}
