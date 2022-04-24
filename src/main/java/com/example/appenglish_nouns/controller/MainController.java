package com.example.appenglish_nouns.controller;

import com.example.appenglish_nouns.model.Group;
import com.example.appenglish_nouns.model.Noun;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.sql.*;
import java.util.*;

public class MainController {
    @FXML
    AnchorPane anchorPaneMenu;
    @FXML
    BarChart<String, Number> barChartNouns;
    @FXML
    Button btnStart, btnCheck, btnFinish, btnAddNoun, btnRefresh;
    @FXML
    ComboBox<String> comboBoxGroup;
    @FXML
    Label lblInput, lblResult, lblScore;
    @FXML
    TableColumn<Noun, Integer> idColumn, inGroupColumn;
    @FXML
    TableColumn<Noun, String> inEnglishColumn, inRussianColumn, inNameGroupColumn;
    @FXML
    TextField txtInEnglsh, txtInRussian, txtFieldOutput;
    @FXML
    TableView<Noun> tableWords;
    @FXML
    TextArea txtAreaOutput;

    ArrayList<Group> arrayGroups;
    ArrayList<Noun> arrayNouns;
    Connection conn;

    int score = 0;
    Noun actualNoun;
    ObservableList<Noun> observableListNouns;
    ObservableList<String> observableListGroupsName;
    XYChart.Series<String, Number> dataSeries;

    public void initialize() {
        String address = getClass().getResource("/com/example/appenglish_nouns/img/menu.jpg").toExternalForm();
        anchorPaneMenu.setStyle("-fx-background-image: url(" + address + ");");

        try {
            conn = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/EnglishApp", "postgres", "123");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //SQL executes
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
            String execute = "SELECT nouns.id AS n_id, in_english, in_russian, groups.id AS g_id, groups.name AS g_name  " +
                    "FROM nouns " +
                    "JOIN groups ON nouns.in_group = groups.id";
            PreparedStatement statement = conn.prepareStatement(execute);
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

    // other functions
    public void getNoun(ArrayList<Noun> listNouns) {
        actualNoun = listNouns.get(new Random().nextInt(0, listNouns.size() - 1));
    }

    //Tab "Menu"

    //Tab "Exercise"
    public void onTabExerciseClick() {
        runSQLSelectNouns(conn);
        btnCheck.setDisable(true);
        btnStart.setDisable(false);
        lblInput.setText("");
        lblResult.setText("");
        lblScore.setText("Score: " + score);
    }
    public void onButtonStartClick() {
        getNoun(arrayNouns);
        btnCheck.setDisable(false);
        btnStart.setDisable(true);
        if (lblInput.getText().equals(""))
            lblInput.setText(actualNoun.inEnglish);
        score = 0;
        lblScore.setText("Score: " + score);
    }

    public void onButtonCheckClick() {
        if (txtAreaOutput.getText().toLowerCase().equals(actualNoun.inRussian.toLowerCase())) {
            getNoun(arrayNouns);
            lblInput.setText(actualNoun.inEnglish);
            lblResult.setText("Right!");
            score++;
            lblScore.setText("Score: " + score);
        }
        else {
            lblResult.setText("Wrong!");
        }
        txtAreaOutput.clear();
    }

    public void onButtonFinishClick() {
        btnCheck.setDisable(true);
        btnStart.setDisable(false);
        lblInput.setText("");
        lblResult.setText("");
    }

    //Tab "Add Word"
    public void onTabAddWordClick() {
        runSQLSelectGroups(conn);

        observableListGroupsName = FXCollections.observableArrayList();

        for (Group g : arrayGroups) {
            observableListGroupsName.add(g.name);
        }

        comboBoxGroup.setItems(observableListGroupsName.sorted());
        comboBoxGroup.setValue("any");
    }

    public void onButtonAddNounClick() {
        String selectedGroup = comboBoxGroup.getValue().toString();
        Group resultingGroup = arrayGroups.stream().filter(g -> g.name.equals(selectedGroup)).findFirst().get();
        Noun addNoun = new Noun(txtInEnglsh.getText(), txtInRussian.getText(), resultingGroup.id);
        runSQLInsertNouns(conn, addNoun);
    }

    //Tab "List Words"
    public void onTabListWordsClick(Event event) {
        getListWords();
    }

    public void onButtonRefreshClick() {
        getListWords();
    }

    public void getListWords() {
        runSQLSelectNouns(conn);

        observableListNouns = FXCollections.observableArrayList();
        observableListNouns.addAll(arrayNouns);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        inEnglishColumn.setCellValueFactory(new PropertyValueFactory<>("inEnglish"));
        inRussianColumn.setCellValueFactory(new PropertyValueFactory<>("inRussian"));
        inGroupColumn.setCellValueFactory(new PropertyValueFactory<>("inGroup"));
        inNameGroupColumn.setCellValueFactory(new PropertyValueFactory<>("inNameGroup"));

        tableWords.setItems(observableListNouns);
    }


    //Tab "Chart"
    public void onTabChartClick() {
        if (dataSeries != null) {
            barChartNouns.getData().clear();
        }

        runSQLSelectGroups(conn);
        runSQLSelectNouns(conn);

        dataSeries = new XYChart.Series<String, Number>();
        dataSeries.setName("Nouns Chart");

        arrayGroups.sort(Comparator.comparing(o -> o.name));

        for (Group g: arrayGroups) {
            int count = (int) arrayNouns.stream().filter(n -> n.inNameGroup.equals(g.name)).count();
            dataSeries.getData().add(new XYChart.Data<String, Number>(g.name, count));
        }

        barChartNouns.getData().add(dataSeries);
    }


}