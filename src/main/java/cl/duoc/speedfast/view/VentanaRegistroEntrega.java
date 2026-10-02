package cl.duoc.speedfast.view;

import cl.duoc.speedfast.model.entity.Pedido;
import cl.duoc.speedfast.model.entity.Repartidor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Interfaz gráfica encargada de proveer el formulario para la asignación manual de rutas.
 * Incorpora componentes de selección optimizados mediante peso horizontal para desplegar de forma balanceada los datos relacionales.
 */
public class VentanaRegistroEntrega extends JFrame {

    private JLabel tituloLabel;
    private JComboBox<Pedido> pedidoJComboBox;
    private JComboBox<Repartidor> repartidorJComboBox;
    private JButton guardarButton;
    private JButton atrasButton;

    public VentanaRegistroEntrega() {
        setTitle("SpeedFast App");
        setSize(750, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        inicializarComponentes();
        construirLayout();
    }

    private void inicializarComponentes() {
        tituloLabel = new JLabel("Asignación de Entregas", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 18));
        tituloLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));

        pedidoJComboBox = new JComboBox<>();
        repartidorJComboBox = new JComboBox<>();

        guardarButton = new JButton("Asignar Entrega");
        atrasButton = new JButton("Atrás");
    }

    private void construirLayout() {
        add(tituloLabel, BorderLayout.NORTH);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font fuenteCampos = new Font("Arial", Font.PLAIN, 14);

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel pedidoLabel = new JLabel("Seleccionar Pedido:");
        pedidoLabel.setFont(fuenteCampos);
        panelFormulario.add(pedidoLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        pedidoJComboBox.setFont(fuenteCampos);
        panelFormulario.add(pedidoJComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel repartidorLabel = new JLabel("Seleccionar Repartidor:");
        repartidorLabel.setFont(fuenteCampos);
        panelFormulario.add(repartidorLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        repartidorJComboBox.setFont(fuenteCampos);
        panelFormulario.add(repartidorJComboBox, gbc);

        add(panelFormulario, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelBotones.add(guardarButton);
        panelBotones.add(atrasButton);
        add(panelBotones, BorderLayout.SOUTH);
    }

    public JComboBox<Pedido> getComboPedidos() {
        return pedidoJComboBox;
    }

    public JComboBox<Repartidor> getComboRepartidores() {
        return repartidorJComboBox;
    }

    public void addGuardarListener(ActionListener l) {
        guardarButton.addActionListener(l);
    }

    public void addVolverAtrasListener(ActionListener l) {
        atrasButton.addActionListener(l);
    }

    public void cerrarVentana() {
        this.dispose();
    }

    public void mostrarMensajeConfirmacion(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Entrega registrada", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarMensajeError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Limpia el menú desplegable de selección y añade de forma secuencial
     * los objetos Pedido vigentes que han sido recuperados desde la base de datos.
     *
     * @param pedidos Lista de entidades en estado PENDIENTE aptas para asignación.
     */
    public void cargarPedidos(List<Pedido> pedidos) {
        pedidoJComboBox.removeAllItems();
        for (Pedido p : pedidos) pedidoJComboBox.addItem(p);
    }

    /**
     * Limpia el menú desplegable de selección y añade de forma secuencial
     * los objetos Repartidor vigentes que han sido recuperados desde la base de datos.
     *
     * @param repartidores Lista completa de trabajadores disponibles en el sistema.
     */
    public void cargarRepartidores(List<Repartidor> repartidores) {
        repartidorJComboBox.removeAllItems();
        for (Repartidor r : repartidores) repartidorJComboBox.addItem(r);
    }

    public void limpiarFormulario() {
        pedidoJComboBox.setSelectedIndex(0);
        repartidorJComboBox.setSelectedIndex(0);
    }
}
