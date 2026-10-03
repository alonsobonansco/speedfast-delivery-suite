# 🚚 SpeedFast App

Actividad sumativa 3 (Semana 8)

---

## 📖 Descripción

Este proyecto es una aplicación de escritorio desarrollada en Java 25 y Maven que gestiona y simula el flujo logístico
de una empresa de despacho a domicilio en tiempo real. Combina un entorno gráfico en Java Swing, persistencia relacional
pura en MySQL y un motor de Simulación Multihilo Concurrente para procesar las rutas en paralelo de forma coordinada.  

En esta versión, el sistema fue refactorizado bajo estándares de Clean Code, incorporando el ciclo de mantenimiento CRUD
completo, optimizaciones de rendimiento en memoria y blindaje de seguridad industrial.


---

## 🚀 Características Principales

La arquitectura del sistema resuelve la sincronización entre los hilos en memoria RAM y las tablas físicas en el disco
duro bajo cuatro pilares estrictos:

1. **Ciclo de Mantenimiento CRUD Completo:** Las pantallas de consulta histórica evolucionaron a paneles administrativos
integrados. Además de la lectura (`READ`) e inserción (`CREATE`), el sistema implementa la modificación procedimental
de columnas (`UPDATE`) y la baja física de registros por clave primaria (`DELETE`), controlando de forma defensiva
las
excepciones relacionales.

2. **Buscadores Predictivos Cruzados en RAM (Java Streams):** Para garantizar un rendimiento óptimo de red y UI, los
filtros de búsqueda en las pantallas históricas (Tipo, Estado, ID Pedido y Repartidores) operan localmente en la 
memoria RAM utilizando operadores Lambda y Streams de Java. El historial de entregas procesa una búsqueda predictiva
de coincidencia parcial (`.contains()`) emulando una cláusula `LIKE` sin estresar al servidor MySQL con
consultas concurrentes redundantes.

3. **Externalización de Credenciales (Seguridad de Entorno):** Siguiendo las mejores prácticas de ciberseguridad
corporativa, la clase `ConexionBD` fue purificada, eliminando las contraseñas escritas en texto plano (*hardcoded*).
El driver JDBC recupera de forma segura el usuario y la clave de acceso directamente desde el sistema operativo
mediante variables de entorno (`System.getenv("DB_USER")` y `System.getenv("DB_PASSWORD")`).

4. **Consistencia Transaccional Atómica:** El formulario de asignación manual ejecuta una transacción SQL unificada
dentro de `EntregaDAO`. Apaga el `setAutoCommit(false)` para garantizar que el `INSERT` de la entrega y el `UPDATE`
del estado del pedido ocurran bajo una misma unidad de trabajo. Si uno falla, se ejecuta un `rollback()` inmediato
impidiendo la corrupción de datos.

---

## 📁 Estructura del Proyecto

```text
speedfast-s8/
├── src/
│   └── main/
│       ├── java/
│       │   └── cl/
│       │       └── duoc/
│       │           └── speedfast/
│       │               ├── Main.java                              # Inicializa la GUI en el EDT y ejecuta diagnóstico JDBC
│       │               ├── database/
│       │               │   └── ConexionBD.java                    # Infraestructura de fábrica para conexiones MySQL
│       │               ├── controller/                            # Capa del Controlador (Orquestación de Negocio)
│       │               │   ├── ControladorPrincipal.java
│       │               │   ├── ControladorRepartoPedidos.java     # Motor de simulación y control de hilos
│       │               │   ├── ControladorRegistroPedido.java
│       │               │   ├── ControladorRegistroRepartidor.java
│       │               │   ├── ControladorRegistroEntrega.java
│       │               │   ├── ControladorListaPedidos.java
│       │               │   ├── ControladorListaRepartidores.java
│       │               │   └── ControladorListaEntregas.java
│       │               ├── model/
│       │               │   ├── dao/                               # Capa de Datos (Encapsulamiento de Sentencias JDBC)
│       │               │   │   ├── PedidoDAO.java
│       │               │   │   ├── RepartidorDAO.java
│       │               │   │   └── EntregaDAO.java
│       │               │   └── entity/                            # Capa del Modelo
│       │               │       ├── Pedido.java
│       │               │       ├── Repartidor.java                # Clase entidad con el ciclo de Runnable activo
│       │               │       ├── Entrega.java
│       │               │       ├── TipoPedido.java                # Enum
│       │               │       └── EstadoPedido.java              # Enum
│       │               ├── view/                                  # Capa de la Vista (Interfaces Gráficas Swing)
│       │               │   ├── VentanaPrincipal.java
│       │               │   ├── VentanaRegistroPedido.java
│       │               │   ├── VentanaRegistroRepartidor.java
│       │               │   ├── VentanaRegistroEntrega.java
│       │               │   ├── VentanaListaPedidos.java
│       │               │   ├── VentanaListaRepartidores.java
│       │               │   └── VentanaListaEntregas.java
│       │               └── event/
│       │                   └── LogListener.java                  # Interfaz funcional para desacoplamiento reactivo de bitácora
│       └── resources/
│           └── schema.sql                                        # Script de inicialización, llaves foráneas y restricciones
└── pom.xml                                                       # Archivo de configuración de dependencias de Maven
```

---

## 🛠️ Instrucciones para clonar y ejecutar

### Requisitos del sistema:

* **JDK:** Java 25 (LTS) o superior  
* **IDE Recomendado:** IntelliJ IDEA

### Pasos:

1. Clonar el repositorio desde la terminal de la computadora o IDE:  
   git clone https://github.com/alonsobonansco/speedfast-s8.git
2. Ir a File →️ Open y seleccionar la carpeta raíz del proyecto (la carpeta que contiene el archivo pom.xml).

3. Ejecutar el `Main` desde su clase en el paquete raíz `cl.duoc.speedfast` *(La aplicación se abrirá, pero arrojará un
aviso de que faltan las credenciales en el sistema, lo cual es normal).*
4. En la barra superior de IntelliJ, justo al lado del botón verde de "Play",
hacer clic en la lista desplegable que ahora dice `Main` y seleccionar **Edit Configuration...**
5. Hacer clic en el ícono de la pequeña carpeta o en el símbolo de más (`+`) al
final de ese campo e ingresar las dos variables de entorno con tus datos locales de MySQL:
    * **Name:** `DB_USER` | **Value:** `tu_usuario_root` *(Por ejemplo: root)*
    * **Name:** `DB_PASSWORD` | **Value:** `tu_contraseña_de_mysql`
6. Hacer clic en **Apply** y luego en **OK** para cerrar el panel de configuración de IntelliJ. 
7. Volver a presionar el botón verde de **Play** en la barra superior. La aplicación
**SpeedFast** se conectará de forma segura a tu base de datos y levantará el panel administrativo al instante.

---

## 👤 Autor

Alonso Bonansco Vergara  
Desarrollo Orientado a Objetos II - 004A  
Analista Programador Computacional
