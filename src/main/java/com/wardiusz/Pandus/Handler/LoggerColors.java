package com.wardiusz.Pandus.Handler;

public class LoggerColors {
    public static final LoggerColors green = new LoggerColors("\u001B[37m");
    public static final LoggerColors GREEN = green;
    private String color;

    LoggerColors(String color) {
        this.color = color;
    }
    public String textColor(String text) {
        return color + text + "\u001B[0m";
    }
}
