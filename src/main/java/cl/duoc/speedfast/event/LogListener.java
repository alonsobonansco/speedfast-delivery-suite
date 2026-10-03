package cl.duoc.speedfast.event;

/**
 * Interfaz funcional que actúa como canal de comunicación asíncrono.
 * Implementa el patrón Observer para transportar las trazas de texto
 * desde el motor de hilos hacia la bitácora gráfica en tiempo real.
 */
@FunctionalInterface
public interface LogListener {

    /**
     * Canaliza y despacha un registro lógico de la simulación concurrente.
     *
     * @param mensaje Cadena de caracteres con el texto informativo a desplegar.
     */
    void registrarMensaje(String mensaje);
}
