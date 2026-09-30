package cl.duoc.speedfast.controller;

import cl.duoc.speedfast.model.dao.EntregaDAO;
import cl.duoc.speedfast.model.entity.Entrega;
import cl.duoc.speedfast.view.VentanaListaEntregas;

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
    }

    private void obtenerDatosDesdeBD() {
        try {
            listaEntregas = entregaDAO.listarTodos();

        } catch (SQLException ex) {
            ventanaListaEntregas.mostrarMensajeError("Error al obtener los datos de la base de datos: " + ex.getMessage());
            listaEntregas = new ArrayList<>();
        }
    }

    private void cargarDatosEnTabla() {
        ventanaListaEntregas.actualizarTabla(listaEntregas);
    }
}
