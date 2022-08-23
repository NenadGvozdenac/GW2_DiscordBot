package com.gw2.discordbot;

import net.dv8tion.jda.api.interactions.commands.OptionType;

public class Option {

    private OptionType optionType;
    private String optionName;
    private String optionDescription;
    private Boolean mandatoryOption;

    Option() {

    }

    Option(OptionType oT, String oN, String oD, Boolean mD) {
        this.optionType = oT;
        this.optionName = oN;
        this.optionDescription = oD;
        this.mandatoryOption = mD;
    }

    public OptionType getOptionType() {
        return optionType;
    }

    public String getOptionName() {
        return optionName;
    }

    public String getOptionDesc() {
        return optionDescription;
    }

    public boolean getMandatoryOption() {
        return mandatoryOption;
    }
}