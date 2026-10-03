package cl.duoc.speedfast.model.entity;

import cl.duoc.speedfast.controller.ControladorRepartoPedidos;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static cl.duoc.speedfast.model.entity.EstadoPedido.ENTREGADO;

/**
 * Representa a un repartidor en el sistema y define su comportamiento concurrente.
 * Implementa {@link Runnable} para permitir que cada repartidor opere como un hilo independiente.
 */
public class Repartidor implements Runnable {

    private String nombreRepartidor;
    private int idRepartidor;
    private ControladorRepartoPedidos controladorRepartoPedidos;

    /**
     * Constructor para inicializar un repartidor directamente vinculado a la simulación.
     *
     * @param nombreRepartidor Nombre del repartidor.
     * @param controladorRepartoPedidos Controlador que orquesta la cola de reparto.
     * @throws IllegalArgumentException Si el controlador es nulo o el nombre es inválido.
     */
    public Repartidor(String nombreRepartidor, ControladorRepartoPedidos controladorRepartoPedidos) {
        if (controladorRepartoPedidos == null) {
            throw new IllegalArgumentException("El controlador de pedidos no puede ser nulo");
        }
        this.nombreRepartidor = validarNombreRepartidor(nombreRepartidor);
        this.controladorRepartoPedidos = controladorRepartoPedidos;
    }

    /**
     * Constructor para registros nuevos originados desde la interfaz gráfica.
     * Inicializa el ID en 0 delegando el identificador real al AUTO_INCREMENT de MySQL.
     *
     * @param nombreRepartidor Nombre del repartidor.
     * @throws IllegalArgumentException Si el nombre viene nulo o vacío.
     */
    public Repartidor(String nombreRepartidor) {
        this.idRepartidor = 0;
        this.nombreRepartidor = validarNombreRepartidor(nombreRepartidor);
    }

    /**
     * Constructor para reconstruir instancias existentes extraídas desde la base de datos.
     *
     * @param idRepartidor ID correlativo físico en MySQL.
     * @param nombreRepartidor Nombre del repartidor.
     * @throws IllegalArgumentException Si el ID es menor o igual a cero o el nombre es inválido.
     */
    public Repartidor(int idRepartidor, String nombreRepartidor) {
        if (idRepartidor <= 0) {
            throw new IllegalArgumentException("El ID del repartidor debe ser un número positivo");
        }
        this.idRepartidor = idRepartidor;
        this.nombreRepartidor = validarNombreRepartidor(nombreRepartidor);
    }

    private String validarNombreRepartidor(String nombreRepartidor) {
        if (nombreRepartidor == null || nombreRepartidor.isBlank()) {
            throw new IllegalArgumentException("El nombre del repartidor no puede ser nulo o vacío");
        }

        return nombreRepartidor.trim();
    }

    public void setControladorPedidos(ControladorRepartoPedidos controladorRepartoPedidos) {
        if (controladorRepartoPedidos == null) {
            throw new IllegalArgumentException("El controlador de pedidos no puede ser nulo");
        }
        this.controladorRepartoPedidos = controladorRepartoPedidos;
    }

    public int getIdRepartidor() {
        return idRepartidor;
    }

    public void setNombreRepartidor(String nombreRepartidor) {
        this.nombreRepartidor = validarNombreRepartidor(nombreRepartidor);
    }

    public String getNombreRepartidor() {
        return nombreRepartidor;
    }

    /**
     * Ciclo de vida activo del hilo. Retira de forma sincronizada los pedidos
     * asignados a su ID, simula los tiempos de ruta y actualiza los estados en MySQL.
     */
    @Override
    public void run() {
        try {
            while (true) {
                Pedido pedido = controladorRepartoPedidos.retirarPedidoPorRepartidor(this.idRepartidor);

                if (pedido == null) break;

                try {
                    TimeUnit.MILLISECONDS.sleep(calcularTiempoAleatorio(1000, 1000));
                    controladorRepartoPedidos.escribirMensaje("[CARGA] Repartidor [" + nombreRepartidor + "] retirando pedido #" + pedido.getIdPedido());

                    TimeUnit.MILLISECONDS.sleep(calcularTiempoAleatorio(1500, 1500));

                    controladorRepartoPedidos.escribirMensaje("[RUTA] Pedido #" + pedido.getIdPedido() + " se encuentra en reparto");

                    TimeUnit.MILLISECONDS.sleep(calcularTiempoAleatorio(1000, 1000));

                    pedido.setEstadoPedido(ENTREGADO);

                    controladorRepartoPedidos.registrarEntregaEnBD(pedido);
                    controladorRepartoPedidos.escribirMensaje("[ENTREGA] Pedido #" + pedido.getIdPedido() + " ha sido entregado por [" + nombreRepartidor + "]");

                } catch (InterruptedException e) {
                    controladorRepartoPedidos.escribirMensaje("Entrega interrumpida");

                    Thread.currentThread().interrupt();
                    break;
                }
            }

        } catch (RuntimeException e) {
            controladorRepartoPedidos.escribirMensaje("[ERROR] Error en el hilo del repartidor [" + nombreRepartidor + "]: " + e.getMessage());

        } finally {
            controladorRepartoPedidos.finalizarSimulacion();
        }
    }

    private int calcularTiempoAleatorio(int baseMilisegundos, int rangoAleatorio) {
        return baseMilisegundos + ThreadLocalRandom.current().nextInt(rangoAleatorio);
    }

    @Override
    public String toString() {
        return this.nombreRepartidor;
    }
}
