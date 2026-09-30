package cl.duoc.speedfast.model.dao;

import cl.duoc.speedfast.database.ConexionBD;
import cl.duoc.speedfast.model.entity.Repartidor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de Acceso a Datos (DAO) encargada de centralizar las operaciones de persistencia,
 * inserción y lectura de la entidad Repartidor físicamente en la base de datos MySQL.
 */
public class RepartidorDAO {

    /**
     * Inserta un nuevo registro de repartidor en el motor de base de datos relacional.
     *
     * @param repartidor Objeto entidad que contiene los datos del trabajador a registrar.
     * @throws SQLException Si ocurre un error de comunicación, sintaxis o restricciones en MySQL.
     */
    public void create(Repartidor repartidor) throws SQLException {
        String sql = "INSERT INTO repartidores (nombre) VALUES (?)";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, repartidor.getNombreRepartidor());

            pstmt.executeUpdate();
        }

    }

    /**
     * Extrae la lista completa y actualizada de todos los repartidores almacenados en la base de datos.
     * Reconstruye los registros relacionales transformándolos en instancias de la entidad Java.
     *
     * @return Una {@link List} que contiene los objetos Repartidor encontrados en el sistema.
     * @throws SQLException Si falla la ejecución de la consulta de lectura en MySQL.
     */
    public List<Repartidor> readAll() throws SQLException {
        List<Repartidor> listaRepartidores = new ArrayList<>();
        String sql = "SELECT * FROM repartidores";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                listaRepartidores.add(new Repartidor(
                        rs.getInt("id"),
                        rs.getString("nombre")));
            }
        }

        return listaRepartidores;
    }

    public void update(Repartidor repartidor) throws SQLException {
        String sql = "UPDATE repartidores SET nombre = ? WHERE id = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

             pstmt.setString(1, repartidor.getNombreRepartidor());
             pstmt.setInt(2, repartidor.getIdRepartidor());
             pstmt.executeUpdate();
        }
    }

    public void delete(int idRepartidor) throws SQLException {
        String sql = "DELETE FROM repartidores WHERE id = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

             pstmt.setInt(1, idRepartidor);
             pstmt.executeUpdate();
        }
    }
}
