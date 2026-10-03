package cl.duoc.speedfast.view;

import cl.duoc.speedfast.model.entity.Entrega;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Interfaz gráfica encargada de desplegar el registro histórico de asignaciones de entregas.
 * Incorpora un componente JTable no editable para la visualización segura de los registros de MySQL.
 */
public class VentanaListaEntregas extends JFrame {

    private JLabel tituloLabel;
    private JTable entregasTable;
    private DefaultTableModel tablaModel;

    private JTextField filtroPedidoJTextField;
    private JComboBox<Object> filtroRepartidorComboBox;

    private JButton agregarButton;
    private JButton eliminarButton;
    private JButton atrasButton;

    public VentanaListaEntregas() {
        setTitle("SpeedFast App");
        setSize(750, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 15));
        setResizable(false);

        inicializarComponentes();
        construirLayout();
    }

    private void inicializarComponentes() {
        tituloLabel = new JLabel("Administración de Entregas", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 18));
        tituloLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));

        filtroPedidoJTextField = new JTextField(8);
        filtroRepartidorComboBox = new JComboBox<>();

        String[] columnas = {"ID Entrega", "ID Pedido", "ID Repartidor", "Fecha", "Hora"};
        tablaModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        entregasTable = new JTable(tablaModel);
        entregasTable.getTableHeader().setReorderingAllowed(false);

        agregarButton = new JButton("Asignar Entrega");
        eliminarButton = new JButton("Eliminar Entrega");
        atrasButton = new JButton("Atrás");
    }

    private void construirLayout() {
        JPanel panelSuperior = new JPanel(new BorderLayout(5, 5));
        panelSuperior.add(tituloLabel, BorderLayout.NORTH);
        tituloLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));

        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros de Búsqueda"));

        panelFiltros.add(new JLabel("Buscar por ID Pedido:"));
        panelFiltros.add(filtroPedidoJTextField);
        panelFiltros.add(new JLabel("Filtrar por Repartidor:"));
        panelFiltros.add(filtroRepartidorComboBox);

        panelSuperior.add(panelFiltros, BorderLayout.CENTER);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(5, 20, 10, 20));
        add(panelSuperior, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(entregasTable);
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        panelTabla.add(scrollPane, BorderLayout.CENTER);
        add(panelTabla, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelBotones.add(agregarButton);
        panelBotones.add(eliminarButton);
        panelBotones.add(atrasButton);
        add(panelBotones, BorderLayout.SOUTH);
    }

    public void addAgregarListener(ActionListener listener) {
        agregarButton.addActionListener(listener);
    }

    public void addEliminarListener(ActionListener listener) {
        eliminarButton.addActionListener(listener);
    }

    public void addVolverAtrasListener(ActionListener listener) {
        atrasButton.addActionListener(listener);
    }

    public void addFiltroPedidoListener(DocumentListener listener) {
        filtroPedidoJTextField.getDocument().addDocumentListener(listener);
    }

    public void addFiltroRepartidorListener(ActionListener listener) {
        filtroRepartidorComboBox.addActionListener(listener);
    }

    public void cerrarVentana() {
        this.dispose();
    }

    public void actualizarTabla(List<Entrega> listaEntregas) {
        tablaModel.setRowCount(0);

        for (Entrega e : listaEntregas) {
            Object[] fila = {
                    e.getIdEntrega(),
                    e.getIdPedido(),
                    e.getIdRepartidor(),
                    e.getFecha(),
                    e.getHora()
            };
            tablaModel.addRow(fila);
        }
    }

    public void llenarRepartidoresComboBox(List<Object> listaRepartidores) {
        filtroRepartidorComboBox.removeAllItems();
        filtroRepartidorComboBox.addItem("--- Todos los Repartidores ---");
        for (Object r : listaRepartidores) filtroRepartidorComboBox.addItem(r);
    }

    public String getIdPedidoFiltro() {
        return filtroPedidoJTextField.getText().trim();
    }

    public JComboBox<Object> getRepartidorFiltro() {
        return filtroRepartidorComboBox;
    }

    public int getIdEntregaSeleccionada() {
        int filaSeleccionada = entregasTable.getSelectedRow();
        if (filaSeleccionada == -1) return -1;
        return (int) tablaModel.getValueAt(filaSeleccionada, 0);
    }

    public void mostrarMensajeConfirmacion(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Proceso exitoso", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarMensajeError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean confirmarEliminacion() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar esta entrega?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        return opcion == JOptionPane.YES_OPTION;
    }
}
