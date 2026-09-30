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
    private JButton atrasButton;

    public VentanaListaRepartidores() {
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
        tituloLabel = new JLabel("Lista de Repartidores", SwingConstants.CENTER);
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

        atrasButton = new JButton("Atrás");
    }

    public void construirLayout() {
        add(tituloLabel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(repartidoresTable);

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

    public void mostrarMensajeError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
