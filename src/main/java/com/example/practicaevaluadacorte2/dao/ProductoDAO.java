package com.example.practicaevaluadacorte2.dao;

import com.example.practicaevaluadacorte2.database.DatabaseConnection;
import com.example.practicaevaluadacorte2.model.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public void insertar(Producto p) throws SQLException {
        String sql = "INSERT INTO catalogo.producto (codigo, nombre, categoria, precio, stock) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, p.getCodigo());
            pstmt.setString(2, p.getNombre());
            pstmt.setString(3, p.getCategoria());
            pstmt.setBigDecimal(4, p.getPrecio());
            pstmt.setInt(5, p.getStock());
            pstmt.executeUpdate();
        }
    }

    public int actualizar(Producto p) throws SQLException {
        String sql = "UPDATE catalogo.producto SET codigo = ?, nombre = ?, categoria = ?, precio = ?, stock = ? WHERE id_producto = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, p.getCodigo());
            pstmt.setString(2, p.getNombre());
            pstmt.setString(3, p.getCategoria());
            pstmt.setBigDecimal(4, p.getPrecio());
            pstmt.setInt(5, p.getStock());
            pstmt.setInt(6, p.getIdProducto());
            return pstmt.executeUpdate();
        }
    }

    public int eliminar(int idProducto) throws SQLException {
        String sql = "DELETE FROM catalogo.producto WHERE id_producto = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idProducto);
            return pstmt.executeUpdate();
        }
    }

    public List<Producto> obtenerProductos(String texto, String categoria, boolean soloStock) throws SQLException {
        List<Producto> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM catalogo.producto WHERE 1=1");

        if (texto != null && !texto.trim().isEmpty()) {
            sql.append(" AND (codigo ILIKE ? OR nombre ILIKE ?)");
        }
        if (categoria != null && !categoria.equals("Todas")) {
            sql.append(" AND categoria = ?");
        }
        if (soloStock) {
            sql.append(" AND stock > 0");
        }

        sql.append(" ORDER BY codigo ASC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            if (texto != null && !texto.trim().isEmpty()) {
                String busqueda = "%" + texto.trim() + "%";
                pstmt.setString(paramIndex++, busqueda);
                pstmt.setString(paramIndex++, busqueda);
            }
            if (categoria != null && !categoria.equals("Todas")) {
                pstmt.setString(paramIndex++, categoria);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Producto p = new Producto();
                    p.setIdProducto(rs.getInt("id_producto"));
                    p.setCodigo(rs.getString("codigo"));
                    p.setNombre(rs.getString("nombre"));
                    p.setCategoria(rs.getString("categoria"));
                    p.setPrecio(rs.getBigDecimal("precio"));
                    p.setStock(rs.getInt("stock"));
                    lista.add(p);
                }
            }
        }
        return lista;
    }
}