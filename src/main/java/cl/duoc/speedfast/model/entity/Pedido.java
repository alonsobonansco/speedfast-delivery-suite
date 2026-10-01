package cl.duoc.speedfast.model.entity;

/**
 * Representa un pedido dentro del sistema SpeedFast, encapsulando sus datos de origen,
 * destino y estado de tránsito mediante un modelo con auto-validación.
 */
public class Pedido {

    private TipoPedido tipoPedido;
    private final int idPedido;
    private String direccionEntrega;
    private EstadoPedido estadoPedido;

    /**
     * Constructor para reconstruir instancias de pedidos existentes recuperados desde MySQL.
     *
     * @param idPedido ID único correlativo físico en la base de datos.
     * @param direccionEntrega Dirección de destino sanitizada.
     * @param tipoPedido Clasificación del servicio (COMIDA, ENCOMIENDA, EXPRESS).
     * @throws IllegalArgumentException Si el ID es menor o igual a cero, o si los datos son nulos o vacíos.
     */
    public Pedido(int idPedido, String direccionEntrega, TipoPedido tipoPedido, EstadoPedido estadoPedido) {
        if (idPedido <= 0) {
            throw new IllegalArgumentException("El ID del pedido debe ser válido.");
        }

        this.tipoPedido = validarTipoPedido(tipoPedido);
        this.idPedido = idPedido;
        setDireccionEntrega(direccionEntrega);
        setEstadoPedido(estadoPedido);
    }

    /**
     * Constructor para nuevos registros levantados desde el formulario de la interfaz gráfica.
     * Inicializa el identificador en 0 a la espera del AUTO_INCREMENT del motor relacional.
     *
     * @param tipoPedido Clasificación del servicio (COMIDA, ENCOMIENDA, EXPRESS).
     * @param direccionEntrega Dirección de destino proporcionada por el usuario.
     * @throws IllegalArgumentException Si el tipo de pedido es nulo o la dirección es inválida.
     */
    public Pedido(String direccionEntrega, TipoPedido tipoPedido, EstadoPedido estadoPedido) {
        this.tipoPedido = validarTipoPedido(tipoPedido);
        this.idPedido = 0;
        setDireccionEntrega(direccionEntrega);
        setEstadoPedido(estadoPedido);
    }

    private TipoPedido validarTipoPedido(TipoPedido tipoPedido) {
        if (tipoPedido == null) {
            throw new IllegalArgumentException("El tipo de pedido no puede ser nulo.");
        }

        return tipoPedido;
    }

    public TipoPedido getTipoPedido() {
        return tipoPedido;
    }

    public void setTipoPedido(TipoPedido tipoPedido) {
        this.tipoPedido = validarTipoPedido(tipoPedido);
    }

    public int getIdPedido() {
        return idPedido;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        if (direccionEntrega == null || direccionEntrega.isBlank()) {
            throw new IllegalArgumentException("La dirección de entrega debe ser válida.");
        }

        this.direccionEntrega = direccionEntrega.trim();
    }

    public EstadoPedido getEstadoPedido() {
        return estadoPedido;
    }

    public void setEstadoPedido(EstadoPedido estadoPedido) {
        if (estadoPedido == null) {
            throw new IllegalArgumentException("El estado del pedido no puede ser nulo.");
        }

        this.estadoPedido = estadoPedido;
    }

    @Override
    public String toString() {
        return "Pedido #" + idPedido + " - " + direccionEntrega;
    }
}
