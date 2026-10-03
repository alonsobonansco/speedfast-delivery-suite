package cl.duoc.speedfast.controller;

import cl.duoc.speedfast.model.dao.PedidoDAO;
import cl.duoc.speedfast.model.entity.Pedido;
import cl.duoc.speedfast.view.VentanaListaEntregas;
import cl.duoc.speedfast.view.VentanaListaPedidos;
import cl.duoc.speedfast.view.VentanaListaRepartidores;
import cl.duoc.speedfast.view.VentanaPrincipal;

import java.sql.SQLException;
import java.util.List;

/**
 * Orquestador maestro de la capa de control de la suite SpeedFast.
 * Aplica el patrón Controlador Centralizado (Front Controller) para coordinar
 * la navegación de las subtareas visuales y activar el módulo concurrente.
 */
public class ControladorPrincipal {

    private final VentanaPrincipal ventanaPrincipal;
    private final PedidoDAO pedidoDAO = new PedidoDAO();

    private VentanaListaPedidos ventanaListaPedidos = null;
    private VentanaListaRepartidores ventanaListaRepartidores = null;
    private VentanaListaEntregas ventanaListaEntregas = null;

    private ControladorRepartoPedidos controladorRepartoPedidos = null;

    /**
     * Constructor principal encargado de enlazar la ventana raíz y disparar
     * de forma reactiva la suscripción global de eventos del menú de navegación.
     *
     * @param ventanaPrincipal Instancia activa del panel de control central (Vista).
     */
    public ControladorPrincipal(VentanaPrincipal ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal;

        inicializarListeners();
    }

    private void inicializarListeners() {
        ventanaPrincipal.addPedidosMenuListener(e -> ejecutarModuloPedidos());
        ventanaPrincipal.addRepartidoresMenuListener(e -> ejecutarModuloRepartidores());
        ventanaPrincipal.addEntregasMenuListener(e -> ejecutarModuloEntregas());
        ventanaPrincipal.addIniciarRepartosMenuListener(e -> ejecutarIniciarEntregas());
    }

    /**
     * Inicializa de manera perezosa el subsistema de gestión histórica de pedidos,
     * instanciando su controlador atómico y trayendo la interfaz gráfica al frente de la pantalla.
     */
    private void ejecutarModuloPedidos() {
        ventanaPrincipal.clearLog();
        if (ventanaListaPedidos == null || !ventanaListaPedidos.isDisplayable()) {
            ventanaListaPedidos = new VentanaListaPedidos();

            new ControladorListaPedidos(ventanaListaPedidos);

            ventanaListaPedidos.setVisible(true);
            ventanaListaPedidos.toFront();
            ventanaListaPedidos.requestFocus();
        }
    }

    /**
     * Despliega de forma independiente el catálogo de mantenimiento de repartidores,
     * aislando su ciclo de vida y delegando las tareas de edición del CRUD en su controlador experto.
     */
    private void ejecutarModuloRepartidores() {
        ventanaPrincipal.clearLog();
        if (ventanaListaRepartidores == null || !ventanaListaRepartidores.isDisplayable()) {
            ventanaListaRepartidores = new VentanaListaRepartidores();

            new ControladorListaRepartidores(ventanaListaRepartidores);

            ventanaListaRepartidores.setVisible(true);
            ventanaListaRepartidores.toFront();
            ventanaListaRepartidores.requestFocus();
        }
    }

    /**
     * Carga la bitácora relacional de asignaciones manuales en pantalla,
     * inyectando la vista en su respectiva aduana de control para activar los filtros.
     */
    private void ejecutarModuloEntregas() {
        ventanaPrincipal.clearLog();
        if (ventanaListaEntregas == null || !ventanaListaEntregas.isDisplayable()) {
            ventanaListaEntregas = new VentanaListaEntregas();

            new ControladorListaEntregas(ventanaListaEntregas);

            ventanaListaEntregas.setVisible(true);
            ventanaListaEntregas.toFront();
            ventanaListaEntregas.requestFocus();
        }
    }

    /**
     * Instancia el controlador asíncrono secundario, enlaza la bitácora visual
     * en tiempo real mediante una referencia de método y arranca la simulación concurrente multihilo.
     */
    private void ejecutarIniciarEntregas() {
        ventanaPrincipal.setEstadoBotonSimulacion(false);
        ventanaPrincipal.clearLog();

        controladorRepartoPedidos = new ControladorRepartoPedidos(ventanaPrincipal);
        controladorRepartoPedidos.setLogListener(ventanaPrincipal::appendLog);

        try {
            List<Pedido> pedidosBD = pedidoDAO.readAll();
            controladorRepartoPedidos.iniciarSimulacionReparto(pedidosBD);

        } catch (SQLException ex) {
            ventanaPrincipal.appendLog("No se pudo iniciar la simulación. Error de lectura en MySQL.");
            ventanaPrincipal.setEstadoBotonSimulacion(true);
        }
    }
}
