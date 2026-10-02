package cl.duoc.speedfast.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Proveedor de infraestructura centralizado encargado de gestionar los parámetros técnicos
 * y el levantamiento de conexiones físicas hacia el motor de base de datos relacional MySQL.
 */
public final class ConexionBD {

    private static final String URL = "jdbc:mysql://localhost:3306/speedfast_db";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";

    private ConexionBD() {}

    /**
     * Establece y retorna un canal activo de comunicación (Connection) utilizando el driver JDBC de MySQL.
     *
     * @return Una instancia de {@link Connection} abierta hacia la base de datos configurada.
     * @throws SQLException Si las credenciales son incorrectas, el servidor está apagado o falla la red.
     */
    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
