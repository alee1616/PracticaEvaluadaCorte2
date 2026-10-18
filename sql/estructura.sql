--crea esquema
CREATE SCHEMA IF NOT EXISTS catalogo;

--crea tabla 
CREATE TABLE catalogo.producto (
    id_producto INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo VARCHAR(10) NOT NULL UNIQUE,
    nombre VARCHAR(80) NOT NULL,
    categoria VARCHAR(20) NOT NULL,
    precio NUMERIC(10,2) NOT NULL,
    stock INTEGER NOT NULL,
--validaciones
    CHECK (trim(codigo) <> ''),
    CHECK (length(trim(nombre)) >= 3 AND length(trim(nombre)) <= 80),
    CHECK (categoria IN ('Papelería', 'Tecnología', 'Accesorios')),
    CHECK (precio > 0),
    CHECK (stock >= 0)
);