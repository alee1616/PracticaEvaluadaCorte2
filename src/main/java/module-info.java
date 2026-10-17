module com.example.practicaevaluadacorte2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires java.desktop;

    opens com.example.practicaevaluadacorte2 to javafx.fxml;
    opens com.example.practicaevaluadacorte2.controller to javafx.fxml;
    opens com.example.practicaevaluadacorte2.model to javafx.fxml, javafx.base;

    exports com.example.practicaevaluadacorte2;
    exports com.example.practicaevaluadacorte2.controller;
    exports com.example.practicaevaluadacorte2.model;
    exports com.example.practicaevaluadacorte2.dao;
    exports com.example.practicaevaluadacorte2.database;
}