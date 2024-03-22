package org.example;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.CustomSales;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;


public class OwnSales extends JFrame {
    JButton update;
    JLabel sumLabel;
    JTable table;
   double sum;
   double idNumber;
   int cc;
   List<CustomSales> salesData;
    List<CustomSales> singleSalesData;
    public JPanel createTablePanel(String  idNumber1) {


        idNumber=Double.parseDouble(idNumber1);
        // Create the table model
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Employee ID");
        model.addColumn("Employee Name");
        model.addColumn("Sales");
        model.addColumn("Vendor");
        salesData=new ArrayList<>();
        // Read data from Excel file and populate the table model
        try (FileInputStream file = new FileInputStream("SALES.xlsx")) {
            sum = 0;
            Workbook workbook = WorkbookFactory.create(file);
            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

            // Skip the first row and start iterating from the second row
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                Object[] rowData = new Object[4];

                // Read Employee ID from the first column
                Cell idCell = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                double employeeID;
                if (idCell.getCellType() == CellType.NUMERIC) {
                    employeeID = idCell.getNumericCellValue(); // Assuming ID is stored as a double
                } else if (idCell.getCellType() == CellType.STRING) {
                    try {
                        employeeID = Double.parseDouble(idCell.getStringCellValue());
                    } catch (NumberFormatException e) {
                        // Handle the case where the string cannot be parsed as a double
                        continue; // Skip this row if ID cannot be parsed as a double
                    }
                } else {
                    continue; // Skip this row if ID cell type is neither numeric nor string
                }

                // Check if the Employee ID matches the given ID number
                if (employeeID == idNumber) {
                    for (int i = 0; i < 4; i++) {
                        Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        switch (cell.getCellType()) {
                            case STRING:
                                rowData[i] = cell.getStringCellValue();
                                out.println(rowData[i]);
                                break;
                            case NUMERIC:
                                rowData[i] = cell.getNumericCellValue();
                                if (i == 2) {
                                    sum = sum + (Double) rowData[i];
                                }
                                break;
                            default:
                                rowData[i] = "";
                        }
                    }
                    model.addRow(rowData); // Add row only if Employee ID matches
                }
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

        // Add key listener to table to capture Enter key presses
        // Add key listener to table to capture Enter key presses
        table.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {
                // Check if Enter key is pressed
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    // Iterate through each row
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    int selectedRow = table.getSelectedRow();
                    updateRowData(selectedRow,
                            new CustomSales(Double.parseDouble(
                                    model.getValueAt(selectedRow,0).toString())
                                    ,model.getValueAt(selectedRow,1).toString()
                                    ,Double.parseDouble(model.getValueAt(selectedRow,2).toString())
                                    ,model.getValueAt(selectedRow,3).toString()));
                    for (int row = 0; row < model.getRowCount(); row++) {
                        // Print row data
                        System.out.print("Row " + row + ": ");
                        for (int col = 0; col < model.getColumnCount(); col++) {
                            System.out.print(model.getValueAt(row, col) + "\t");
                        }
                        System.out.println(); // Move to next line after printing row data
                    }
                }
            }
        });

        // Create a panel to contain the table
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        update = new JButton("Update");

        sumLabel = new JLabel("Total Sales: " + sum);
        bottomPanel.add(sumLabel, BorderLayout.NORTH);
       // bottomPanel.add(update, BorderLayout.CENTER);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    public void updateRowData(int rowNumber, CustomSales rowData)
    {
        salesData=salesData();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sales Data");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Employee ID");
            headerRow.createCell(1).setCellValue("Employee Name");
            headerRow.createCell(2).setCellValue("Sales");
            headerRow.createCell(3).setCellValue("Vendor");

            // Write data rows
            int rowNum = 1;
            cc=0;
            sum=0;
            for (CustomSales sales : salesData) {
                 if(sales.getEmployeeID()!=0){
                     if(cc==rowNumber)
                     {
                         Row row = sheet.createRow(rowNum++);
                         row.createCell(0).setCellValue(rowData.getEmployeeID());
                         row.createCell(1).setCellValue(rowData.getEmployeeName());
                         row.createCell(2).setCellValue(rowData.getSales());
                         row.createCell(3).setCellValue(rowData.getVendor());
                         sum=sum+rowData.getSales();
                     }
                     else {
                         Row row = sheet.createRow(rowNum++);
                         row.createCell(0).setCellValue(sales.getEmployeeID());
                         row.createCell(1).setCellValue(sales.getEmployeeName());
                         row.createCell(2).setCellValue(sales.getSales());
                         row.createCell(3).setCellValue(sales.getVendor());
                         if(sales.getEmployeeID()==rowData.getEmployeeID())
                         {
                             sum=sum+sales.getSales();
                         }
                     }


                     cc++;
                 }


            }

            // Write workbook to file
            try (FileOutputStream fileOut = new FileOutputStream("SALES.xlsx")) {
                workbook.write(fileOut);
                sumLabel.setText("Total Sales: " + sum);
                System.out.println("Sales data saved to Sales.xlsx successfully.");
            }
        } catch (IOException f) {
            f.printStackTrace();
        }
    }

