package cl.duoc.speedfast.view;

import cl.duoc.speedfast.model.entity.Repartidor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Interfaz gráfica encargada de desplegar el listado histórico de los repartidores.
 * Incorpora un componente JTable no editable para la visualización segura de los registros de MySQL.
 */
public class VentanaListaRepartidores extends JFrame {

    private JLabel tituloLabel;
    private JTable repartidoresTable;
    private DefaultTableModel tablaModel;

    private JButton agregarButton;
    private JButton editarButton;
    private JButton eliminarButton;
    private JButton atrasButton;

    public VentanaListaRepartidores() {
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
        tituloLabel = new JLabel("Administración de Repartidores", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 18));
        tituloLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));

        String[] columnas = {"ID Repartidor", "Nombre Repartidor"};
        tablaModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        repartidoresTable = new JTable(tablaModel);
        repartidoresTable.getTableHeader().setReorderingAllowed(false);
        repartidoresTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        agregarButton = new JButton("Agregar Nuevo");
        editarButton = new JButton("Editar Seleccionado");
        eliminarButton = new JButton("Eliminar Seleccionado");
        atrasButton = new JButton("Atrás");
    }

    private void construirLayout() {
        add(tituloLabel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(repartidoresTable);
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        panelTabla.add(scrollPane, BorderLayout.CENTER);
        add(panelTabla, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelBotones.add(agregarButton);
        panelBotones.add(editarButton);
        panelBotones.add(eliminarButton);
        panelBotones.add(atrasButton);
        add(panelBotones, BorderLayout.SOUTH);
    }

    public int getIdRepartidorSeleccionado() {
        int filaSeleccionada = repartidoresTable.getSelectedRow();
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

    public void cerrarVentana() {
        this.dispose();
    }

    public void actualizarTabla(List<Repartidor> listaRepartidores) {
        tablaModel.setRowCount(0);

        for (Repartidor r : listaRepartidores) {
            Object[] fila = {
                    r.getIdRepartidor(),
                    r.getNombreRepartidor()
            };

            tablaModel.addRow(fila);
        }
    }

    public void mostrarMensajeConfirmacion(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Proceso exitoso", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarMensajeError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean confirmarEliminacion() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar el repartidor seleccionado?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        return opcion == JOptionPane.YES_OPTION;
    }
}
