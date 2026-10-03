package cl.duoc.speedfast.model.entity;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Representa la vinculación e historial de una asignación entre un pedido y un repartidor,
 * registrando de forma inmutable la marca temporal de su salida a ruta.
 */
public class Entrega {

    private final int idPedido;
    private final int idRepartidor;
    private final LocalDate fecha;
    private final LocalTime hora;
    private int idEntrega;

    /**
     * Constructor transaccional para instanciar y validar un registro de asignación de entrega.
     *
     * @param idPedido     ID correlativo del pedido asociado en MySQL.
     * @param idRepartidor ID correlativo del repartidor asignado en MySQL.
     * @param fecha        Fecha de registro del despacho físico.
     * @param hora         Hora exacta de salida del transporte.
     * @throws IllegalArgumentException Si los IDs son menores o iguales a cero, o si los objetos temporales son nulos.
     */
    public Entrega(int idPedido, int idRepartidor, LocalDate fecha, LocalTime hora) {
        if (idPedido <= 0) {
            throw new IllegalArgumentException("El ID del pedido debe ser válido.");
        }
        if (idRepartidor <= 0) {
            throw new IllegalArgumentException("El ID del repartidor debe ser válido.");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula.");
        }
        if (hora == null) {
            throw new IllegalArgumentException("La hora no puede ser nula.");
        }

        this.idPedido = idPedido;
        this.idRepartidor = idRepartidor;
        this.fecha = fecha;
        this.hora = hora;
    }

    public int getIdEntrega() {
        return idEntrega;
    }

    public void setIdEntrega(int idEntrega) {
        if (idEntrega <= 0) {
            throw new IllegalArgumentException("El ID de la entrega debe ser válido.");
        }
        this.idEntrega = idEntrega;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public int getIdRepartidor() {
        return idRepartidor;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHora() {
        return hora;
    }
}
