package cl.duoc.speedfast.controller;

import cl.duoc.speedfast.model.dao.PedidoDAO;
import cl.duoc.speedfast.model.entity.Pedido;
import cl.duoc.speedfast.view.*;

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

    private VentanaRegistroPedido ventanaRegistroPedido = null;
    private VentanaListaPedidos ventanaListaPedidos = null;
    private VentanaListaRepartidores ventanaListaRepartidores = null;
    private VentanaRegistroRepartidor ventanaRegistroRepartidor = null;
    private VentanaRegistroEntrega ventanaRegistroEntrega = null;
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
        ventanaPrincipal.addRegistrarPedidoMenuListener(e -> ejecutarRegistroPedido());
        ventanaPrincipal.addListarPedidosMenuListener(e -> ejecutarListarPedidos());
        ventanaPrincipal.addRegistrarRepartidorMenuListener(e -> ejecutarRegistroRepartidor());
        ventanaPrincipal.addListarRepartidoresMenuListener(e -> ejecutarListarRepartidores());
        ventanaPrincipal.addRegistrarEntregaMenuListener(e -> ejecutarRegistroEntrega());
        ventanaPrincipal.addListarEntregasMenuListener(e -> ejecutarListarEntregas());
        ventanaPrincipal.addIniciarRepartosMenuListener(e -> ejecutarIniciarEntregas());
    }

    private void ejecutarRegistroEntrega() {
        if (ventanaRegistroEntrega == null || !ventanaRegistroEntrega.isDisplayable()) {
            ventanaRegistroEntrega = new VentanaRegistroEntrega();

            new ControladorRegistroEntrega(ventanaRegistroEntrega);

            ventanaRegistroEntrega.setVisible(true);
            ventanaRegistroEntrega.toFront();
            ventanaRegistroEntrega.requestFocus();
        }
    }

    private void ejecutarRegistroPedido() {
        ventanaPrincipal.clearLog();
        if (ventanaRegistroPedido == null || !ventanaRegistroPedido.isDisplayable()) {
            ventanaRegistroPedido = new VentanaRegistroPedido();

            new ControladorRegistroPedido(ventanaRegistroPedido);

            ventanaRegistroPedido.setVisible(true);
            ventanaRegistroPedido.toFront();
            ventanaRegistroPedido.requestFocus();
        }
    }

    private void ejecutarListarPedidos() {
        if (ventanaListaPedidos == null || !ventanaListaPedidos.isDisplayable()) {
            ventanaListaPedidos = new VentanaListaPedidos();
        }

        new ControladorListaPedidos(ventanaListaPedidos);

        ventanaListaPedidos.setVisible(true);
        ventanaListaPedidos.toFront();
        ventanaListaPedidos.requestFocus();
    }

    private void ejecutarRegistroRepartidor() {
        if (ventanaRegistroRepartidor == null || !ventanaRegistroRepartidor.isDisplayable()) {
            ventanaRegistroRepartidor = new VentanaRegistroRepartidor();
        }

        new ControladorRegistroRepartidor(ventanaRegistroRepartidor);

        ventanaRegistroRepartidor.setVisible(true);
        ventanaRegistroRepartidor.toFront();
        ventanaRegistroRepartidor.requestFocus();
    }

    private void ejecutarListarRepartidores() {
        if (ventanaListaRepartidores == null || !ventanaListaRepartidores.isDisplayable()) {
            ventanaListaRepartidores = new VentanaListaRepartidores();
        }

        new ControladorListaRepartidores(ventanaListaRepartidores);

        ventanaListaRepartidores.setVisible(true);
        ventanaListaRepartidores.toFront();
        ventanaListaRepartidores.requestFocus();
    }

    private void ejecutarListarEntregas() {
        if (ventanaListaEntregas == null || !ventanaListaEntregas.isDisplayable()) {
            ventanaListaEntregas = new VentanaListaEntregas();
        }

        new ControladorListaEntregas(ventanaListaEntregas);

        ventanaListaEntregas.setVisible(true);
        ventanaListaEntregas.toFront();
        ventanaListaEntregas.requestFocus();
    }

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
