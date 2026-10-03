package cl.duoc.speedfast.model.dao;

import cl.duoc.speedfast.database.ConexionBD;
import cl.duoc.speedfast.model.entity.Entrega;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de Acceso a Datos (DAO) encargada de centralizar la persistencia,
 * lectura histórica e inserciones transaccionales de las Entregas en la base de datos MySQL.
 */
public class EntregaDAO {

    /**
     * Inserta un registro de entrega y actualiza el estado del pedido de forma atómica.
     * Utiliza una transacción manual (commit/rollback) para garantizar la integridad referencial.
     *
     * @param entrega Objeto entidad que contiene las llaves foráneas y marcas de tiempo a registrar.
     * @throws SQLException Si falla alguna consulta, si no se afectan filas, o si se gatilla un Rollback en MySQL.
     */
    public void create(Entrega entrega) throws SQLException {
        String sqlEntrega = "INSERT INTO entregas (id_pedido, id_repartidor, fecha, hora) VALUES (?, ?, ?, ?)";
        String sqlPedido = "UPDATE pedidos SET estado = 'EN_REPARTO' WHERE id = ?";

        Connection conn = null;

        try {
            conn = ConexionBD.obtenerConexion();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtEntrega = conn.prepareStatement(sqlEntrega);
                 PreparedStatement pstmtPedido = conn.prepareStatement(sqlPedido)) {

                pstmtEntrega.setInt(1, entrega.getIdPedido());
                pstmtEntrega.setInt(2, entrega.getIdRepartidor());
                pstmtEntrega.setDate(3, Date.valueOf(entrega.getFecha()));
                pstmtEntrega.setTime(4, Time.valueOf(entrega.getHora()));

                pstmtEntrega.executeUpdate();

                pstmtPedido.setInt(1, entrega.getIdPedido());

                // Validación de integridad en la capa de persistencia
                int filasAfectadas = pstmtPedido.executeUpdate();

                if (filasAfectadas == 0) {
                    throw new SQLException("No se actualizó ningún pedido");
                }

                conn.commit();
            }

        } catch (SQLException ex) {
            if (conn != null) {
                conn.rollback();
            }

            throw ex;

        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    /**
     * Extrae el historial plano completo de todas las asignaciones de entregas registradas en MySQL.
     * Mapea de forma directa las fechas y horas a los tipos modernos de java.time.
     *
     * @return Una {@link List} que contiene los objetos Entrega estructurados.
     * @throws SQLException Si ocurre un error durante el escaneo del ResultSet en MySQL.
     */
    public List<Entrega> readAll() throws SQLException {
        List<Entrega> listaEntregas = new ArrayList<>();
        String sql = "SELECT * FROM entregas";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Entrega entrega = new Entrega(
                        rs.getInt("id_pedido"),
                        rs.getInt("id_repartidor"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getTime("hora").toLocalTime()
                );
                entrega.setIdEntrega(rs.getInt("id"));
                listaEntregas.add(entrega);
            }
        }

        return listaEntregas;
    }

    /**
     * Elimina permanentemente un registro de asignación de entrega mediante su clave primaria.
     * Se agregó para evitar problemas de integridad relacional al eliminar pedidos o repartidores asociados.
     *
     * @param idEntrega Identificador numérico único de la entrega a eliminar físicamente en MySQL.
     * @throws SQLException Si ocurre una falla de comunicación en el driver JDBC o restricciones relacionales.
     */
    public void delete(int idEntrega) throws SQLException {
        String sql = "DELETE FROM entregas WHERE id = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idEntrega);
            pstmt.executeUpdate();
        }
    }
}
