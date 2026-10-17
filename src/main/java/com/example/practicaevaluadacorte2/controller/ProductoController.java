package com.example.practicaevaluadacorte2.controller;

import com.example.practicaevaluadacorte2.dao.ProductoDAO;
import com.example.practicaevaluadacorte2.model.Producto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ProductoController {

    @FXML
    private TextField txtCodigo;
    @FXML
    private TextField txtNombre;
    @FXML
    private ComboBox<String> cmbCategoriaForm;
    @FXML
    private TextField txtPrecio;
    @FXML
    private TextField txtStock;

    @FXML
    private TextField txtBuscarTexto;
    @FXML
    private ComboBox<String> cmbBuscarCategoria;
    @FXML
    private CheckBox chkSoloExistencias;
    @FXML
    private Label lblContador;

    @FXML
    private TableView<Producto> tblProductos;
    @FXML
    private TableColumn<Producto, Integer> colId;
    @FXML
    private TableColumn<Producto, String> colCodigo;
    @FXML
    private TableColumn<Producto, String> colNombre;
    @FXML
    private TableColumn<Producto, String> colCategoria;
    @FXML
    private TableColumn<Producto, BigDecimal> colPrecio;
    @FXML
    private TableColumn<Producto, Integer> colStock;

    private ProductoDAO dao = new ProductoDAO();
    private ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
    private Producto productoSeleccionado = null;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        tblProductos.setItems(listaProductos);

        cmbCategoriaForm.setItems(FXCollections.observableArrayList("Papelería", "Tecnología", "Accesorios"));
        cmbBuscarCategoria.setItems(FXCollections.observableArrayList("Todas", "Papelería", "Tecnología", "Accesorios"));
        cmbBuscarCategoria.setValue("Todas");

        tblProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                productoSeleccionado = newV;
                txtCodigo.setText(newV.getCodigo());
                txtNombre.setText(newV.getNombre());
                cmbCategoriaForm.setValue(newV.getCategoria());
                txtPrecio.setText(newV.getPrecio().toString());
                txtStock.setText(String.valueOf(newV.getStock()));
            }
        });

        onRecargar();
    }

    @FXML
    void onLimpiarFormulario() {
        txtCodigo.clear();
        txtNombre.clear();
        cmbCategoriaForm.setValue(null);
        txtPrecio.clear();
        txtStock.clear();
        productoSeleccionado = null;
        tblProductos.getSelectionModel().clearSelection();
    }

    @FXML
    void onGuardar(ActionEvent event) {
        if (!validarEntradas()) {
            return;
        }

        Producto p = new Producto();
        p.setCodigo(txtCodigo.getText().trim().toUpperCase());
        p.setNombre(txtNombre.getText().trim());
        p.setCategoria(cmbCategoriaForm.getValue());
        p.setPrecio(new BigDecimal(txtPrecio.getText().trim()));
        p.setStock(Integer.parseInt(txtStock.getText().trim()));

        try {
            dao.insertar(p);
            alerta("Info", "Producto guardado con éxito.", Alert.AlertType.INFORMATION);
            onLimpiarFormulario();
            onBuscar();
        } catch (SQLException e) {
            manejarErrorSQL(e);
        }
    }

    @FXML
    void onActualizar(ActionEvent event) {
        if (productoSeleccionado == null) {
            alerta("Aviso", "Seleccione un producto para actualizar.", Alert.AlertType.WARNING);
            return;
        }

        if (!validarEntradas()) {
            return;
        }

        Producto p = new Producto();
        p.setIdProducto(productoSeleccionado.getIdProducto());
        p.setCodigo(txtCodigo.getText().trim().toUpperCase());
        p.setNombre(txtNombre.getText().trim());
        p.setCategoria(cmbCategoriaForm.getValue());
        p.setPrecio(new BigDecimal(txtPrecio.getText().trim()));
        p.setStock(Integer.parseInt(txtStock.getText().trim()));

        try {
            int afectadas = dao.actualizar(p);
            if (afectadas == 0) {
                alerta("Error", "No se pudo actualizar. El registro ya no existe en la BD.", Alert.AlertType.ERROR);
            } else {
                alerta("Info", "Producto actualizado.", Alert.AlertType.INFORMATION);
                onLimpiarFormulario();
            }
            onBuscar();
        } catch (SQLException e) {
            manejarErrorSQL(e);
        }
    }
    
    @FXML
    void onRecargar() {
        try {
            List<Producto> todos = dao.obtenerProductos(null, "Todas", false);
            listaProductos.setAll(todos);
            actualizarContador();
        } catch (SQLException e) {
            alerta("Fallo de conexión", "Asegúrese de que el servidor PostgreSQL esté encendido.", Alert.AlertType.ERROR);
        }
    }

    private void actualizarContador() {
        lblContador.setText("Registros visibles: " + listaProductos.size());
    }



    private void alerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}