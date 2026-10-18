# Catálogo de Productos - Papelería Campus

Aplicación de escritorio desarrollada con **JavaFX** que almacena el catálogo de productos de la papelería Campus en **PostgreSQL** y accede a él mediante **JDBC**. Permite consultar, buscar, filtrar, crear, actualizar y eliminar productos, mostrando la información en un `TableView` coherente con la base de datos.

**Laboratorio:** Acceso a datos y visualización en TableView
**Curso:** Programación de Aplicaciones de Escritorio | JavaFX + PostgreSQL

## Autor

- **Estudiante:** Guillén Castillo, Marian Alejandra
- **Grupo:** [5]
- **Repositorio:** [https://github.com/alee1616/PracticaEvaluadaCorte2.git]

## Versiones utilizadas

| Componente | Versión |
|---|---|
| Java (JDK) | 21 |
| JavaFX / OpenJFX | 21.0.6 |
| PostgreSQL | 18.4 |
| Driver JDBC (pgJDBC) | 42.7.13 |
| Maven (wrapper) | 3.8.5 (Maven Wrapper 3.3.2) |
| maven-compiler-plugin | 3.13.0 |
| javafx-maven-plugin | 0.0.8 |
| JUnit Jupiter (pruebas) | 5.12.1 |
| IDE | IntelliJ IDEA 2025.3.3 (Ultimate, build IU-253.31033.145) |
| Sistema operativo | Windows (x86_64) |

## Dependencias

Declaradas en `pom.xml`:

- `org.openjfx:javafx-controls` 21.0.6
- `org.openjfx:javafx-fxml` 21.0.6
- `org.postgresql:postgresql` 42.7.13 (driver JDBC)
- `org.junit.jupiter:junit-jupiter-api` 5.12.1 (alcance de pruebas)

## Funcionalidades

- Listado en `TableView` con seis columnas (ID, código, nombre, categoría, precio y stock), orden inicial por código ascendente y contador de registros visibles.
- Búsqueda por texto parcial en nombre o código, sin distinguir mayúsculas y minúsculas (`ILIKE`).
- Filtro por categoría (`Todas`, Papelería, Tecnología, Accesorios) y casilla **Solo con existencias**, combinados con AND. Cada búsqueda consulta PostgreSQL.
- Botones: Nuevo/Limpiar, Guardar, Actualizar, Eliminar (con confirmación), Buscar, Limpiar filtros y Recargar.
- Validaciones: código de 1 a 10 caracteres (se guarda con `trim` y en mayúsculas), nombre de 3 a 80, categoría permitida, precio con `BigDecimal` (0.01 a 99999999.99 y máximo 2 decimales) y stock entero de 0 a 2147483647.
- Manejo de errores: fallo de conexión, violaciones de restricciones SQL, cero filas afectadas, y registro de la operación y el `SQLState` en consola, con mensajes seguros para el usuario.

## Estructura del proyecto

```
PracticaEvaluadaCorte2/
├── pom.xml
├── config.properties.example      # Ejemplo de configuración (sin secretos)
├── pruebas.md                     # Matriz de pruebas
├── evidencias/                    # Capturas de las pruebas
├── sql/
│   ├── estructura.sql             # Esquema, tabla y restricciones
│   └── datos.sql                  # 12 productos iniciales
└── src/main/
    ├── java/com/example/practicaevaluadacorte2/
    │   ├── ProductoApplication.java            # Clase principal JavaFX
    │   ├── Launcher.java
    │   ├── model/Producto.java                 # Entidad
    │   ├── dao/ProductoDAO.java                # Todo el SQL (PreparedStatement)
    │   ├── database/DatabaseConnection.java    # Conexión configurable
    │   ├── controller/ProductoController.java  # Eventos y validaciones
    │   └── pruebas/PruebasDAO.java             # Prueba E2 (sin interfaz)
    └── resources/.../producto-view.fxml        # Interfaz
```

## Creación de la base de datos

1. Crear la base de datos desde `psql` o pgAdmin:

```sql
   CREATE DATABASE papeleria_campus;
```

2. Seleccionar la base `papeleria_campus`:
    - En `psql`: `\c papeleria_campus`
    - En pgAdmin: seleccionar `papeleria_campus` en el árbol y abrir **Query Tool**.

3. Ejecutar los scripts en este orden:

```
   1) sql/estructura.sql   -> esquema catalogo, tabla producto y restricciones
   2) sql/datos.sql        -> los 12 productos iniciales
```

En `psql`: `\i sql/estructura.sql` y luego `\i sql/datos.sql`.

### Restaurar la base de prueba

Para volver a los 12 productos iniciales basta con ejecutar de nuevo `sql/datos.sql`: el script vacía la tabla (`TRUNCATE ... RESTART IDENTITY`) y vuelve a insertar los 12 registros.

## Configuración de la conexión

La conexión **no está escrita en el código**. Se lee del archivo `config.properties`, que está en la raíz del proyecto (junto a `pom.xml`) y está excluido de Git mediante `.gitignore`.

1. Copiar el archivo de ejemplo:

```bash
   cp config.properties.example config.properties
```

(En Windows: copiar el archivo y renombrarlo a `config.properties`.)

2. Editar `config.properties` con los datos del PostgreSQL local:

```properties
   db.url=jdbc:postgresql://localhost:5432/papeleria_campus
   db.user=postgres
   db.password=CAMBIAR_POR_SU_CONTRASENA
```

Para probar un fallo de conexión (prueba E1) se usa un archivo de configuración con un puerto incorrecto, indicado con la opción de JVM `-Ddb.config=config-prueba.properties`.

## Ejecución

**Desde IntelliJ IDEA:** abrir el proyecto, esperar a que Maven descargue las dependencias y ejecutar `ProductoApplication`.

**Desde la terminal:**

```bash
./mvnw clean javafx:run
```

(En Windows: `mvnw.cmd clean javafx:run`.)

Requisitos previos: PostgreSQL en ejecución, base de datos creada con sus scripts y `config.properties` configurado.

## Organización de clases y flujo de datos

| Capa | Clase | Responsabilidad |
|---|---|---|
| Modelo | `Producto` | Entidad con los campos de la tabla |
| Acceso a datos | `ProductoDAO` | Todo el SQL, con `PreparedStatement`, `executeQuery` y `executeUpdate` |
| Conexión | `DatabaseConnection` | Abre la conexión con los datos de `config.properties` |
| Controlador | `ProductoController` | Eventos de la interfaz, validaciones y llamadas al DAO |
| Interfaz | `producto-view.fxml` | Vista JavaFX |

**Flujo de una operación:**

```
Interfaz (FXML) -> ProductoController -> ProductoDAO -> PostgreSQL
                                                          |
TableView <- ObservableList<Producto> <- Producto <- ResultSet
```

1. El usuario actúa sobre la interfaz (buscar, guardar, actualizar o eliminar).
2. El controlador valida y normaliza los datos (`trim`, mayúsculas, `BigDecimal`).
3. El controlador llama al DAO, que ejecuta la sentencia SQL con parámetros.
4. En las consultas, cada fila del `ResultSet` se convierte en un `Producto`, que se agrega a la `ObservableList`.
5. La lista alimenta el `TableView`. Tras