package org.example;
public class CustomSales {
    private double employeeID;
    private String employeeName;
    private double sales;
    private String vendor;

    public CustomSales(){

    }
    // Constructor
    public CustomSales(double employeeID, String employeeName, double sales, String vendor) {
        this.employeeID = employeeID;
        this.employeeName = employeeName;
        this.sales = sales;
        this.vendor = vendor;
    }

    // Getter and setter methods
    public double getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(double employeeID) {
        this.employeeID = employeeID;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public double getSales() {
        return sales;
    }

    public void setSales(double sales) {
        this.sales = sales;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }
}
