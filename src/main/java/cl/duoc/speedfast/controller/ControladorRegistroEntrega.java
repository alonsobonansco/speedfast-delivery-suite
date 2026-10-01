package cl.duoc.speedfast.controller;

import cl.duoc.speedfast.model.dao.EntregaDAO;
import cl.duoc.speedfast.model.dao.PedidoDAO;
import cl.duoc.speedfast.model.dao.RepartidorDAO;
import cl.duoc.speedfast.model.entity.Entrega;
import cl.duoc.speedfast.model.entity.Pedido;
import cl.duoc.speedfast.model.entity.Repartidor;
import cl.duoc.speedfast.view.VentanaRegistroEntrega;

import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Controlador especializado encargado de gestionar el flujo del formulario de asignación manual.
 * Coordina la carga dinámica de JComboBoxes desde MySQL y procesa la vinculación transaccional.
 */
public class ControladorRegistroEntrega {

    private final VentanaRegistroEntrega ventanaRegistroEntrega;
    private final ControladorListaEntregas controladorListaEntregas;
    private final EntregaDAO entregaDAO = new EntregaDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final RepartidorDAO repartidorDAO = new RepartidorDAO();

    /**
     * Constructor principal que enlaza el formulario de asignación, gatilla los listeners
     * de los botones y pobla los componentes gráficos consultando de forma inicial los DAOs.
     *
     * @param ventanaRegistroEntrega Instancia activa del formulario visual de asignación.
     */
    public ControladorRegistroEntrega(VentanaRegistroEntrega ventanaRegistroEntrega,
                                      ControladorListaEntregas controladorListaEntregas) {
        this.ventanaRegistroEntrega = ventanaRegistroEntrega;
        this.controladorListaEntregas = controladorListaEntregas;

        inicializarListeners();
        cargarDatosEnComponentes();
    }

    private void cargarDatosEnComponentes() {
        try {
            ventanaRegistroEntrega.cargarPedidos(pedidoDAO.listarPendientes());
            ventanaRegistroEntrega.cargarRepartidores(repartidorDAO.readAll());
        } catch (SQLException ex) {
            ventanaRegistroEntrega.mostrarMensajeError("Error al cargar los datos: " + ex.getMessage());
        }
    }

    private void inicializarListeners() {
        ventanaRegistroEntrega.addGuardarListener(e -> procesarAsignacion());
        ventanaRegistroEntrega.addVolverAtrasListener(e -> ventanaRegistroEntrega.cerrarVentana());
        ventanaRegistroEntrega.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (controladorListaEntregas != null) {
                    controladorListaEntregas.refrescarYMostrar();
                }
            }
        });
    }

    private void procesarAsignacion() {
        Pedido pedidoSelec = (Pedido) ventanaRegistroEntrega.getComboPedidos().getSelectedItem();
        Repartidor repartidorSelec = (Repartidor) ventanaRegistroEntrega.getComboRepartidores().getSelectedItem();

        if (pedidoSelec == null || repartidorSelec == null) {
            ventanaRegistroEntrega.mostrarMensajeError("Debe seleccionar un pedido y un repartidor obligatoriamente.");
            return;
        }

        try {
            Entrega nuevaEntrega = new Entrega(
                    pedidoSelec.getIdPedido(),
                    repartidorSelec.getIdRepartidor(),
                    LocalDate.now(),
                    LocalTime.now()
            );

            entregaDAO.create(nuevaEntrega);

            ventanaRegistroEntrega.getComboPedidos().removeItem(pedidoSelec);

            ventanaRegistroEntrega.mostrarMensajeConfirmacion("Entrega registrada con éxito en la Base de Datos");

        } catch (IllegalArgumentException | DateTimeException e) {
            ventanaRegistroEntrega.mostrarMensajeError("Error al asignar la entrega: " + e.getMessage());

        } catch (SQLException ex) {
            ventanaRegistroEntrega.mostrarMensajeError("Error al guardar la entrega en la base de datos: " + ex.getMessage());
        }
    }
}
