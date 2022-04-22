package com.example.appenglish_nouns.controller;

import com.example.appenglish_nouns.model.Noun;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

public class MainController {
    @FXML
    TextField txtInEnglsh, txtInRussian, txtInGroup;
    @FXML
    Button btnStart, btnCheck, btnFinish, btnAddNoun, btnGetList;
    @FXML
    Label lblInput;
    @FXML
    TextArea txtAreaOutput, txtAreaListWords;

    Noun actualNoun;
    ArrayList<Noun> arrayNouns;
    Connection conn;

    public void initialize() {
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/EnglishApp", "postgres", "123");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        /*if (conn != null) {
            System.out.println("Connected to the database!");
        } else {
            System.out.println("Failed to make connection!");
        }*/
        runSelect(conn);
        getNoun(arrayNouns);
        /*try (Connection conn = DriverManager
                .getConnection("jdbc:postgresql://127.0.0.1:5432/EnglishApp", "postgres", "123")) {
            if (conn != null) {
                System.out.println("Connected to the database!");
            } else {
                System.out.println("Failed to make connection!");
            }

            runSelect(conn);
            getNoun(arrayNouns);

        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    public void runSelect(Connection conn) {
        try {
            arrayNouns = new ArrayList<>();
            String execute = "SELECT * FROM nouns";
            PreparedStatement statement = conn.prepareStatement(execute);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                arrayNouns.add(new Noun(rs.getInt("id"),
                        rs.getString("in_english"),
                        rs.getString("in_russian"),
                        rs.getInt("in_group")));
            }
        } catch (SQLException e) {
            System.out.println("select ERROR: " + e.getMessage());
        }
    }

    public void runInsert(Connection conn, Noun newNoun) {
        if (txtInGroup.getText() == null) {
            try {
                String execute = "INSERT INTO nouns(in_english, in_russian)  " +
                        "VALUES( \'" + newNoun.inEnglish + "\', \'" + newNoun.inRussian + ");";
                PreparedStatement statement = conn.prepareStatement(execute);
                statement.execute();
            } catch (SQLException e) {
                System.out.println("insert ERROR: " + e.getMessage());
            }
        }
        else {
            try {
                String execute = "INSERT INTO nouns(in_english, in_russian, in_group)  " +
                        "VALUES( \'" + newNoun.inEnglish + "\', \'" + newNoun.inRussian + "\', " + newNoun.inGroup + ");";
                PreparedStatement statement = conn.prepareStatement(execute);
                statement.execute();
            } catch (SQLException e) {
                System.out.println("insert ERROR: " + e.getMessage());
            }
        }
    }

    public void getNoun(ArrayList<Noun> listNouns) {
        actualNoun = listNouns.get(new Random().nextInt(0, listNouns.size() - 1));
    }

    public void onButtonStartClick() {
        lblInput.setText(actualNoun.inEnglish);
    }

    public void onButtonCheckClick() {
        if (txtAreaOutput.getText().toLowerCase().equals(actualNoun.inRussian.toLowerCase())) {
            getNoun(arrayNouns);
            lblInput.setText(actualNoun.inEnglish);
            txtAreaOutput.clear();
        }
    }

    public void onButtonFinishClick() {
    }

    public void onButtonAddNounClick() {
        Noun addNoun;
        if (txtInGroup.getText() == null)
            addNoun = new Noun(txtInEnglsh.getText(), txtInRussian.getText());
        else
            addNoun = new Noun(txtInEnglsh.getText(), txtInRussian.getText(), Integer.parseInt(txtInGroup.getText()));
        runInsert(conn, addNoun);
    }

    public void onButtonGetListClick(ActionEvent actionEvent) {
        runSelect(conn);
        txtAreaListWords.clear();
        for (Noun n: arrayNouns) {
            txtAreaListWords.appendText(n.toString() + "\n");
        }
    }
}