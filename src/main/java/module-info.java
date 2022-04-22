module com.example.appenglish_nouns {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.appenglish_nouns to javafx.fxml;
    exports com.example.appenglish_nouns;
    exports com.example.appenglish_nouns.controller;
    opens com.example.appenglish_nouns.controller to javafx.fxml;
}