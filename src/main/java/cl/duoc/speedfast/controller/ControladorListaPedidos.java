package cl.duoc.speedfast.controller;

import cl.duoc.speedfast.model.dao.PedidoDAO;
import cl.duoc.speedfast.model.entity.Pedido;
import cl.duoc.speedfast.view.VentanaListaPedidos;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador especializado encargado de gestionar el flujo de la pantalla de listado de pedidos históricos.
 * Recupera de forma segura los registros desde MySQL y alimenta de forma reactiva la tabla visual.
 */
public class ControladorListaPedidos {

    private final VentanaListaPedidos ventanaListaPedidos;
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private List<Pedido> listaPedidos;

    /**
     * Constructor principal encargado de enlazar la ventana de listado, activar el botón de retorno,
     * y gatillar la lectura atómica de persistencia para pintar los registros en caliente en la interfaz.
     *
     * @param ventanaListaPedidos Instancia activa de la ventana visual con el JTable de pedidos.
     */
    public ControladorListaPedidos(VentanaListaPedidos ventanaListaPedidos) {
        this.ventanaListaPedidos = ventanaListaPedidos;

        inicializarListeners();
        obtenerDatosDesdeBD();
        cargarDatosEnTabla();
    }

    private void inicializarListeners() {
        ventanaListaPedidos.addVolverAtrasListener(e -> ventanaListaPedidos.cerrarVentana());
    }

    private void cargarDatosEnTabla() {
        this.ventanaListaPedidos.actualizarTabla(this.listaPedidos);
    }

    private void obtenerDatosDesdeBD() {
        try {
            this.listaPedidos = pedidoDAO.readAll();

        } catch (SQLException ex) {
            ventanaListaPedidos.mostrarMensajeError("Error al obtener los datos de la base de datos: " + ex.getMessage());
            listaPedidos = new ArrayList<>();
        }
    }
}
