package cl.duoc.speedfast.controller;

import cl.duoc.speedfast.model.dao.EntregaDAO;
import cl.duoc.speedfast.model.dao.RepartidorDAO;
import cl.duoc.speedfast.model.entity.Entrega;
import cl.duoc.speedfast.model.entity.Repartidor;
import cl.duoc.speedfast.view.VentanaListaEntregas;
import cl.duoc.speedfast.view.VentanaRegistroEntrega;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
    private final RepartidorDAO repartidorDAO = new RepartidorDAO();
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
        cargarComboFiltroRepartidores();
        cargarDatosEnTabla();
    }

    private void inicializarListeners() {
        ventanaListaEntregas.addVolverAtrasListener(e -> ventanaListaEntregas.cerrarVentana());
        ventanaListaEntregas.addAgregarListener(e -> abrirFormularioNuevo());
        ventanaListaEntregas.addEliminarListener(e -> procesarEliminacion());
        ventanaListaEntregas.addFiltroRepartidorListener(e -> aplicarFiltrosHistorial());
        ventanaListaEntregas.addFiltroPedidoListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                aplicarFiltrosHistorial();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                aplicarFiltrosHistorial();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                aplicarFiltrosHistorial();
            }
        });
    }

    private void obtenerDatosDesdeBD() {
        try {
            listaEntregas = entregaDAO.readAll();

        } catch (SQLException ex) {
            ventanaListaEntregas.mostrarMensajeError("Error al obtener los datos de la base de datos: " + ex.getMessage());
            listaEntregas = new ArrayList<>();
        }
    }

    /**
     * Consulta de forma complementaria la tabla de repartidores en MySQL al iniciar la pantalla
     * para abastecer y poblar el componente JComboBox de filtrado con los trabajadores activos.
     */
    private void cargarComboFiltroRepartidores() {
        try {
            List<Object> repartidores = new ArrayList<>(repartidorDAO.readAll());
            ventanaListaEntregas.llenarRepartidoresComboBox(repartidores);

        } catch (SQLException ex) {
            ventanaListaEntregas.mostrarMensajeError("Error al cargar los repartidores: " + ex.getMessage());
        }
    }

    private void cargarDatosEnTabla() {
        ventanaListaEntregas.actualizarTabla(listaEntregas);
    }

    /**
     * Ejecuta el motor multifiltro cruzado de forma local en la memoria RAM utilizando Streams.
     * Evalúa de forma combinada la coincidencia predictiva del ID de pedido y la clave foránea del repartidor.
     */
    private void aplicarFiltrosHistorial() {
        String idPedidoTexto = ventanaListaEntregas.getIdPedidoFiltro();
        Object repartidorSel = ventanaListaEntregas.getRepartidorFiltro().getSelectedItem();

        if (repartidorSel == null) return;

        List<Entrega> listaFiltrada = this.listaEntregas.stream()
                .filter(e -> {
                    if (idPedidoTexto.isBlank()) return true;

                    String idPedidoStr = String.valueOf(e.getIdPedido());

                    return idPedidoStr.contains(idPedidoTexto);

                })
                .filter(e -> {
                    if (repartidorSel instanceof String) return true;

                    Repartidor r = (Repartidor) repartidorSel;
                    return e.getIdRepartidor() == r.getIdRepartidor();
                })
                .toList();

        ventanaListaEntregas.actualizarTabla(listaFiltrada);
    }

    /**
     * Oculta la vista histórica actual y levanta el formulario de asignación manual,
     * inyectando la referencia de control del padre para permitir la sincronización reversa.
     */
    private void abrirFormularioNuevo() {
        VentanaRegistroEntrega ventanaRegistroEntrega = new VentanaRegistroEntrega();
        ventanaListaEntregas.setVisible(false);
        new ControladorRegistroEntrega(ventanaRegistroEntrega, this);
        ventanaRegistroEntrega.setVisible(true);
    }

    /**
     * Maneja la baja de una asignación en el historial (DELETE) por su identificador correlativo primario.
     * Despliega la confirmación nativa de la vista y re-aplica los filtros en caliente al confirmar.
     */
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
                aplicarFiltrosHistorial();

                ventanaListaEntregas.mostrarMensajeConfirmacion("Entrega eliminada correctamente.");

            } catch (SQLException ex) {
                ventanaListaEntregas.mostrarMensajeError("Error al eliminar la entrega: " + ex.getMessage());
            }
        }
    }

    public void refrescarYMostrar() {
        obtenerDatosDesdeBD();
        aplicarFiltrosHistorial();
        ventanaListaEntregas.setVisible(true);
    }
}
