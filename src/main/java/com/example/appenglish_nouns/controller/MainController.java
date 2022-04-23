package com.example.appenglish_nouns.controller;

import com.example.appenglish_nouns.model.Noun;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

public class MainController {
    @FXML
    TableView<Noun> tableWords;
    @FXML
    TableColumn<Noun, Integer> idColumn;
    @FXML
    TableColumn<Noun, String> inEnglishColumn;
    @FXML
    TableColumn<Noun, String> inRussianColumn;
    @FXML
    TableColumn<Noun, Integer> inGroupColumn;
    @FXML
    TextField txtInEnglsh, txtInRussian, txtInGroup;
    @FXML
    Button btnStart, btnCheck, btnFinish, btnAddNoun, btnGetList;
    @FXML
    Label lblInput;
    @FXML
    TextArea txtAreaOutput;

    Noun actualNoun;
    ArrayList<Noun> arrayNouns;
    ObservableList<Noun> observableListNouns;
    Connection conn;

    public void initialize() {
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/EnglishApp", "postgres", "123");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        runSelect(conn);

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
        } else {
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
        getNoun(arrayNouns);
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

    public void onButtonGetListClick() {
        runSelect(conn);

        observableListNouns = FXCollections.observableArrayList();
        observableListNouns.addAll(arrayNouns);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        inEnglishColumn.setCellValueFactory(new PropertyValueFactory<>("inEnglish"));
        inRussianColumn.setCellValueFactory(new PropertyValueFactory<>("inRussian"));
        inGroupColumn.setCellValueFactory(new PropertyValueFactory<>("inGroup"));

        tableWords.setItems(observableListNouns);
    }
}