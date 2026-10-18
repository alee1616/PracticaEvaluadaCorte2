package com.example.practicaevaluadacorte2.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static final Properties PROPS = cargar();

    private static Properties cargar() {
        Properties p = new Properties();
        String ruta = System.getProperty("db.config", "config.properties");
        try (InputStream in = new FileInputStream(ruta)) {
            p.load(in);
        } catch (IOException e) {
            System.err.println("[CONFIG] No se pudo leer " + ruta);
        }
        return p;
    }

    public static Connection getConnection() throws SQLException {
        String url = PROPS.getProperty("db.url");
        String user = PROPS.getProperty("db.user");
        String pass = PROPS.getProperty("db.password");
        if (url == null || user == null || pass == null) {
            throw new SQLException("Configuración de base de datos incompleta", "08001");
        }
        return DriverManager.getConnection(url, user, pass);
    }
}