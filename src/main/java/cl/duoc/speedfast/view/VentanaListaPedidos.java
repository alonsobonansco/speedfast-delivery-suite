package cl.duoc.speedfast.view;

import cl.duoc.speedfast.model.entity.Pedido;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Interfaz gráfica encargada de desplegar el listado histórico de los pedidos.
 * Incorpora un componente JTable no editable para la visualización segura de los registros de MySQL.
 */
public class VentanaListaPedidos extends JFrame {

    private JLabel tituloLabel;
    private JTable pedidosTable;
    private DefaultTableModel tablaModel;

    private JComboBox<String> filtroTipoComboBox;
    private JComboBox<String> filtroEstadoComboBox;

    private JButton agregarButton;
    private JButton editarButton;
    private JButton eliminarButton;
    private JButton atrasButton;

    public VentanaListaPedidos() {
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
        tituloLabel = new JLabel("Administración de Pedidos", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 18));
        tituloLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));

        filtroTipoComboBox = new JComboBox<>(new String[]{"Todos", "COMIDA", "ENCOMIENDA", "EXPRESS"});
        filtroEstadoComboBox = new JComboBox<>(new String[]{"Todos", "PENDIENTE", "EN_REPARTO", "ENTREGADO"});

        String[] columnas = {"ID Pedido", "Dirección Entrega", "Tipo Pedido", "Estado Pedido"};
        tablaModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        pedidosTable = new JTable(tablaModel);
        pedidosTable.getTableHeader().setReorderingAllowed(false);
        pedidosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        agregarButton = new JButton("Agregar Nuevo");
        editarButton = new JButton("Editar Seleccionado");
        eliminarButton = new JButton("Eliminar Seleccionado");
        atrasButton = new JButton("Atrás");
    }

    public void construirLayout() {
        JPanel panelSuperior = new JPanel(new BorderLayout(5, 5));
        panelSuperior.add(tituloLabel, BorderLayout.NORTH);
        tituloLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));

        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros de Búsqueda"));

        panelFiltros.add(new JLabel("Filtrar por Tipo:"));
        panelFiltros.add(filtroTipoComboBox);
        panelFiltros.add(new JLabel("Filtrar por Estado:"));
        panelFiltros.add(filtroEstadoComboBox);

        panelSuperior.add(panelFiltros, BorderLayout.SOUTH);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        add(panelSuperior, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(pedidosTable);
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        panelTabla.add(scrollPane, BorderLayout.CENTER);
        add(panelTabla, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panelBotones.add(agregarButton);
        panelBotones.add(editarButton);
        panelBotones.add(eliminarButton);
        panelBotones.add(atrasButton);
        add(panelBotones, BorderLayout.SOUTH);
    }

    public void cerrarVentana() {
        this.dispose();
    }

    public void actualizarTabla(List<Pedido> listaPedidos) {
        tablaModel.setRowCount(0);

        for (Pedido p : listaPedidos) {
            tablaModel.addRow(new Object[]{
                    p.getIdPedido(),
                    p.getDireccionEntrega(),
                    p.getTipoPedido().name(),
                    p.getEstadoPedido().name()
            });
        }
    }

    public String getFiltroTipoSeleccionado() {
        return (String) filtroTipoComboBox.getSelectedItem();
    }

    public String getFiltroEstadoSeleccionado() {
        return (String) filtroEstadoComboBox.getSelectedItem();
    }

    public void addFiltroTipoListener(ActionListener listener) {
        filtroTipoComboBox.addActionListener(listener);
    }

    public void addFiltroEstadoListener(ActionListener listener) {
        filtroEstadoComboBox.addActionListener(listener);
    }

    public int getIdPedidoSeleccionado() {
        int filaSeleccionada = pedidosTable.getSelectedRow();
        if (filaSeleccionada == -1) return -1;
        return (int) tablaModel.getValueAt(filaSeleccionada, 0);
    }

    public void addAgregarListener(ActionListener listener) {
        agregarButton.addActionListener(listener);
    }

    public void addEditarListener(ActionListener listener) {
        editarButton.addActionListener(listener);
    }

    public void addEliminarListener(ActionListener listener) {
        eliminarButton.addActionListener(listener);
    }

    public void addVolverAtrasListener(ActionListener listener) {
        atrasButton.addActionListener(listener);
    }

    public void mostrarMensajeConfirmacion(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Proceso Exitoso", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarMensajeError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean confirmarEliminacion() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar el pedido seleccionado?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        return opcion == JOptionPane.YES_OPTION;
    }
}
