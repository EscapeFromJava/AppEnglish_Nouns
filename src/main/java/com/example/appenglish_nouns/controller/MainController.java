package com.example.appenglish_nouns.controller;

import com.example.appenglish_nouns.MainApp;
import com.example.appenglish_nouns.model.Group;
import com.example.appenglish_nouns.model.Noun;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainController {
    @FXML
    AnchorPane anchorPaneMenu;
    @FXML
    BarChart<String, Number> barChartNouns;
    @FXML
    Button btnStart, btnCheck, btnSkip, btnFinish, btnAddNoun, btnRefresh, btnOpenFile;
    @FXML
    CheckBox checkBoxHelper;
    @FXML
    ComboBox<String> comboBoxGroup;
    @FXML
    Label lblInput, lblResult, lblScore, lblResultAddWord;
    @FXML
    TableColumn<Noun, Integer> idColumn, inGroupColumn;
    @FXML
    TableColumn<Noun, String> inEnglishColumn, inRussianColumn, inNameGroupColumn;
    @FXML
    TableView<Noun> tableWords;
    @FXML
    TabPane tabPaneMain;
    @FXML
    TextArea txtAreaOpenFile;
    @FXML
    TextField txtInEnglish, txtInRussian, txtFieldOutput;
    @FXML
    ToggleButton btnChangeTheme;
    ArrayList<Group> arrayGroups;
    ArrayList<Noun> arrayNouns;
    Connection conn;
    int score = 0;
    Noun actualNoun;
    ObservableList<Noun> observableListNouns;
    ObservableList<String> observableListGroupsName;
    XYChart.Series<String, Number> dataSeries;

    public void initialize() {
        String address = getClass().getResource("/com/example/appenglish_nouns/img/menu.png").toExternalForm();
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
    public void onButtonChangeThemeClick() {
        tabPaneMain.getStylesheets().clear();
        if (btnChangeTheme.isSelected()) {
            btnChangeTheme.setText("Dark");
            tabPaneMain.getStylesheets().add(MainApp.class.getResource("/com/example/appenglish_nouns/styles/darkTheme.css").toExternalForm());
        }
        else {
            btnChangeTheme.setText("Light");
            tabPaneMain.getStylesheets().add(MainApp.class.getResource("/com/example/appenglish_nouns/styles/lightTheme.css").toExternalForm());
        }
    }

    //Tab "Exercise"
    public void onTabExerciseClick() {
        runSQLSelectNouns(conn);
        btnCheck.setDisable(true);
        btnFinish.setDisable(true);
        btnSkip.setDisable(true);
        btnStart.setDisable(false);
        if (checkBoxHelper.isSelected())
            checkBoxHelper.fire();
        checkBoxHelper.setDisable(true);
        checkBoxHelper.setDisable(true);
        lblInput.setText("");
        lblResult.setText("");
        lblScore.setText("Score: " + score);
        txtFieldOutput.setDisable(true);
    }

    public void onButtonStartClick() {
        getNoun(arrayNouns);
        btnCheck.setDisable(false);
        btnFinish.setDisable(false);
        btnSkip.setDisable(false);
        btnStart.setDisable(true);
        checkBoxHelper.setDisable(false);
        if (lblInput.getText().equals(""))
            lblInput.setText(actualNoun.inEnglish);
        score = 0;
        lblScore.setText("Score: " + score);
        txtFieldOutput.setDisable(false);
        txtFieldOutput.requestFocus();
    }

    public void onButtonCheckClick() {
        if (txtFieldOutput.getText().toLowerCase().equals(actualNoun.inRussian.toLowerCase())) {
            getNoun(arrayNouns);
            lblInput.setText(actualNoun.inEnglish);
            lblResult.setText("Right!");
            score++;
            lblScore.setText("Score: " + score);
        } else {
            lblResult.setText("Wrong!");
        }
        txtFieldOutput.clear();
        if (checkBoxHelper.isSelected()) {
            txtFieldOutput.setText(String.valueOf(actualNoun.inRussian.charAt(0)));
            txtFieldOutput.requestFocus();
            txtFieldOutput.forward();
        }
        txtFieldOutput.requestFocus();
    }

    public void onButtonSkipClick() {
        getNoun(arrayNouns);
        lblInput.setText(actualNoun.inEnglish);
        lblResult.setText("");
        txtFieldOutput.clear();
        if (checkBoxHelper.isSelected()) {
            txtFieldOutput.setText(String.valueOf(actualNoun.inRussian.charAt(0)));
            txtFieldOutput.requestFocus();
            txtFieldOutput.forward();
        }
        txtFieldOutput.requestFocus();
    }

    public void onButtonFinishClick() {
        btnCheck.setDisable(true);
        btnFinish.setDisable(true);
        btnSkip.setDisable(true);
        btnStart.setDisable(false);
        if (checkBoxHelper.isSelected())
            checkBoxHelper.fire();
        checkBoxHelper.setDisable(true);
        lblInput.setText("");
        lblResult.setText("");
        txtFieldOutput.setDisable(true);
    }

    public void onCheckBoxHelperClick() {
        if (checkBoxHelper.isSelected()) {
            txtFieldOutput.setText(String.valueOf(actualNoun.inRussian.charAt(0)));
        } else {
            txtFieldOutput.setText("");
        }
        txtFieldOutput.requestFocus();
        txtFieldOutput.forward();
    }

    //Tab "Add Word"
    public void onTabAddWordClick() {
        lblResultAddWord.setText("");
        txtInEnglish.setText("");
        txtInRussian.setText("");

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
        Noun addNoun = new Noun(txtInEnglish.getText(), txtInRussian.getText(), resultingGroup.id);
        try {
            runSQLInsertNouns(conn, addNoun);
            lblResultAddWord.setText("The object [" + addNoun.inEnglish + " : " + addNoun.inRussian + "] was successfully added");
        } catch (Exception e) {
            lblResultAddWord.setText("Error! " + e.getMessage());
        }
    }

    //Tab "List Words"
    public void onTabListWordsClick() {
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

        dataSeries = new XYChart.Series<>();
        dataSeries.setName("Nouns Chart");

        arrayGroups.sort(Comparator.comparing(o -> o.name));

        for (Group g : arrayGroups) {
            int count = (int) arrayNouns.stream().filter(n -> n.inNameGroup.equals(g.name)).count();
            dataSeries.getData().add(new XYChart.Data<>(g.name, count));
        }

        barChartNouns.getData().add(dataSeries);
    }

    //Tab "Open File"
    public void onButtonOpenFileClick() {
        txtAreaOpenFile.clear();

        Stage stage = (Stage) btnOpenFile.getScene().getWindow(); //ToDo - need to fix this

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showOpenDialog(stage);
        try {
            Stream<String> text = Files.lines(Paths.get(file.getPath()));
            txtAreaOpenFile.setText(text.map(String::valueOf).collect(Collectors.joining("\n")));
        } catch (IOException e) {
            txtAreaOpenFile.setText(e.getMessage());
        }
    }
}