package com.example.practicaevaluadacorte2.controller;

import com.example.practicaevaluadacorte2.dao.ProductoDAO;
import com.example.practicaevaluadacorte2.model.Producto;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ProductoController {

    @FXML
    private TextField txtId;
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
            productoSeleccionado = newV;
            if (newV != null) {
                txtId.setText(String.valueOf(newV.getIdProducto()));
                txtCodigo.setText(newV.getCodigo());
                txtNombre.setText(newV.getNombre());
                cmbCategoriaForm.setValue(newV.getCategoria());
                txtPrecio.setText(newV.getPrecio().toString());
                txtStock.setText(String.valueOf(newV.getStock()));
            }
        });

        Platform.runLater(this::onRecargar);
    }

    @FXML
    void onLimpiarFormulario() {
        txtId.clear();
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
            manejarErrorSQL("INSERT producto", e);
        }
    }

    @FXML
    void onActualizar(ActionEvent event) {
        if (productoSeleccionado == null) {
            alerta("Aviso", "Debe seleccionar un producto para actualizar.", Alert.AlertType.WARNING);
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
                alerta("Aviso", "No se pudo actualizar: el producto ya no existe en la base de datos. Se recargará el listado.", Alert.AlertType.WARNING);
            } else {
                alerta("Info", "Producto actualizado.", Alert.AlertType.INFORMATION);
            }
            onLimpiarFormulario();
            onBuscar();
        } catch (SQLException e) {
            manejarErrorSQL("UPDATE producto", e);
        }
    }

    @FXML
    void onEliminar(ActionEvent event) {
        if (productoSeleccionado == null) {
            alerta("Aviso", "Debe seleccionar un producto de la tabla para eliminar.", Alert.AlertType.WARNING);
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar");
        confirm.setHeaderText("¿Seguro que desea eliminar el producto " + productoSeleccionado.getCodigo() + "?");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                int afectadas = dao.eliminar(productoSeleccionado.getIdProducto());
                if (afectadas == 0) {
                    alerta("Aviso", "El producto ya no existe en la base de datos. Se recargará el listado.", Alert.AlertType.WARNING);
                } else {
                    alerta("Info", "Eliminado correctamente.", Alert.AlertType.INFORMATION);
                }
                onLimpiarFormulario();
                onBuscar();
            } catch (SQLException e) {
                manejarErrorSQL("DELETE producto", e);
            }
        }
    }

    @FXML
    void onBuscar() {
        String texto = txtBuscarTexto.getText();
        String cat = cmbBuscarCategoria.getValue();
        boolean soloStock = chkSoloExistencias.isSelected();

        try {
            List<Producto> filtrados = dao.obtenerProductos(texto, cat, soloStock);
            listaProductos.setAll(filtrados);
            actualizarContador();

            if (filtrados.isEmpty()) {
                lblContador.setText("Registros visibles: 0 - Sin coincidencias");
            }
        } catch (SQLException e) {
            manejarErrorSQL("SELECT producto", e);
        }
    }

    @FXML
    void onLimpiarFiltros() {
        txtBuscarTexto.clear();
        cmbBuscarCategoria.setValue("Todas");
        chkSoloExistencias.setSelected(false);
        onRecargar();
    }
    @FXML
    void onRecargar() {
        try {
            List<Producto> todos = dao.obtenerProductos(null, "Todas", false);
            listaProductos.setAll(todos);
            actualizarContador();
        } catch (SQLException e) {
            manejarErrorSQL("SELECT producto", e);
        }
    }

    private void actualizarContador() {
        lblContador.setText("Registros visibles: " + listaProductos.size());
    }

    private boolean validarEntradas() {
        String cod = txtCodigo.getText();
        if (cod == null || cod.trim().isEmpty() || cod.trim().length() > 10) {
            alerta("Error Validación", "El código debe tener entre 1 y 10 caracteres.", Alert.AlertType.WARNING);
            return false;
        }

        String nom = txtNombre.getText();
        if (nom == null || nom.trim().length() < 3 || nom.trim().length() > 80) {
            alerta("Error Validación", "El nombre debe tener entre 3 y 80 caracteres.", Alert.AlertType.WARNING);
            return false;
        }

        if (cmbCategoriaForm.getValue() == null) {
            alerta("Error Validación", "Seleccione una categoría válida.", Alert.AlertType.WARNING);
            return false;
        }

        String textoPrecio = txtPrecio.getText() == null ? "" : txtPrecio.getText().trim();
        if (!textoPrecio.matches("-?\\d+(\\.\\d+)?")) {
            alerta("Error Validación", "Formato de precio inválido. Use números con punto decimal (ej. 12.50).", Alert.AlertType.WARNING);
            return false;
        }
        BigDecimal precio = new BigDecimal(textoPrecio);
        if (precio.compareTo(BigDecimal.ZERO) <= 0) {
            alerta("Error Validación", "El precio debe ser mayor que 0.", Alert.AlertType.WARNING);
            return false;
        }
        if (precio.scale() > 2) {
            alerta("Error Validación", "El precio no debe tener más de 2 decimales.", Alert.AlertType.WARNING);
            return false;
        }
        if (precio.compareTo(new BigDecimal("99999999.99")) > 0) {
            alerta("Error Validación", "El precio supera el máximo permitido (99999999.99).", Alert.AlertType.WARNING);
            return false;
        }

        String textoStock = txtStock.getText() == null ? "" : txtStock.getText().trim();
        try {
            BigInteger stock = new BigInteger(textoStock);
            if (stock.signum() < 0) {
                alerta("Error Validación", "El stock no puede ser negativo.", Alert.AlertType.WARNING);
                return false;
            }
            if (stock.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
                alerta("Error Validación", "El stock no puede superar 2147483647.", Alert.AlertType.WARNING);
                return false;
            }
        } catch (NumberFormatException e) {
            alerta("Error Validación", "El stock debe ser un número entero (sin letras ni decimales).", Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

    private void manejarErrorSQL(String operacion, SQLException e) {
        System.err.println("[ERROR] Operación: " + operacion
                + " | SQLState: " + e.getSQLState() + " | " + e.getMessage());

        String estado = e.getSQLState() == null ? "" : e.getSQLState();
        if (estado.equals("23505")) {
            alerta("Registro duplicado", "Ya existe un producto con ese código.", Alert.AlertType.ERROR);
        } else if (estado.equals("23514")) {
            alerta("Dato no permitido", "Los datos no cumplen las restricciones de la base de datos.", Alert.AlertType.ERROR);
        } else if (estado.startsWith("08")) {
            alerta("Error de conexión", "No se pudo conectar con la base de datos.", Alert.AlertType.ERROR);
        } else {
            alerta("Error de base de datos", "No se pudo completar la operación.", Alert.AlertType.ERROR);
        }
    }
    private void alerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}