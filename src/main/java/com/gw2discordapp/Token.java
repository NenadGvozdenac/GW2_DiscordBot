package com.gw2discordapp;

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
}
