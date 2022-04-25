package com.example.appenglish_nouns.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SQLrequest {
    public static ArrayList<Group> arrayGroups;
    public static ArrayList<Noun> arrayNouns;
    public static void runSQLSelectGroups(Connection conn) {
        try {
            arrayGroups = new ArrayList<>();
            String request = "SELECT * FROM groups";
            PreparedStatement statement = conn.prepareStatement(request);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                arrayGroups.add(new Group(rs.getInt("id"),
                        rs.getString("name")));
            }
        } catch (SQLException e) {
            System.out.println("select ERROR: " + e.getMessage());
        }
    }

    public static void runSQLSelectNouns(Connection conn) {
        try {
            arrayNouns = new ArrayList<>();
            String request = "SELECT nouns.id AS n_id, in_english, in_russian, groups.id AS g_id, groups.name AS g_name  " +
                    "FROM nouns " +
                    "JOIN groups ON nouns.in_group = groups.id";
            PreparedStatement statement = conn.prepareStatement(request);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                arrayNouns.add(new Noun(rs.getInt("n_id"),
                        rs.getString("in_english"),
                        rs.getString("in_russian"),
                        rs.getInt("g_id"),
                        rs.getString("g_name")));
            }
        } catch (SQLException e) {
            System.out.println("select ERROR: " + e.getMessage());
        }
    }
    public static void runSQLInsertNoun(Connection conn, Noun newNoun) {
        try {
            String execute = "INSERT INTO nouns(in_english, in_russian, in_group)  " +
                    "VALUES( \'" + newNoun.inEnglish + "\', \'" + newNoun.inRussian + "\', " + newNoun.inGroup + ");";
            PreparedStatement statement = conn.prepareStatement(execute);
            statement.execute();
        } catch (SQLException e) {
            System.out.println("insert ERROR: " + e.getMessage());
        }
    }
    public static void runSQLInsertGroup(Connection conn, Group newGroup) {
        try {
            String execute = "INSERT INTO groups(name)  " +
                    "VALUES( \'" + newGroup.name + "\');";
            PreparedStatement statement = conn.prepareStatement(execute);
            statement.execute();
        } catch (SQLException e) {
            System.out.println("insert ERROR: " + e.getMessage());
        }
    }
}
