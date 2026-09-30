package cl.duoc.speedfast.model.entity;

/**
 * Enumeración que define los estados válidos para el ciclo de vida de un pedido.
 * El uso de este Enum garantiza la seguridad de tipos (Type-Safety) y evita
 * errores de consistencia semántica o de tipeo en el flujo logístico del sistema.
 */
public enum EstadoPedido {
    PENDIENTE,
    EN_REPARTO,
    ENTREGADO
}
