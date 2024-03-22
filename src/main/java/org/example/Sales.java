package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import static java.lang.System.out;


public class Sales extends JFrame {
    JButton update;
    JLabel sumLabel;
    JTable table;
   double sum;
    public JPanel createTablePanel(String roleType) {
        // Create the table model
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Employee ID");
        model.addColumn("Employee Name");
        model.addColumn("Sales");
        model.addColumn("Vendor");

        // Read data from Excel file and populate the table model
        try (FileInputStream file = new FileInputStream("SALES.xlsx")) {
            sum=0;
            Workbook workbook = WorkbookFactory.create(file);
            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

            // Skip the first row and start iterating from the second row
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                Object[] rowData = new Object[4];
                for (int i = 0; i < 4; i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    switch (cell.getCellType()) {
                        case STRING:
                            rowData[i] = cell.getStringCellValue();
                            out.println(rowData[i]);
                            break;
                          case NUMERIC:
                            rowData[i] = cell.getNumericCellValue();
                            if(i==2)
                            {

                                sum=sum+Double.parseDouble(rowData[i].toString().trim());
                            }
                            break;
                        default:
                            rowData[i] = "";
                    }
                }
                model.addRow(rowData);
            }

        } catch (IOException | EncryptedDocumentException e) {
            e.printStackTrace();
        }

        // Create the table
        table = new JTable(model);
        table.setPreferredScrollableViewportSize(new Dimension(1200, 500));
        table.setFillsViewportHeight(true);

        // Center-align text in table cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        // Set column widths
        int minWidth = 140; // Minimum width for columns
        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setMinWidth(minWidth);
        }

        // Create a panel to contain the table
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        update = new JButton("Update");
        update.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Print table data
                updateTableData();
                saveTableDataToExcel(table);
            }
        });
        sumLabel=new JLabel("Total Sales: "+sum);
        bottomPanel.add(sumLabel,BorderLayout.NORTH);
      //  bottomPanel.add(update,BorderLayout.CENTER);
        if(roleType.equals("Sales"))
        {
            update.setVisible(false);
        }
        if(roleType.equals("Operation"))
        {
            update.setVisible(false);
        }
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void saveTableDataToExcel(JTable table) {
        try {
            // Create a new workbook
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("SALES");

            // Write header row
            Row headerRow = sheet.createRow(0);
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            for (int col = 0; col < model.getColumnCount(); col++) {
                headerRow.createCell(col).setCellValue(model.getColumnName(col));
            }

            // Write table data to the workbook
            for (int row = 0; row < model.getRowCount(); row++) {
                Row sheetRow = sheet.createRow(row + 1); // Add 1 to skip header row
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Object value = model.getValueAt(row, col);
                    Cell cell = sheetRow.createCell(col);
                    if (value instanceof String) {
                        cell.setCellValue((String) value);
                    } else if (value instanceof Double) {

                        cell.setCellValue((Double) value);
                        if(col==2)
                        {

                            sum=sum+ (Double) value;
                        }
                    }
                }
            }

            // Write workbook to file
            try (FileOutputStream fileOut = new FileOutputStream("SALES.xlsx")) {
                workbook.write(fileOut);
            }
             sumLabel.setText("Total Sales: "+sum);
            out.println("Table data saved to SALES.xlsx successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateTableData() {
        if (table != null) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            for (int row = 0; row < model.getRowCount(); row++) {
                for (int col = 0; col < model.getColumnCount(); col++) {
                    out.print(model.getValueAt(row, col) + "\t");
                    // insert this data to xlsx file.
                }
                out.println();
            }
        }
    }

}
