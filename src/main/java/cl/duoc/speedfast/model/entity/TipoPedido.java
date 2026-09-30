package cl.duoc.speedfast.model.entity;

/**
 * Enumeración que define las categorías de negocio válidas para la clasificación de un pedido.
 * El uso de este Enum garantiza la seguridad de tipos (Type-Safety) y permite aislar la lógica
 * polimórfica específica de cada paquete en el flujo logístico del sistema.
 */
public enum TipoPedido {
    COMIDA,
    EXPRESS,
    ENCOMIENDA
}
