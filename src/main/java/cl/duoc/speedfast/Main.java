package cl.duoc.speedfast;

import cl.duoc.speedfast.controller.ControladorPrincipal;
import cl.duoc.speedfast.database.ConexionBD;
import cl.duoc.speedfast.view.VentanaPrincipal;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Punto de entrada inicial (Bootstrap) de la suite de software SpeedFast.
 * Inicializa el Event Dispatch Thread para levantar la interfaz gráfica
 * y ejecuta un diagnóstico de conexión temprano hacia el motor MySQL.
 */
public class Main {

    /**
     * Método de arranque global de la aplicación.
     *
     * @param args Argumentos de la línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventanaPrincipal = new VentanaPrincipal();
            new ControladorPrincipal(ventanaPrincipal);
            ventanaPrincipal.setVisible(true);
        });

        // Se deja para pruebas
        try (Connection connection = ConexionBD.obtenerConexion()) {
            if (connection != null) {
                System.out.println("Conexión a la base de datos establecida correctamente.");
            } else {
                System.out.println("No se pudo establecer la conexión a la base de datos.");
            }
        } catch (SQLException ex) {
            System.out.println("Error en la conexión a la base de datos: " + ex.getMessage());
        }
    }
}
