# Documento de Pruebas - CRUD Papelería Campus

## Ronda 1: Listado inicial
| Prueba | Acción realizada | Resultado esperado | Resultado obtenido | Estado | Evidencia |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **01_listado_inicial** | Iniciar la app sin filtros | Mostrar los 12 productos originales y contador en 12 | Se muestran 12 filas en la tabla | OK | `evidencias/01_listado_inicial.png` |
| **02_select_pgadmin** | Ejecutar SELECT en pgAdmin | Ver los mismos 12 productos en la base de datos | 12 filas correctas en BD | OK | `evidencias/02_select_pgadmin.png` |

## Ronda 2: Filtros
| Prueba | Filtros aplicados | Resultado esperado | Resultado obtenido | Estado | Evidencia |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **B1** | Búsqueda de texto: "CUADERNO" | Mostrar solo P001 y P002 | 2 filas visibles en la tabla | OK | `evidencias/B1.png` |
| **B2** | Búsqueda de texto: "T00" | Mostrar de T001 a T004 | 4 filas visibles en la tabla | OK | `evidencias/B2.png` |
| **B3** | Filtro Categoría: "Papelería" | Mostrar de P001 a P004 | 4 filas visibles en la tabla | OK | `evidencias/B3.png` |
| **B4** | Check: "Solo con existencias" | Ocultar productos con stock 0 | 9 filas visibles en la tabla | OK | `evidencias/B4.png` |
| **B5** | "USB" + Tecnología + Existencias | Mostrar T001 y T003 | 2 filas visibles en la tabla | OK | `evidencias/B5.png` |
| **B7** | Limpiar filtros tras B5 | Restablecer la vista original | 12 filas visibles en la tabla | OK | `evidencias/B7.png` |
| **B6** | Búsqueda de texto: "ZZZ999" | Alerta de "Sin coincidencias" y 0 filas | Aviso mostrado, 0 filas visibles | OK | `evidencias/B6.png` |

## Ronda 3: CRUD
| Prueba | Acción realizada | Resultado esperado | Resultado obtenido | Estado | Evidencia |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **CRUD1_insertar** | Insertar X001 (Producto prueba, Accesorios, 10, 1) | Mostrar 13 filas en la tabla con X001 | 13 filas, X001 creado con éxito | OK | `evidencias/CRUD1_insertar.png` |
| **CRUD1_select** | SELECT en base de datos tras inserción | Ver 13 filas incluyendo X001 | X001 presente en la BD | OK | `evidencias/CRUD1_select.png` |
| **CRUD2_actualizar** | Cambiar precio a 15.50 y stock a 3 en X001 | Reflejar los nuevos valores en la tabla | Valores de X001 actualizados | OK | `evidencias/CRUD2_actualizar.png` |
| **CRUD2_select** | SELECT en base de datos tras actualización | Ver X001 con precio 15.50 y stock 3 | BD reflejó la actualización | OK | `evidencias/CRUD2_select.png` |
| **CRUD3_cancelar** | Pulsar Eliminar en X001 y cancelar alerta | El registro no debe borrarse | X001 sigue en la tabla (13 filas) | OK | `evidencias/CRUD3_cancelar.png` |
| **CRUD4_eliminar** | Pulsar Eliminar en X001 y confirmar | El registro debe desaparecer | 12 filas visibles, X001 borrado | OK | `evidencias/CRUD4_eliminar.png` |
| **CRUD4_select** | SELECT en base de datos tras eliminación | Ver 12 filas, sin rastro de X001 | X001 eliminado de la BD | OK | `evidencias/CRUD4_select.png` |
| **CRUD5_persistencia** | Recrear X001, cerrar app y volver a abrir | Los datos deben guardarse físicamente | X001 visible al reabrir la app | OK | `evidencias/CRUD5_persistencia.png` |
| **CRUD6_filtros** | Filtrar Tecnología, cambiar T002 a Accesorios | T002 debe desaparecer del filtro | Filtro actualizado (3 filas restantes) | OK | `evidencias/CRUD6_filtros.png` |

