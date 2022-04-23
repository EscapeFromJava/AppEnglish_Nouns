package com.example.appenglish_nouns.controller;

import com.example.appenglish_nouns.model.Group;
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
    Button btnStart, btnCheck, btnFinish, btnAddNoun, btnGetList;
    @FXML
    ComboBox<String> comboBoxGroup;
    @FXML
    Label lblInput;
    @FXML
    TableColumn<Noun, Integer> idColumn, inGroupColumn;
    @FXML
    TableColumn<Noun, String> inEnglishColumn, inRussianColumn;
    @FXML
    TextField txtInEnglsh, txtInRussian, txtInGroup;
    @FXML
    TableView<Noun> tableWords;
    @FXML
    TextArea txtAreaOutput;

    ArrayList<Group> arrayGroups;
    ArrayList<Noun> arrayNouns;
    Connection conn;
    Noun actualNoun;
    ObservableList<Noun> observableListNouns;

    ObservableList<String> observableListGroupsName;

    public void initialize() {
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/EnglishApp", "postgres", "123");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        runSQLSelectNouns(conn);

    }

    public void runSQLSelectGroups(Connection conn) {
        try {
            arrayGroups = new ArrayList<>();
            String execute = "SELECT * FROM groups";
            PreparedStatement statement = conn.prepareStatement(execute);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                arrayGroups.add(new Group(rs.getInt("id"),
                        rs.getString("name")));
            }
        } catch (SQLException e) {
            System.out.println("select ERROR: " + e.getMessage());
        }
    }

    public void runSQLSelectNouns(Connection conn) {
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


    public void runSQLInsertNouns(Connection conn, Noun newNoun) {
        try {
            String execute = "INSERT INTO nouns(in_english, in_russian, in_group)  " +
                    "VALUES( \'" + newNoun.inEnglish + "\', \'" + newNoun.inRussian + "\', " + newNoun.inGroup + ");";
            PreparedStatement statement = conn.prepareStatement(execute);
            statement.execute();
        } catch (SQLException e) {
            System.out.println("insert ERROR: " + e.getMessage());
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
        String selectedGroup = comboBoxGroup.getValue().toString();
        Group resultingGroup = arrayGroups.stream().filter(g -> g.name.equals(selectedGroup)).findFirst().get();
        Noun addNoun = new Noun(txtInEnglsh.getText(), txtInRussian.getText(), resultingGroup.id);
        runSQLInsertNouns(conn, addNoun);
    }

    public void onButtonGetListClick() {
        runSQLSelectNouns(conn);

        observableListNouns = FXCollections.observableArrayList();
        observableListNouns.addAll(arrayNouns);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        inEnglishColumn.setCellValueFactory(new PropertyValueFactory<>("inEnglish"));
        inRussianColumn.setCellValueFactory(new PropertyValueFactory<>("inRussian"));
        inGroupColumn.setCellValueFactory(new PropertyValueFactory<>("inGroup"));

        tableWords.setItems(observableListNouns);
    }

    public void onTabWordClick() {
        runSQLSelectGroups(conn);

        observableListGroupsName = FXCollections.observableArrayList();

        for (Group g : arrayGroups) {
            observableListGroupsName.add(g.name);
        }

        comboBoxGroup.setItems(observableListGroupsName.sorted());
        comboBoxGroup.setValue("any");
    }

}