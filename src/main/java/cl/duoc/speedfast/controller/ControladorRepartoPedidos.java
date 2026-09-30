package cl.duoc.speedfast.controller;

import cl.duoc.speedfast.event.LogListener;
import cl.duoc.speedfast.model.dao.EntregaDAO;
import cl.duoc.speedfast.model.dao.PedidoDAO;
import cl.duoc.speedfast.model.dao.RepartidorDAO;
import cl.duoc.speedfast.model.entity.Entrega;
import cl.duoc.speedfast.model.entity.EstadoPedido;
import cl.duoc.speedfast.model.entity.Pedido;
import cl.duoc.speedfast.model.entity.Repartidor;
import cl.duoc.speedfast.view.VentanaPrincipal;

import java.sql.SQLException;
import java.util.List;

/**
 * Motor logístico central encargado de la simulación y orquestación del reparto concurrente.
 * Gestiona el ciclo de vida de los hilos de los trabajadores y sincroniza de forma segura
 * el retiro dirigido de los pedidos protegiendo la consistencia de los datos.
 */
public class ControladorRepartoPedidos {

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final RepartidorDAO repartidorDAO = new RepartidorDAO();
    private final EntregaDAO entregaDAO = new EntregaDAO();
    private final VentanaPrincipal ventanaPrincipal;
    private List<Pedido> listaPedidosEnSimulacion;
    private LogListener logListener;
    private int repartidoresActivos = 0;

    /**
     * Constructor principal que enlaza la ventana raíz para interactuar de forma directa
     * con los componentes visuales de bloqueo y bitácora del panel de control central.
     *
     * @param ventanaPrincipal Instancia activa de la ventana principal del sistema.
     */
    public ControladorRepartoPedidos(VentanaPrincipal ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal;
    }

    public synchronized void registrarEntregaEnBD(Pedido pedido) {
        try {
            pedidoDAO.actualizarEstado(pedido.getIdPedido(), EstadoPedido.ENTREGADO);

        } catch (SQLException ex) {
            escribirMensaje("[ERROR] Error al registrar la entrega en la base de datos: " + ex.getMessage());
        }
    }

    public void setLogListener(LogListener logListener) {
        this.logListener = logListener;
    }

    /**
     * Valida las precondiciones del negocio (existencia de pedidos aptos y repartidores) y
     * dispara en paralelo un hilo independiente para cada trabajador registrado en MySQL.
     *
     * @param listaPedidos Catálogo completo de pedidos históricos extraídos desde la base de datos.
     */
    public void iniciarSimulacionReparto(List<Pedido> listaPedidos) {
        if (listaPedidos == null || listaPedidos.isEmpty()) {
            escribirMensaje("[AVISO] No hay pedidos registrados en el sistema para despachar.");
            habilitarInicioSimulacion();
            return;
        }

        try {
            List<Repartidor> listaRepartidores = repartidorDAO.listarTodos();

            if (listaRepartidores.isEmpty()) {
                escribirMensaje("[AVISO] No hay repartidores registrados en el sistema.");
                habilitarInicioSimulacion();
                return;
            }

            if (listaPedidos.stream().noneMatch(p -> p.getEstadoPedido() == EstadoPedido.EN_REPARTO)) {
                escribirMensaje("[AVISO] No hay pedidos asignados para entregar.");
                habilitarInicioSimulacion();
                return;
            }

            this.listaPedidosEnSimulacion = listaPedidos;

            escribirMensaje("\n --- INICIANDO REPARTO CONCURRENTE DESDE BASE DE DATOS --- ");

            repartidoresActivos = listaRepartidores.size();

            for (Repartidor r : listaRepartidores) {
                r.setControladorPedidos(this);

                new Thread(r).start();
            }

        } catch (SQLException ex) {
            escribirMensaje("[ERROR] Error al listar los repartidores: " + ex.getMessage());
            habilitarInicioSimulacion();
        }
    }

    /**
     * Suministra de forma segura un pedido en tránsito al hilo del repartidor solicitante.
     * Cruza los datos en memoria con la tabla intermedia de MySQL para garantizar que cada
     * trabajador retire únicamente los pedidos asignados a su ID.
     *
     * @param idRepartidor Identificador único del repartidor (hilo activo) que solicita la carga.
     * @return El objeto {@link Pedido} direccionado que le corresponde procesar, o null si no quedan tareas.
     */
    public synchronized Pedido retirarPedidoPorRepartidor(int idRepartidor) {
        try {
            List<Entrega> listaEntregas = entregaDAO.listarTodos();

            for (Pedido p : listaPedidosEnSimulacion) {
                if (p.getEstadoPedido() == EstadoPedido.EN_REPARTO) {
                    for (Entrega e : listaEntregas) {
                        if (e.getIdPedido() == p.getIdPedido() && e.getIdRepartidor() == idRepartidor) {
                            p.setEstadoPedido(EstadoPedido.ENTREGADO);
                            return p;
                        }
                    }
                }
            }

        } catch (SQLException ex) {
            escribirMensaje("[ERROR] Error al retirar el pedido: " + ex.getMessage());
        }

        return null;
    }

    /**
     * Registra el apagado de un hilo de forma coordinada. Al certificar que el último
     * repartidor terminó su ciclo, emite el aviso de cierre y reactiva la interfaz gráfica.
     */
    public synchronized void finalizarSimulacion() {
        this.repartidoresActivos--;
        if (repartidoresActivos == 0) {
            escribirMensaje("\n[AVISO] Todos los pedidos han sido procesados.");
            habilitarInicioSimulacion();
        }
    }

    private void habilitarInicioSimulacion() {
        if (ventanaPrincipal != null) {
            ventanaPrincipal.setEstadoBotonSimulacion(true);
        }
    }

    public synchronized void escribirMensaje(String mensaje) {
        if (logListener != null) {
            logListener.registrarMensaje(mensaje);
        }
    }
}