## Ronda 4: Validaciones
| Prueba | Datos ingresados / Acción | Resultado esperado | Resultado obtenido | Estado | Evidencia |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **V1** | Código vacío o >10 caracteres | Rechaza (Aviso: entre 1 y 10 caracteres) | Aviso de validación mostrado | OK | `evidencias/V1.png` |
| **V2** | Nombre "AB" (2 caracteres) | Rechaza (Aviso: entre 3 y 80 caracteres) | Aviso de validación mostrado | OK | `evidencias/V2.png` |
| **V3** | Categoría no seleccionada | Rechaza (Aviso: Seleccione categoría) | Aviso de validación mostrado | OK | `evidencias/V3.png` |
| **V4** | Precio con letras ("abc") | Rechaza (Aviso: Formato inválido) | Aviso de validación mostrado | OK | `evidencias/V4.png` |
| **V5** | Precio 0 o negativo | Rechaza (Aviso: Mayor que 0) | Aviso de validación mostrado | OK | `evidencias/V5.png` |
| **V6** | Precio con 3 decimales (12.345) | Rechaza (Aviso: Máximo 2 decimales) | Aviso de validación mostrado | OK | `evidencias/V6.png` |
| **V7** | Stock con letras o decimales | Rechaza (Aviso: Número entero) | Aviso de validación mostrado | OK | `evidencias/V7.png` |
| **V8** | Stock negativo (-1) | Rechaza (Aviso: No puede ser negativo) | Aviso de validación mostrado | OK | `evidencias/V8.png` |
| **V9** | Código duplicado ("p001") | Rechaza (Aviso: Ya existe el código) | Aviso de duplicidad mostrado | OK | `evidencias/V9.png` |
| **V9b** | Actualizar P001 sin cambiar código | Permite la edición de sí mismo | Aviso "Producto actualizado" | OK | `evidencias/V9b_editar_propio.png` |
| **V10** | Actualizar sin seleccionar fila | Rechaza (Aviso: Seleccione un producto) | Aviso de validación mostrado | OK | `evidencias/V10.png` |
| **BD** | Verificar base de datos al final | La base no debe registrar las validaciones | 12 filas originales intactas | OK | `evidencias/V_select_12filas.png` |

## Ronda 5: Excepciones
| Prueba | Acción realizada | Resultado esperado | Resultado obtenido | Estado | Evidencia |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **E1** | Iniciar app con puerto de BD incorrecto | Aviso visual "Fallo de conexión" | Excepción capturada y alerta mostrada | OK | `evidencias/E1.png` |
| **E2_consola** | Ejecutar PruebasDAO (duplicado y check) | Ver SQLState 23505 y 23514 | Códigos SQLState listados en consola | OK | `evidencias/E2_consola.png` |
| **E3_actualizar** | Cargar producto, borrarlo en BD y Actualizar | Aviso de que el producto ya no existe | Alerta mostrada y manejada | OK | `evidencias/E3_actualizar.png` |
| **E3_eliminar** | Seleccionar producto, borrarlo en BD y Eliminar | Aviso de que el producto ya no existe | Alerta mostrada y manejada | OK | `evidencias/E3_eliminar.png` |
| **E4_log** | Ejecutar PruebasDAO | Ver logs personalizados `[ERROR] Operación:` | Consola muestra el log formateado | OK | `evidencias/E4_log.png` |

## Ronda 6: Restricciones y Git
| Prueba | Acción realizada | Resultado esperado | Resultado obtenido | Estado | Evidencia |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **SQL_restricciones** | Ejecutar INSERT inválido directo en pgAdmin | Base de datos rechaza por restricción CHECK | Error "violates check constraint" | OK | `evidencias/SQL_restricciones.png` |
| **git_log** | Ejecutar `git log --oneline` en terminal | Listar 3 o más commits del proyecto | Historial visualizado correctamente | OK | `evidencias/git_log.png` |