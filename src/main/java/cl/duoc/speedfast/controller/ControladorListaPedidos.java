package cl.duoc.speedfast.controller;

import cl.duoc.speedfast.model.dao.PedidoDAO;
import cl.duoc.speedfast.model.entity.Pedido;
import cl.duoc.speedfast.view.VentanaListaPedidos;
import cl.duoc.speedfast.view.VentanaRegistroPedido;

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
        ventanaListaPedidos.addAgregarListener(e -> abrirFormularioNuevo());
        ventanaListaPedidos.addEditarListener(e -> abrirFormularioEditar());
        ventanaListaPedidos.addEliminarListener(e -> procesarEliminacion());
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

    private void abrirFormularioNuevo() {
        VentanaRegistroPedido ventanaRegistroPedido = new VentanaRegistroPedido();
        ventanaListaPedidos.setVisible(false);
        new ControladorRegistroPedido(ventanaRegistroPedido, null, this);
        ventanaRegistroPedido.setVisible(true);
    }

    private void abrirFormularioEditar() {
        int idSel = ventanaListaPedidos.getIdPedidoSeleccionado();
        if (idSel == -1) {
            ventanaListaPedidos.mostrarMensajeError("Debe seleccionar un pedido para editar.");
            return;
        }

        Pedido pedidoAEditar = listaPedidos.stream()
                .filter(p -> p.getIdPedido() == idSel)
                .findFirst().orElse(null);

        if (pedidoAEditar == null) return;

        ventanaListaPedidos.setVisible(false);

        VentanaRegistroPedido ventanaRegistroPedido = new VentanaRegistroPedido();

        new ControladorRegistroPedido(ventanaRegistroPedido, pedidoAEditar, this);
        ventanaRegistroPedido.setVisible(true);
    }

    private void procesarEliminacion() {
        int idSel = ventanaListaPedidos.getIdPedidoSeleccionado();
        if (idSel == -1) {
            ventanaListaPedidos.mostrarMensajeError("Debe seleccionar un pedido para eliminar.");
            return;
        }

        // Es buena práctica pedir confirmación antes de eliminar un registro
        boolean confirmacion = ventanaListaPedidos.confirmarEliminacion();

        if (confirmacion) {
            try {
                pedidoDAO.delete(idSel);
                obtenerDatosDesdeBD();
                cargarDatosEnTabla();
                ventanaListaPedidos.mostrarMensajeConfirmacion("Pedido eliminado correctamente.");

            } catch (SQLException ex) {
                ventanaListaPedidos.mostrarMensajeError("Error al eliminar el pedido: " + ex.getMessage());
            }
        }
    }

    public void refrescarYMostrar() {
        obtenerDatosDesdeBD();
        cargarDatosEnTabla();
        ventanaListaPedidos.setVisible(true);
    }
}
