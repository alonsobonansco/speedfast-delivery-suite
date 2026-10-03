package cl.duoc.speedfast.controller;

import cl.duoc.speedfast.model.dao.PedidoDAO;
import cl.duoc.speedfast.model.entity.EstadoPedido;
import cl.duoc.speedfast.model.entity.Pedido;
import cl.duoc.speedfast.model.entity.TipoPedido;
import cl.duoc.speedfast.view.VentanaRegistroPedido;

import java.sql.SQLException;

/**
 * Controlador especializado encargado de gestionar el flujo del formulario de ingreso de pedidos.
 * Captura los datos de la interfaz visual, los transforma en la entidad correspondiente y los persiste en MySQL.
 */
public class ControladorRegistroPedido {

    private final VentanaRegistroPedido ventanaRegistroPedido;
    private final PedidoDAO pedidoDAO = new PedidoDAO();

    private final Pedido pedidoAEditar;
    private final ControladorListaPedidos controladorListaPedidos;

    /**
     * Constructor principal que enlaza el formulario de ingreso de datos
     * y activa de forma reactiva la suscripción de los botones Guardar y Atrás.
     *
     * @param ventanaRegistroPedido   Instancia activa del formulario visual de nuevo pedido.
     * @param pedidoAEditar           Entidad cargada con los datos del pedido a modificar (null si se trata de un registro nuevo).
     * @param controladorListaPedidos Referencia al controlador maestro de la lista para coordinar la sincronización al cerrar.
     */
    public ControladorRegistroPedido(VentanaRegistroPedido ventanaRegistroPedido,
                                     Pedido pedidoAEditar,
                                     ControladorListaPedidos controladorListaPedidos) {
        this.ventanaRegistroPedido = ventanaRegistroPedido;
        this.pedidoAEditar = pedidoAEditar;
        this.controladorListaPedidos = controladorListaPedidos;

        inicializarListeners();

        if (this.pedidoAEditar != null) {
            this.ventanaRegistroPedido.prellenarFormulario(
                    pedidoAEditar.getDireccionEntrega(),
                    pedidoAEditar.getTipoPedido().name(),
                    pedidoAEditar.getEstadoPedido().name()
            );
        }
    }

    private void inicializarListeners() {
        ventanaRegistroPedido.addVolverAtrasListener(e -> ventanaRegistroPedido.cerrarVentana());
        ventanaRegistroPedido.addGuardarListener(e -> procesarGuardado());

        ventanaRegistroPedido.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (controladorListaPedidos != null) {
                    controladorListaPedidos.refrescarYMostrar();
                }
            }
        });
    }

    private void procesarGuardado() {
        try {
            String direccionEntrega = ventanaRegistroPedido.getDireccionEntrega();
            String tipoPedidoStr = ventanaRegistroPedido.getTipoPedido();
            String estadoPedidoStr = ventanaRegistroPedido.getEstadoPedido();

            if (direccionEntrega.isBlank()) {
                ventanaRegistroPedido.mostrarMensajeError("La dirección de entrega no puede estar vacía.");
                return;
            }

            TipoPedido tipoPedidoEnum = TipoPedido.valueOf(tipoPedidoStr.toUpperCase());
            EstadoPedido estadoPedidoEnum = EstadoPedido.valueOf(estadoPedidoStr.toUpperCase());

            if (pedidoAEditar == null) {
                Pedido nuevoPedido = new Pedido(direccionEntrega, tipoPedidoEnum, estadoPedidoEnum);
                pedidoDAO.create(nuevoPedido);

                ventanaRegistroPedido.mostrarMensajeConfirmacion("Pedido registrado exitosamente.");

                ventanaRegistroPedido.limpiarFormulario();
            } else {
                pedidoAEditar.setDireccionEntrega(direccionEntrega);
                pedidoAEditar.setTipoPedido(tipoPedidoEnum);
                pedidoAEditar.setEstadoPedido(estadoPedidoEnum);
                pedidoDAO.update(pedidoAEditar);

                ventanaRegistroPedido.mostrarMensajeConfirmacion("Pedido actualizado exitosamente.");
                ventanaRegistroPedido.cerrarVentana();
            }

        } catch (IllegalArgumentException e) {
            ventanaRegistroPedido.mostrarMensajeError(e.getMessage());
        } catch (SQLException ex) {
            ventanaRegistroPedido.mostrarMensajeError("Error al guardar el pedido en la base de datos: " + ex.getMessage());
        }
    }
}
