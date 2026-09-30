package cl.duoc.speedfast.view;

import cl.duoc.speedfast.model.entity.Entrega;

import javax.swing.*;
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
    private JButton atrasButton;

    public VentanaListaEntregas() {
        setTitle("SpeedFast App");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 15));
        setResizable(false);

        inicializarComponentes();
        construirLayout();
    }

    private void inicializarComponentes() {
        tituloLabel = new JLabel("Historial de Entregas Registradas", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 18));
        tituloLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));

        String[] columnas = {"ID Entrega", "ID Pedido", "ID Repartidor", "Fecha", "Hora"};
        tablaModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        entregasTable = new JTable(tablaModel);
        entregasTable.getTableHeader().setReorderingAllowed(false);

        atrasButton = new JButton("Atrás");
    }

    private void construirLayout() {
        add(tituloLabel, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(entregasTable);
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        panelTabla.add(scrollPane, BorderLayout.CENTER);
        add(panelTabla, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelBotones.add(atrasButton);
        add(panelBotones, BorderLayout.SOUTH);
    }

    public void addVolverAtrasListener(ActionListener listener) {
        atrasButton.addActionListener(listener);
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

    public void mostrarMensajeError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
