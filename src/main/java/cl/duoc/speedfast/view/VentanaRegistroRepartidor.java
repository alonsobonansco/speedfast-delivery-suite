package cl.duoc.speedfast.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Interfaz gráfica encargada de proveer el formulario de ingreso para nuevos repartidores.
 * Incorpora componentes de texto y un diseño basado en GridBagLayout para garantizar la simetría visual.
 */
public class VentanaRegistroRepartidor extends JFrame {

    private JLabel tituloLabel;
    private JTextField nombreTextField;
    private JButton guardarButton;
    private JButton atrasButton;

    public VentanaRegistroRepartidor() {
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
        tituloLabel = new JLabel("Formulario de Registro de Repartidores", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 18));
        tituloLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));

        nombreTextField = new JTextField(20);

        guardarButton = new JButton("Guardar");
        atrasButton = new JButton("Atrás");
    }

    private void construirLayout() {
        add(tituloLabel, BorderLayout.NORTH);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridy = 0;

        gbc.gridx = 0;
        JLabel nombreLabel = new JLabel("Nombre del Repartidor:");
        nombreLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panelFormulario.add(nombreLabel, gbc);

        gbc.gridx = 1;
        nombreTextField.setFont(new Font("Arial", Font.PLAIN, 14));
        panelFormulario.add(nombreTextField, gbc);

        add(panelFormulario, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelBotones.add(guardarButton);
        panelBotones.add(atrasButton);

        add(panelBotones, BorderLayout.SOUTH);
    }

    /**
     * Recupera y sanitiza el texto ingresado en el campo del nombre del repartidor,
     * removiendo los espacios en blanco innecesarios en los extremos.
     *
     * @return El nombre del repartidor ingresado como una cadena de texto plano.
     */
    public String getNombreRepartidor() {
        return nombreTextField.getText().trim();
    }

    public void cerrarVentana() {
        dispose();
    }

    public void limpiarFormulario() {
        nombreTextField.setText("");
    }

    public void addGuardarListener(ActionListener listener) {
        guardarButton.addActionListener(listener);
    }

    public void addVolverAtrasListener(ActionListener listener) {
        atrasButton.addActionListener(listener);
    }

    public void mostrarMensajeConfirmacion(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Repartidor registrado", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarMensajeError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}