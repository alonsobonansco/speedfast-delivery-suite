package cl.duoc.speedfast.model.dao;

import cl.duoc.speedfast.database.ConexionBD;
import cl.duoc.speedfast.model.entity.EstadoPedido;
import cl.duoc.speedfast.model.entity.Pedido;
import cl.duoc.speedfast.model.entity.TipoPedido;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de Acceso a Datos (DAO) encargada de centralizar las operaciones de persistencia,
 * inserción, actualización y lectura de la entidad Pedido físicamente en la base de datos MySQL.
 */
public class PedidoDAO {

    /**
     * Inserta un nuevo registro de pedido en la base de datos mapeando los Enums correspondientes.
     *
     * @param pedido Objeto entidad que contiene los datos del despacho a registrar.
     * @throws SQLException Si ocurre un error de comunicación o restricciones sintácticas en MySQL.
     */
    public void create(Pedido pedido) throws SQLException {
        String sql = "INSERT INTO pedidos (direccion, tipo, estado) VALUES (?, ?, ?)";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pedido.getDireccionEntrega());
            pstmt.setString(2, pedido.getTipoPedido().name());
            pstmt.setString(3, pedido.getEstadoPedido().name());

            pstmt.executeUpdate();
        }
    }

    /**
     * Extrae la lista completa histórica de todos los pedidos almacenados en la base de datos,
     * transformando los textos VARCHAR de vuelta a sus Enums tipados en Java.
     *
     * @return Una {@link List} que contiene todos los objetos Pedido encontrados.
     * @throws SQLException Si falla la ejecución de la consulta de lectura en MySQL.
     */
    public List<Pedido> readAll() throws SQLException {
        List<Pedido> listaPedidos = new ArrayList<>();
        String sql = "SELECT * FROM pedidos";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String textoTipo = rs.getString("tipo");
                String textoEstado = rs.getString("estado");

                TipoPedido tipoPedido = TipoPedido.valueOf(textoTipo.toUpperCase());
                EstadoPedido estadoPedido = EstadoPedido.valueOf(textoEstado.toUpperCase());

                Pedido pedido = new Pedido(
                        rs.getInt("id"),
                        rs.getString("direccion"),
                        tipoPedido);

                pedido.setEstadoPedido(estadoPedido);
                listaPedidos.add(pedido);
            }
        }

        return listaPedidos;
    }

    /**
     * Recupera exclusivamente los registros de pedidos que se encuentran en estado 'PENDIENTE'
     * para abastecer dinámicamente los componentes visuales de asignación.
     *
     * @return Una {@link List} con los objetos Pedido aptos para ser asignados en ruta.
     * @throws SQLException Si ocurre una falla en el filtro de lectura en MySQL.
     */
    public List<Pedido> listarPendientes() throws SQLException {
        List<Pedido> listaPendientes = new ArrayList<>();

        String sql = "SELECT * FROM pedidos WHERE estado = 'PENDIENTE'";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String textoTipo = rs.getString("tipo");
                String textoEstado = rs.getString("estado");

                TipoPedido tipoPedido = TipoPedido.valueOf(textoTipo.toUpperCase());
                EstadoPedido estadoPedido = EstadoPedido.valueOf(textoEstado.toUpperCase());

                Pedido pedido = new Pedido(
                        rs.getInt("id"),
                        rs.getString("direccion"),
                        tipoPedido);

                pedido.setEstadoPedido(estadoPedido);
                listaPendientes.add(pedido);
            }
        }

        return listaPendientes;
    }

    /**
     * Modifica de manera directa el estado de un registro de pedido utilizando tipado fuerte de Enum.
     *
     * @param idPedido    ID único correlativo del pedido a modificar.
     * @param nuevoEstado Siguiente estado del flujo de negocio (EN_REPARTO, ENTREGADO).
     * @throws SQLException Si falla la ejecución del comando UPDATE en el motor relacional.
     */
    public void actualizarEstado(int idPedido, EstadoPedido nuevoEstado) throws SQLException {
        String sql = "UPDATE pedidos SET estado = ? WHERE id = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nuevoEstado.name());
            pstmt.setInt(2, idPedido);

            pstmt.executeUpdate();
        }
    }
}
