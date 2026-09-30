package cl.duoc.speedfast.controller;

import cl.duoc.speedfast.model.dao.RepartidorDAO;
import cl.duoc.speedfast.model.entity.Repartidor;
import cl.duoc.speedfast.view.VentanaListaRepartidores;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador especializado encargado de gestionar el flujo de la pantalla de listado de repartidores.
 * Recupera de forma segura el catálogo histórico de MySQL y alimenta de forma reactiva el JTable visual.
 */
public class ControladorListaRepartidores {

    private final VentanaListaRepartidores ventanaListaRepartidores;
    private final RepartidorDAO repartidorDAO = new RepartidorDAO();
    private List<Repartidor> listaRepartidores;

    /**
     * Constructor principal encargado de enlazar la ventana de listado, activar el botón de retorno,
     * y gatillar la lectura atómica de persistencia para pintar los registros en caliente en la interfaz.
     *
     * @param ventanaListaRepartidores Instancia activa de la ventana visual con el JTable de repartidores.
     */
    public ControladorListaRepartidores(VentanaListaRepartidores ventanaListaRepartidores) {
        this.ventanaListaRepartidores = ventanaListaRepartidores;

        inicializarListeners();
        obtenerDatosDesdeBD();
        cargarDatosEnTabla();
    }

    private void inicializarListeners() {
        ventanaListaRepartidores.addVolverAtrasListener(e -> ventanaListaRepartidores.cerrarVentana());
    }

    private void cargarDatosEnTabla() {
        this.ventanaListaRepartidores.actualizarTabla(this.listaRepartidores);
    }

    private void obtenerDatosDesdeBD() {
        try {
            this.listaRepartidores = repartidorDAO.readAll();

        } catch (SQLException ex) {
            ventanaListaRepartidores.mostrarMensajeError("Error al obtener los datos: " + ex.getMessage());
            listaRepartidores = new ArrayList<>();
        }
    }
}
