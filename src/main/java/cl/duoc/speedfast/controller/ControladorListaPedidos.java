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
        ventanaListaPedidos.addFiltroTipoListener(e -> aplicarFiltrosTipoEstado());
        ventanaListaPedidos.addFiltroEstadoListener(e -> aplicarFiltrosTipoEstado());
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

    /**
     * Oculta de manera temporal la ventana del catálogo y levanta la vista del formulario
     * en limpio pasándole un parámetro de control nulo para inicializar el modo de inserción (CREATE).
     */
    private void abrirFormularioNuevo() {
        VentanaRegistroPedido ventanaRegistroPedido = new VentanaRegistroPedido();
        ventanaListaPedidos.setVisible(false);
        new ControladorRegistroPedido(ventanaRegistroPedido, null, this);
        ventanaRegistroPedido.setVisible(true);
    }

    /**
     * Captura el identificador correlativo único de la cuadrícula, recupera la entidad correspondiente
     * de la memoria RAM e inyecta la instancia en el formulario visual para activar el modo de modificación (UPDATE).
     */
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

    /**
     * Ejecuta la baja física del pedido por clave primaria (DELETE). Solicita la confirmación
     * gráfica del usuario y procesa las excepciones centralizadas por fallas de restricción relacional.
     */
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
                if (ex.getErrorCode() == 1451) {
                    ventanaListaPedidos.mostrarMensajeError(
                            "No se puede eliminar este pedido porque tiene un historial de entregas asociado.\n" +
                                    "Para borrarlo, primero debes eliminar sus registros en el módulo de entregas."
                    );
                } else {
                    ventanaListaPedidos.mostrarMensajeError("Error al eliminar el pedido: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Procesa de manera reactiva e instantánea el filtrado de registros en la memoria RAM
     * mediante operadores lambda sin sin hacer consultas adicionales a MySQL, cruzando las opciones de
     * tipo y estado.
     */
    public void aplicarFiltrosTipoEstado() {
        String tipoSeleccionado = ventanaListaPedidos.getFiltroTipoSeleccionado();
        String estadoSeleccionado = ventanaListaPedidos.getFiltroEstadoSeleccionado();

        List<Pedido> pedidosFiltrados = listaPedidos.stream()
                .filter(p -> tipoSeleccionado.equals("Todos") ||
                        p.getTipoPedido().name().equalsIgnoreCase(tipoSeleccionado))
                .filter(p -> estadoSeleccionado.equals("Todos") ||
                        p.getEstadoPedido().name().equalsIgnoreCase(estadoSeleccionado))
                .toList();

        ventanaListaPedidos.actualizarTabla(pedidosFiltrados);
    }

    public void refrescarYMostrar() {
        obtenerDatosDesdeBD();
        aplicarFiltrosTipoEstado();
        ventanaListaPedidos.setVisible(true);
    }
}
