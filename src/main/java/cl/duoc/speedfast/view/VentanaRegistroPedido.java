package cl.duoc.speedfast.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Interfaz gráfica encargada de proveer el formulario de ingreso para nuevos pedidos.
 * Incorpora campos de texto, un menú desplegable JComboBox y un diseño simétrico basado en GridBagLayout.
 */
public class VentanaRegistroPedido extends JFrame {

    private JLabel tituloLabel;
    private JTextField direccionTextField;
    private JComboBox<String> tipoComboBox;
    private JComboBox<String> estadoComboBox;

    private JButton guardarButton;
    private JButton atrasButton;

    public VentanaRegistroPedido() {
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
        tituloLabel = new JLabel("Formulario de Registro de Pedidos", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 16));
        tituloLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));

        direccionTextField = new JTextField(20);
        tipoComboBox = new JComboBox<>(new String[]{"COMIDA", "ENCOMIENDA", "EXPRESS"});
        estadoComboBox = new JComboBox<>(new String[]{"PENDIENTE", "EN_REPARTO", "ENTREGADO"});

        guardarButton = new JButton("Guardar");
        atrasButton = new JButton("Atrás");
    }

    private void construirLayout() {
        add(tituloLabel, BorderLayout.NORTH);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font fuenteCampos = new Font("Arial", Font.PLAIN, 14);

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel direccionLabel = new JLabel("Dirección de Entrega:");
        direccionLabel.setFont(fuenteCampos);
        panelFormulario.add(direccionLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        direccionTextField.setFont(fuenteCampos);
        panelFormulario.add(direccionTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel tipoLabel = new JLabel("Tipo de Pedido:");
        tipoLabel.setFont(fuenteCampos);
        panelFormulario.add(tipoLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        tipoComboBox.setFont(fuenteCampos);
        panelFormulario.add(tipoComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel estadoLabel = new JLabel("Estado del Pedido:");
        estadoLabel.setFont(fuenteCampos);
        panelFormulario.add(estadoLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        estadoComboBox.setFont(fuenteCampos);
        panelFormulario.add(estadoComboBox, gbc);

        add(panelFormulario, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelBotones.add(guardarButton);
        panelBotones.add(atrasButton);
        add(panelBotones, BorderLayout.SOUTH);

        atrasButton.addActionListener(e -> cerrarVentana());
    }

    public void prellenarFormulario(String direccion, String tipo, String estado) {
        direccionTextField.setText(direccion);
        tipoComboBox.setSelectedItem(tipo);
        estadoComboBox.setSelectedItem(estado);
    }

    /**
     * Recupera y sanitiza la dirección de destino ingresada en el campo de texto,
     * removiendo los espacios en blanco innecesarios en los extremos.
     *
     * @return La dirección de entrega como una cadena de texto plano.
     */
    public String getDireccionEntrega() {
        return direccionTextField.getText().trim();
    }

    /**
     * Obtiene el tipo de servicio seleccionado en el menú desplegable de la interfaz.
     *
     * @return El nombre de la opción seleccionada como una cadena de texto plano.
     */
    public String getTipoPedido() {
        return (String) tipoComboBox.getSelectedItem();
    }

    public String getEstadoPedido() {
        return (String) estadoComboBox.getSelectedItem();
    }

    public void cerrarVentana() {
        this.dispose();
    }

    public void addGuardarListener(ActionListener listener) {
        guardarButton.addActionListener(listener);
    }

    // ya no lo uso? tampoco los de ↓?
    public void addVolverAtrasListener(ActionListener listener) {
        atrasButton.addActionListener(listener);
    }

    public void mostrarMensajeConfirmacion(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Pedido registrado", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarMensajeError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void limpiarFormulario() {
        direccionTextField.setText("");
    }
}