public List<CustomSales> salesData()
{
    salesData = new ArrayList<>();
    try (FileInputStream file = new FileInputStream("SALES.xlsx")) {
        sum = 0;
        Workbook workbook = WorkbookFactory.create(file);
        Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

        // Iterate over each row in the sheet
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            CustomSales sales = new CustomSales(); // Create a CustomSales object for each row

            // Iterate over each cell in the row
            for (int i = 0; i < row.getLastCellNum(); i++) {
                Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                switch (cell.getCellType()) {
                    case STRING:
                        String stringValue = cell.getStringCellValue();
                        // Set each column data to CustomSales object
                        switch (i) {
                            case 0:
                                if (!stringValue.isEmpty()) {
                                    sales.setEmployeeID(Double.parseDouble(stringValue));
                                }
                                break;
                            case 1:
                                sales.setEmployeeName(stringValue);
                                break;
                            case 2:
                                if (!stringValue.isEmpty()) {
                                    sales.setSales(Double.parseDouble(stringValue));
                                }
                                break;
                            case 3:
                                sales.setVendor(stringValue);
                                break;
                            default:
                                break;
                        }
                        break;
                    case NUMERIC:
                        double numericValue = cell.getNumericCellValue();
                        // Set each column data to CustomSales object
                        switch (i) {
                            case 0:
                                sales.setEmployeeID(numericValue);
                                break;
                            case 1:
                                sales.setEmployeeName(String.valueOf(numericValue));
                                break;
                            case 2:
                                sales.setSales(numericValue);
                                break;
                            case 3:
                                sales.setVendor(String.valueOf(numericValue));
                                break;
                            default:
                                break;
                        }
                        if (i == 2) {
                            sum += numericValue;
                        }
                        break;
                    default:
                        break;
                }
            }
            salesData.add(sales); // Add CustomSales object to the list
        }
    } catch (IOException | EncryptedDocumentException e) {
        e.printStackTrace();
    }

    return salesData;
}

    public List<CustomSales> getTableData(JTable table) {
        List<CustomSales> salesData = new ArrayList<>();

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int rowCount = model.getRowCount();
        int columnCount = model.getColumnCount();

        // Start iterating from the second row (index 1) to skip the header row
        for (int row = 1; row < rowCount; row++) {
            CustomSales sales = new CustomSales();
            for (int col = 0; col < columnCount; col++) {
                Object cellValue = model.getValueAt(row, col);
                switch (col) {
                    case 0:
                        sales.setEmployeeID(Double.parseDouble(cellValue.toString()));
                        break;
                    case 1:
                        sales.setEmployeeName(cellValue.toString());
                        break;
                    case 2:
                        sales.setSales(Double.parseDouble(cellValue.toString()));
                        break;
                    case 3:
                        sales.setVendor(cellValue.toString());
                        break;
                    default:
                        break;
                }
            }
            salesData.add(sales);
        }

        return salesData;
    }

    private void saveTableDataToExcel(JTable table,List<CustomSales> salseData1) {
        salesData=salseData1;
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
