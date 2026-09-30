package cl.duoc.speedfast.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Pantalla raíz e interfaz principal del ecosistema de software SpeedFast.
 * Provee la barra de navegación del menú global de la suite y expone un panel
 * central inmenso con JTextArea dedicado al despliegue en tiempo real de logs hilos.
 */
public class VentanaPrincipal extends JFrame {

    private JLabel tituloLabel;
    private JTextArea logTextArea;

    private JMenuItem itemRegistrarPedido;
    private JMenuItem itemListarPedidos;
    private JMenuItem itemRegistrarRepartidor;
    private JMenuItem itemListarRepartidores;
    private JMenuItem itemRegistrarEntrega;
    private JMenuItem itemListarEntregas;
    private JMenuItem itemIniciarRepartos;

    public VentanaPrincipal() {
        setTitle("SpeedFast App");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);

        inicializarComponentes();
        inicializarMenuBar();
        construirLayout();
    }

    private void inicializarComponentes() {
        tituloLabel = new JLabel("Panel de Control SpeedFast", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 20));
        tituloLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        logTextArea = new JTextArea();
        logTextArea.setEditable(false);
        logTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
    }

    private void construirLayout() {
        add(tituloLabel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(logTextArea);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel panelCentralLog = new JPanel(new BorderLayout());
        panelCentralLog.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        panelCentralLog.add(scrollPane, BorderLayout.CENTER);

        add(panelCentralLog, BorderLayout.CENTER);
    }

    private void inicializarMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuPedidos = new JMenu("Pedidos");
        itemRegistrarPedido = new JMenuItem("Registrar Pedido");
        itemListarPedidos = new JMenuItem("Listar Pedidos");
        menuPedidos.add(itemRegistrarPedido);
        menuPedidos.add(itemListarPedidos);

        JMenu menuRepartidores = new JMenu("Repartidores");
        itemRegistrarRepartidor = new JMenuItem("Registrar Repartidor");
        itemListarRepartidores = new JMenuItem("Listar Repartidores");
        menuRepartidores.add(itemRegistrarRepartidor);
        menuRepartidores.add(itemListarRepartidores);

        JMenu menuEntregas = new JMenu("Entregas");
        itemRegistrarEntrega = new JMenuItem("Asignar Entrega a Repartidor");
        itemListarEntregas = new JMenuItem("Listar Entregas");
        menuEntregas.add(itemRegistrarEntrega);
        menuEntregas.add(itemListarEntregas);

        itemIniciarRepartos = new JMenuItem("Iniciar Repartos");

        menuBar.add(menuPedidos);
        menuBar.add(menuRepartidores);
        menuBar.add(menuEntregas);
        menuBar.add(itemIniciarRepartos);

        setJMenuBar(menuBar);
    }

    /**
     * Añade de forma segura una nueva línea de log al final de la bitácora visual
     * forzando el auto-scroll automático dentro del hilo Event Dispatch Thread (EDT).
     *
     * @param mensaje Cadena de texto plano o traza de simulación a estampar en la interfaz.
     */
    public void appendLog(String mensaje) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            logTextArea.append(mensaje + "\n");
            logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
        });
    }

    /**
     * Vacía por completo el contenido del panel de texto central de logs de forma
     * asíncrona y segura respetando el hilo de refresco de Swing.
     */
    public void clearLog() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            logTextArea.setText("");
        });
    }

    public void addRegistrarPedidoMenuListener(ActionListener listener) {
        itemRegistrarPedido.addActionListener(listener);
    }

    public void addListarPedidosMenuListener(ActionListener listener) {
        itemListarPedidos.addActionListener(listener);
    }

    public void addRegistrarRepartidorMenuListener(ActionListener listener) {
        itemRegistrarRepartidor.addActionListener(listener);
    }

    public void addListarRepartidoresMenuListener(ActionListener listener) {
        itemListarRepartidores.addActionListener(listener);
    }

    public void addRegistrarEntregaMenuListener(ActionListener listener) {
        itemRegistrarEntrega.addActionListener(listener);
    }

    public void addListarEntregasMenuListener(ActionListener listener) {
        itemListarEntregas.addActionListener(listener);
    }

    public void addIniciarRepartosMenuListener(ActionListener listener) {
        itemIniciarRepartos.addActionListener(listener);
    }

    public void setEstadoBotonSimulacion(boolean encendido) {
        SwingUtilities.invokeLater(() -> itemIniciarRepartos.setEnabled(encendido));
    }
}
