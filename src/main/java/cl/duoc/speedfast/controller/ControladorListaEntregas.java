package cl.duoc.speedfast.controller;

import cl.duoc.speedfast.model.dao.EntregaDAO;
import cl.duoc.speedfast.model.entity.Entrega;
import cl.duoc.speedfast.view.VentanaListaEntregas;
import cl.duoc.speedfast.view.VentanaRegistroEntrega;
import cl.duoc.speedfast.view.VentanaRegistroPedido;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador especializado encargado de gestionar el flujo de la pantalla de historial de entregas.
 * Recupera de forma segura las asignaciones desde MySQL y alimenta de forma reactiva la tabla visual.
 */
public class ControladorListaEntregas {

    private final VentanaListaEntregas ventanaListaEntregas;
    private final EntregaDAO entregaDAO = new EntregaDAO();
    private List<Entrega> listaEntregas;

    /**
     * Constructor principal encargado de enlazar la ventana de historial, activar el botón de retorno,
     * y gatillar la lectura atómica de persistencia para pintar los registros en caliente en la interfaz.
     *
     * @param ventanaListaEntregas Instancia activa de la ventana visual con el JTable de entregas.
     */
    public ControladorListaEntregas(VentanaListaEntregas ventanaListaEntregas) {
        this.ventanaListaEntregas = ventanaListaEntregas;

        inicializarListeners();
        obtenerDatosDesdeBD();
        cargarDatosEnTabla();
    }

    private void inicializarListeners() {
        ventanaListaEntregas.addVolverAtrasListener(e -> ventanaListaEntregas.cerrarVentana());
        ventanaListaEntregas.addAgregarListener(e -> abrirFormularioNuevo());
        ventanaListaEntregas.addEliminarListener(e -> procesarEliminacion());
    }

    private void obtenerDatosDesdeBD() {
        try {
            listaEntregas = entregaDAO.readAll();

        } catch (SQLException ex) {
            ventanaListaEntregas.mostrarMensajeError("Error al obtener los datos de la base de datos: " + ex.getMessage());
            listaEntregas = new ArrayList<>();
        }
    }

    private void cargarDatosEnTabla() {
        ventanaListaEntregas.actualizarTabla(listaEntregas);
    }

    private void abrirFormularioNuevo() {
        VentanaRegistroEntrega ventanaRegistroEntrega = new VentanaRegistroEntrega();
        ventanaListaEntregas.setVisible(false);
        new ControladorRegistroEntrega(ventanaRegistroEntrega, this);
        ventanaRegistroEntrega.setVisible(true);
    }

    private void procesarEliminacion() {
        int idSel = ventanaListaEntregas.getIdEntregaSeleccionada();
        if (idSel == -1) {
            ventanaListaEntregas.mostrarMensajeError("Debe seleccionar una entrega para eliminar.");
            return;
        }

        boolean confirmacion = ventanaListaEntregas.confirmarEliminacion();

        if (confirmacion) {
            try {
                entregaDAO.delete(idSel);
                obtenerDatosDesdeBD();
                cargarDatosEnTabla();

                ventanaListaEntregas.mostrarMensajeConfirmacion("Entrega eliminada correctamente.");

            } catch (SQLException ex) {
                ventanaListaEntregas.mostrarMensajeError("Error al eliminar la entrega: " + ex.getMessage());
            }
        }
    }

    public void refrescarYMostrar() {
        obtenerDatosDesdeBD();
        cargarDatosEnTabla();
        ventanaListaEntregas.setVisible(true);
    }
}
