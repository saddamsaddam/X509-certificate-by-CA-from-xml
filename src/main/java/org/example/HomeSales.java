package org.example;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class HomeSales extends JFrame {
String idNumber;
    public HomeSales(String id) {
        idNumber=id;
        setTitle("Sales Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 700);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Create tabs and add them to the tabbed pane
        JPanel stockInventoryPanel = new StockInventory().createTablePanel("Sales");
        JPanel orderBookingPanel = new JPanel();
        JPanel orderStatusPanel = new JPanel();
        JPanel companySalesPanel = new JPanel();
        JPanel employeeRecordsPanel = new JPanel();
        JPanel ownSalesRecordsPanel = new JPanel();

        tabbedPane.addTab("Stock Inventory", stockInventoryPanel);
        tabbedPane.addTab("Order Booking", orderBookingPanel);
        tabbedPane.addTab("Order Status", orderStatusPanel);
        tabbedPane.addTab("Own Sales Records", ownSalesRecordsPanel);

        // Add tabbed pane to the frame
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        // Add ChangeListener to the tabbedPane
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
                int selectedIndex = sourceTabbedPane.getSelectedIndex();
                System.out.println("Selected Index: " + selectedIndex);
                switch (selectedIndex) {
                    case 0:
                        updatePanel(stockInventoryPanel, new StockInventory().createTablePanel("Sales"));
                        System.out.println("Selected Stock Inventory tab");
                        break;
                    case 1:
                        updatePanel( orderBookingPanel, new BookingOrder().createTablePanel("Sales"));
                        System.out.println("Selected Order Booking tab");
                        break;
                    case 2:
                        updatePanel(orderStatusPanel, new OrderStatus().createTablePanel("Sales"));
                        System.out.println("Selected Order Status tab");
                        break;

                    case 3:
                        updatePanel(ownSalesRecordsPanel, new  OwnSales().createTablePanel(idNumber));
                        System.out.println("Selected Own Sales Records tab");
                        break;
                    default:
                        break;
                }
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }
    static private void updatePanel(JPanel panel, Component componentToAdd) {
        if (panel.getComponentCount() > 0) {
            panel.remove(0);
        }


        panel.add(componentToAdd);
        panel.revalidate();
        panel.repaint();

    }

    public static void main(String[] args) {

        new HomeSales("").setVisible(true);
    }
}
