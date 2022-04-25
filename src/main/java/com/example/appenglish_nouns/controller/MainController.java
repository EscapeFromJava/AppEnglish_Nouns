package com.example.appenglish_nouns.controller;

import com.example.appenglish_nouns.MainApp;
import com.example.appenglish_nouns.model.Group;
import com.example.appenglish_nouns.model.Noun;
import com.example.appenglish_nouns.model.SQLrequest;
import com.example.appenglish_nouns.model.Session;
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

import static com.example.appenglish_nouns.model.SQLrequest.arrayGroups;
import static com.example.appenglish_nouns.model.SQLrequest.arrayNouns;

public class MainController {
    @FXML
    AnchorPane anchorPaneMenu;
    @FXML
    BarChart<String, Number> barChartNouns;
    @FXML
    Button btnStart, btnCheck, btnSkip, btnFinish, btnAddNoun, btnAddGroup, btnRefresh, btnOpenFile;
    @FXML
    CheckBox checkBoxHelper;
    @FXML
    ComboBox<String> comboBoxGroup;
    @FXML
    Label lblInput, lblResult, lblScore, lblSessionTime, lblResultAddNoun, lblResultAddGroup;
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
    TextField txtInEnglish, txtInRussian, txtGroupName, txtFieldOutput;
    @FXML
    ToggleButton btnChangeTheme;
    Connection conn;
    int score = 0;
    Noun actualNoun;
    ObservableList<Noun> observableListNouns;
    ObservableList<String> observableListGroupsName;

    Session session;
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
        SQLrequest.runSQLSelectNouns(conn);
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
        lblSessionTime.setText("");
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
        lblSessionTime.setText("");
        txtFieldOutput.setDisable(false);
        txtFieldOutput.requestFocus();
        session = new Session("Player");
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
        lblSessionTime.setText(session.getSessionTime());
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

    //Tab "Add"
    public void onTabAddClick() {
        txtInEnglish.setText("");
        txtInRussian.setText("");
        lblResultAddNoun.setText("");

        txtGroupName.setText("");
        lblResultAddGroup.setText("");

        refreshListGroup();
    }

    public void onButtonAddNounClick() {
        String selectedGroup = comboBoxGroup.getValue().toString();
        Group resultingGroup = arrayGroups.stream().filter(g -> g.name.equals(selectedGroup)).findFirst().get();
        Noun newNoun = new Noun(txtInEnglish.getText(), txtInRussian.getText(), resultingGroup.id);
        if (!newNoun.isNull()) {
            try {
                SQLrequest.runSQLInsertNoun(conn, newNoun);
                lblResultAddNoun.setText("The object [" + newNoun.inEnglish + " : " + newNoun.inRussian + "] was successfully added");
            } catch (Exception e) {
                lblResultAddNoun.setText("Error! " + e.getMessage());
            }
        }
        else
            lblResultAddNoun.setText("Fill in the fields");
    }
    public void onButtonAddGroupClick() {
        Group newGroup = new Group(txtGroupName.getText());
        if (!newGroup.isNull()) {
            try {
                SQLrequest.runSQLInsertGroup(conn, newGroup);
                lblResultAddGroup.setText("The object [" + newGroup.name + "] was successfully added");
            } catch (Exception e) {
                lblResultAddGroup.setText("Error! " + e.getMessage());
            }
        }
        else
            lblResultAddGroup.setText("Fill in the fields");

        refreshListGroup();
    }
    public void refreshListGroup() {
        SQLrequest.runSQLSelectGroups(conn);

        observableListGroupsName = FXCollections.observableArrayList();

        for (Group g : arrayGroups) {
            observableListGroupsName.add(g.name);
        }

        comboBoxGroup.setItems(observableListGroupsName.sorted());
        comboBoxGroup.setValue("any");
    }

    //Tab "List Words"
    public void onTabListWordsClick() {
        getListWords();
    }

    public void onButtonRefreshClick() {
        getListWords();
    }

    public void getListWords() {
        SQLrequest.runSQLSelectNouns(conn);

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

        SQLrequest.runSQLSelectGroups(conn);
        SQLrequest.runSQLSelectNouns(conn);

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