package com.wardiusz.Pandus.commands.DTO;

import com.wardiusz.Pandus.Handler.Config;

public enum ConfigOptions {
    TOKEN(Config.get("TOKEN")),
    USE_MYSQL("false"),
    DB_HOST(Config.get("DB_HOST")),
    DB_PORT(Config.get("DB_PORT")),
    DB_NAME(Config.get("DB_NAME")),
    DB_USER(Config.get("DB_USER")),
    DB_PASSWORD(Config.get("DB_PASSWORD")),
    MUSIC_BOT(Config.get("MUSIC_BOT")),
    PREFIX_COMMANDS(Config.get("PREFIX_COMMANDS")),
    WELCOME_MSG(Config.get("DEFAULT_WELCOME_MSG")),
    GOODBYE_MSG(Config.get("DEFAULT_GOODBYE_MSG"));

    private String value;

    ConfigOptions(String value){
        this.value = value;
    }

    public void setValue(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
