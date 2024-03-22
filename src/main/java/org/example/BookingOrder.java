package org.example;
import org.apache.poi.EmptyFileException;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.Color;
import java.awt.event.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import static java.lang.System.out;

public class BookingOrder extends JFrame {
    JButton bookingButton;
    JButton add, edit, delete;
    JLabel sumLabel;
    JTable table;
    double sum;
    int rowCount=0;
    List<BookingClass> bookingData;
    DefaultTableModel model;
    int buttonType=0;
    public JPanel createTablePanel(String roleType) {
        // Create the table model
       model = new DefaultTableModel();
        model.addColumn("SKU");
        model.addColumn("Brand");
        model.addColumn("Stock On Hand");
        model.addColumn("Action");

        // Read data from Excel file and populate the table model
        try (FileInputStream file = new FileInputStream("Booking Order.xlsx")) {
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
                            break;
                        case NUMERIC:
                            rowData[i] = cell.getNumericCellValue();
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

        // Make the table non-editable
        table.setDefaultEditor(Object.class, null);


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
        // Custom cell renderer for "Action" column
        table.getColumnModel().getColumn(3).setCellRenderer(new ActionCellRenderer());
        // Add mouse listener to table to capture row clicks
        // Add mouse listener to table to capture double-click events
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = table.getSelectedRow();
                    // Giving Access Privilege
                    if(! roleType.equals("Sales"))
                    {
                        printRowData(selectedRow);
                    }



                }
            }
        });


        // Create a panel to contain the table
        JPanel panel = new JPanel(new BorderLayout());
        JPanel topbuttons=new JPanel();
        add=new JButton("Add");
        edit=new JButton("Edit");
        delete=new JButton("Delete");
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bookingButton.setVisible(true);
                model.setRowCount(0);
                model.addRow(new Object[]{" ", " ", "0.0","No Action"});
                table.setDefaultEditor(Object.class, new DefaultCellEditor(new JTextField()));
                buttonType=0;
                // Print table data
                 // updateTableData();
                //saveTableDataToExcel(table);
            }
        });
        edit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                table.setDefaultEditor(Object.class, new DefaultCellEditor(new JTextField()));
                bookingButton.setText("Edit");
                bookingButton.setVisible(true);
                buttonType=1;

            }
        });
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Print table data
               // updateTableData();
                //saveTableDataToExcel(table);
                buttonType=2;

                deleteBox();


            }
        });
        topbuttons.add(add);
        topbuttons.add(edit);
        topbuttons.add(delete);

        panel.add(topbuttons, BorderLayout.NORTH);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bookingButton = new JButton("Booking");
        bookingButton.setVisible(false);
        bookingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(buttonType==0)
                {
                    addDataToEXL();
                }
                else if(buttonType==1)
                {
                    // save data to exel
                    saveTableDataToExcel(table);
                }



               // updateTableData();

                // Print table data

               // saveTableDataToExcel(table);
            }
        });
        sumLabel = new JLabel("Total Sales: " + sum);
        bottomPanel.add(sumLabel, BorderLayout.NORTH);
        bottomPanel.add(bookingButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        if(roleType.equals("Sales"))
        {
            add.setVisible(false);
            edit.setVisible(false);
            delete.setVisible(false);
        }
        if(roleType.equals("Operation"))
        {
            add.setVisible(false);

            delete.setVisible(false);
        }

        return panel;
    }

    // Custom cell renderer for "Action" column
    private static class ActionCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER); // Center-align text
            if ("Booking".equals(value)) {
                setBackground(Color.GREEN);
            } else {
                setBackground(table.getBackground());
            }
            return cellComponent;
        }
    }
   public void deleteBox()
   {


           JFrame frame = new JFrame("Row Data");
           frame.setSize(new Dimension(500, 250));
           frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

           JPanel body = new JPanel(new BorderLayout());

           JPanel panel1 = new JPanel();
           JLabel quantityLabel = new JLabel("Row Number:");
           JTextField quantityField = new JTextField("1");
           // Set preferred size for quantityField
           quantityField.setPreferredSize(new Dimension(150, 20));

           JPanel panel2 = new JPanel();


           panel1.add(quantityLabel);
           panel1.add(quantityField);

           JPanel panel = new JPanel(new BorderLayout());
           panel.add(panel1, BorderLayout.NORTH);
           panel.add(panel2, BorderLayout.CENTER);

           body.add(new JLabel("    ."), BorderLayout.NORTH);
           body.add(panel, BorderLayout.CENTER);
           JButton Book = new JButton("Delete");
           Book.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                   // Perform booking action here
                   // For example, you can retrieve the quantity and date selected
                   // and process the booking accordingly
                   String quantity = quantityField.getText();
                   DeleteRow(table,Integer.parseInt(quantity));
                   updateTable();

                   // Close the frame after booking action
                   frame.dispose();
               }
           });
           body.add(Book, BorderLayout.SOUTH);

           frame.add(body);
           // Center the frame on the screen
           Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
           int centerX = (int) ((screenSize.getWidth() - frame.getWidth()) / 2);
           int centerY = (int) ((screenSize.getHeight() - frame.getHeight()) / 2);
           frame.setLocation(centerX, centerY);
           frame.setVisible(true);


   }
    public void DeleteRow(JTable table,int rowCoun)
    {
        rowCount=rowCoun-1;
        try {
            // Create a new workbook
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Booking Order");

            // Write header row
            Row headerRow = sheet.createRow(0);
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            for (int col = 0; col < model.getColumnCount(); col++) {
                headerRow.createCell(col).setCellValue(model.getColumnName(col));
            }

            // Write table data to the workbook
            int rowNumber=0;
            for (int row = 0; row < model.getRowCount(); row++) {
                if(rowCount!=row)
                {
                    Row sheetRow = sheet.createRow(rowNumber + 1); // Add 1 to skip header row
                    for (int col = 0; col < model.getColumnCount(); col++) {
                        Object value = model.getValueAt(row, col);
                        Cell cell = sheetRow.createCell(col);
                        if (value instanceof String) {
                            cell.setCellValue((String) value);
                        } else if (value instanceof Double) {
                            cell.setCellValue((Double) value);
                        }

                    }
                    rowNumber++;
                }

            }

            // Write workbook to file
            try (FileOutputStream fileOut = new FileOutputStream("Booking Order.xlsx")) {
                workbook.write(fileOut);
            }
            out.println("Table data saved to Booking order.xlsx successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void saveTableDataToExcel(JTable table) {
        try {
            // Create a new workbook
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Booking Order");

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
                    }

                }
            }

            // Write workbook to file
            try (FileOutputStream fileOut = new FileOutputStream("Booking Order.xlsx")) {
                workbook.write(fileOut);
            }
            out.println("Table data saved to Booking order.xlsx successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateTable()
    {
        model.setRowCount(0);

        // Read data from Excel file and populate the table model
        try (FileInputStream file = new FileInputStream("Booking Order.xlsx")) {
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
                            break;
                        case NUMERIC:
                            rowData[i] = cell.getNumericCellValue();
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
    }
    private void updateTableData() {
        if (table != null) {

            for (int row = 0; row < model.getRowCount(); row++) {
                for (int col = 0; col < model.getColumnCount(); col++) {
                    out.print(model.getValueAt(row, col) + "\t");
                }
                out.println();
            }
        }
    }

    private void printRowData(int row) {
        if (row >= 0) {

            JFrame frame = new JFrame("Row Data");
            frame.setSize(new Dimension(500, 250));
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel body = new JPanel(new BorderLayout());

            JPanel panel1 = new JPanel();
            JLabel quantityLabel = new JLabel("Quantity:");
            JTextField quantityField = new JTextField(model.getValueAt(row, 2).toString());
            // Set preferred size for quantityField
            quantityField.setPreferredSize(new Dimension(150, 20));
            JLabel dateLabel = new JLabel(" Date:     ");
            JXDatePicker datePicker = new JXDatePicker();
            // Set preferred size for datePicker
            datePicker.setPreferredSize(new Dimension(150, 20));
            JPanel panel2 = new JPanel();


            panel1.add(quantityLabel);
            panel1.add(quantityField);
            panel2.add(dateLabel);
            panel2.add(datePicker);

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(panel1, BorderLayout.NORTH);
            panel.add(panel2, BorderLayout.CENTER);

            body.add(new JLabel("    ."), BorderLayout.NORTH);
            body.add(panel, BorderLayout.CENTER);
            JButton Book = new JButton("Book");
            Book.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Perform booking action here
                    // For example, you can retrieve the quantity and date selected
                    // and process the booking accordingly
                    String quantity = quantityField.getText();
                    Date selectedDate = datePicker.getDate();

                    SimpleDateFormat sdfInput = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
                    SimpleDateFormat sdfOutput = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.ENGLISH);
                    Date date;
                    try {
                        // Parse the date string using the input format
                        date = sdfInput.parse(selectedDate.toString());
                        // Format the date using the desired output format
                        String formattedDate = sdfOutput.format(date);
                        AddToXLXS(new BookingClass(
                                model.getValueAt(row, 0).toString(),
                                Double.parseDouble(quantity),
                                formattedDate
                        ));
                    } catch (ParseException ex) {
                        throw new RuntimeException(ex);
                    }

                    // Close the frame after booking action
                    frame.dispose();
                }
            });
            body.add(Book, BorderLayout.SOUTH);

            frame.add(body);
            // Center the frame on the screen
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int centerX = (int) ((screenSize.getWidth() - frame.getWidth()) / 2);
            int centerY = (int) ((screenSize.getHeight() - frame.getHeight()) / 2);
            frame.setLocation(centerX, centerY);
            frame.setVisible(true);

        }
    }


    public List<BookingClass> bookingListExelData() {
       bookingData = new ArrayList<>();

        try (FileInputStream file = new FileInputStream("Booking Order.xlsx")) {
            Workbook workbook = WorkbookFactory.create(file);
            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

            // Iterate over the rows, skipping the header row
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row != null) {
                    // Extract data from the row
                    String name = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                    double quantity;
                    Cell quantityCell = row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    if (quantityCell.getCellType() == CellType.NUMERIC) {
                        quantity = quantityCell.getNumericCellValue();
                    } else {
                        // Handle the case where the quantity cell contains a string value
                        String quantityStr = quantityCell.getStringCellValue();
                        // You may need to parse the string quantity value appropriately
                        // For example, you could use Double.parseDouble(quantityStr)
                        // Or handle the parsing error as needed
                        // Here, we'll set the quantity to 0 if parsing fails
                        quantity = 0;
                    }
                    String date = row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();

                    // Create BookingClass object and add it to the list
                    BookingClass booking = new BookingClass(name, quantity, date);
                    bookingData.add(booking);
                }
            }
        } catch (IOException | EncryptedDocumentException e) {
            e.printStackTrace();
        }

        return bookingData;
    }

    public List<BookingClass> bookinglist() {
        bookingData = new ArrayList<>();

        try (FileInputStream file = new FileInputStream("Order Status.xlsx")) {
            Workbook workbook = WorkbookFactory.create(file);
            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

            // Iterate over the rows, skipping the header row
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row != null) {
                    // Extract data from the row
                    String name = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                    double quantity;
                    Cell quantityCell = row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    if (quantityCell.getCellType() == CellType.NUMERIC) {
                        quantity = quantityCell.getNumericCellValue();
                    } else {
                        // Handle the case where the quantity cell contains a string value
                        String quantityStr = quantityCell.getStringCellValue();
                        // You may need to parse the string quantity value appropriately
                        // For example, you could use Double.parseDouble(quantityStr)
                        // Or handle the parsing error as needed
                        // Here, we'll set the quantity to 0 if parsing fails
                        quantity = 0;
                    }
                    String date = row.getCell(2, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();

                    // Create BookingClass object and add it to the list
                    BookingClass booking = new BookingClass(name, quantity, date);
                    bookingData.add(booking);
                }
            }
        } catch (IOException | EncryptedDocumentException e) {
            e.printStackTrace();
        }

        return bookingData;
    }
    public static void saveBookingDataToExcel(List<BookingClass> bookingData, String filePath) {
        try (Workbook workbook = WorkbookFactory.create(true)) {
            Sheet sheet = workbook.createSheet("Booking Data");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("SKU");
            headerRow.createCell(1).setCellValue("Brand");
            headerRow.createCell(2).setCellValue("Stock On Hand");
            headerRow.createCell(2).setCellValue("Action");

            // Write booking data to rows
            for (int i = 0; i < bookingData.size(); i++) {
                BookingClass booking = bookingData.get(i);
                System.out.println(booking.toString());
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(booking.getName());
                row.createCell(1).setCellValue(booking.getDate());
                row.createCell(2).setCellValue(booking.getQuantity());
                row.createCell(3).setCellValue("Booking");
            }

            // Write workbook content to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
                System.out.println("Data saved to Excel file successfully.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void addDataToEXL() {
        // different two way here for add value to exel
         List<BookingClass> bcm=new ArrayList<>();
        bcm= bookingListExelData();
         System.out.println(bcm.size());
        List<BookingClass> bc= retrieveTableData();
        bcm.addAll(bc);
        System.out.println(bcm.size());


      // saveBookingDataToExcel(bcm,"Booking Order.xlsx");

      model = (DefaultTableModel) table.getModel();

        try (FileInputStream file = new FileInputStream("Booking Order.xlsx");
             Workbook workbook = WorkbookFactory.create(file)) {

            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

            // Get the last row index
            int lastRowIndex = sheet.getLastRowNum();

            // Write table data to the workbook starting from the last row index + 1
            for (int row = 0; row < model.getRowCount(); row++) {
                Row sheetRow = sheet.createRow(lastRowIndex + row + 1); // Add 1 to skip header row
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Object value = model.getValueAt(row, col);
                    Cell cell = sheetRow.createCell(col);
                    if (value instanceof String) {
                        cell.setCellValue((String) value);
                    } else if (value instanceof Double) {
                        cell.setCellValue((Double) value);
                    }
                }
            }

            // Write workbook to file
            try (FileOutputStream fileOut = new FileOutputStream("Booking Order.xlsx")) {
                workbook.write(fileOut);
                System.out.println("Table data added to Booking Order.xlsx successfully.");
            }
        } catch (IOException | EncryptedDocumentException e) {
            e.printStackTrace();
        }

    }

    public List<BookingClass> retrieveTableData() {
        List<BookingClass> tableData = new ArrayList<>();

        // Retrieve the table model


        // Iterate over the rows of the table model
        for (int row = 0; row < model.getRowCount(); row++) {
            String name = model.getValueAt(row, 0).toString();
            double quantity = Double.parseDouble(model.getValueAt(row, 2).toString());
            String date = model.getValueAt(row, 1).toString();

            // Create a BookingClass object and add it to the list
            BookingClass booking = new BookingClass(name, quantity, date);
            tableData.add(booking);
        }

        return tableData;
    }
    public void AddToXLXS(BookingClass bc) {
        File filee = new File("Order Status.xlsx");

        // Check if the file doesn't exist
        if (!filee.exists()) {
            try {
                // If the file doesn't exist, create it
                if (filee.createNewFile()) {
                    System.out.println("File created: " + filee.getAbsolutePath());
                } else {
                    System.out.println("File creation failed.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Get the existing booking data from the Excel file
         bookingData = bookinglist();

        // Add the new booking to the list
          bookingData.add(bc);

        // Write the updated data back to the Excel file
        try (FileOutputStream fileOut = new FileOutputStream(filee);
             Workbook workbook = new XSSFWorkbook()) {

            // Create a new sheet
            Sheet sheet = workbook.createSheet("Booking Data");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Name");
            headerRow.createCell(1).setCellValue("Quantity");
            headerRow.createCell(2).setCellValue("Date");

            // Write booking data to rows
            int rowNum = 1;
            for (BookingClass booking : bookingData) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(booking.getName());
                row.createCell(1).setCellValue(booking.getQuantity());
                row.createCell(2).setCellValue(booking.getDate().toString()); // Convert Date to String for simplicity
            }


            // Write the workbook content to the file
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
