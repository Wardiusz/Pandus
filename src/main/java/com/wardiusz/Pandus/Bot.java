package com.wardiusz.Pandus;

import com.wardiusz.Pandus.commands.DTO.MyDatabase;
import net.dv8tion.jda.api.JDA;

import java.sql.Connection;
import java.sql.SQLException;

public class Bot {
    private static JDA jda;

    public static JDA getJDA() {
        return jda;
    }

    static void main() throws SQLException, InterruptedException {
        try (Connection connection = MyDatabase.getConnection()) {
            connection.setAutoCommit(false);
        }
        Provider provider = new Provider();
        jda = provider.setupJDA();
    }
}