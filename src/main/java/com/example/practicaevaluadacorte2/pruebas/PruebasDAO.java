package com.example.practicaevaluadacorte2.pruebas;

import com.example.practicaevaluadacorte2.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PruebasDAO {

    public static void main(String[] args) {
        System.out.println("--- INICIANDO PRUEBAS DE EXCEPCIONES ---");
        pruebaDuplicado();
        pruebaRestriccionCheck();
        System.out.println("--- PRUEBAS FINALIZADAS ---");
    }

    private static void pruebaDuplicado() {
        //error 23505 insertando P001 que ya existe
        String sql = "INSERT INTO catalogo.producto (codigo, nombre, categoria, precio, stock) VALUES ('P001', 'Error', 'Papelería', 10.0, 5)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[ERROR] Operación: Inserción de código duplicado.");
            System.out.println("Mensaje BD: " + e.getMessage());
            System.out.println("SQLState capturado: " + e.getSQLState()); // Lanza 23505
            System.out.println("--------------------------------------------------");
        }
    }

    private static void pruebaRestriccionCheck() {
        // error 23514 insertando un precio negativo (viola el CHECK)
        String sql = "INSERT INTO catalogo.producto (codigo, nombre, categoria, precio, stock) VALUES ('Z999', 'Error', 'Papelería', -50.0, 5)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[ERROR] Operación: Inserción con precio negativo.");
            System.out.println("Mensaje BD: " + e.getMessage());
            System.out.println("SQLState capturado: " + e.getSQLState()); // Lanza 23514
            System.out.println("--------------------------------------------------");
        }
    }
}