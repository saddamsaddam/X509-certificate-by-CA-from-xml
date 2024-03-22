package org.example;

public class BookingClass {
    private String name;
    private double quantity;
    private String date;


    // Constructor
    public BookingClass(String name, double quantity, String date) {
        this.name = name;
        this.quantity = quantity;
        this.date = date;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // Override toString() method for debugging or display purposes
    @Override
    public String toString() {
        return "BookingClass{" +
                "name='" + name + '\'' +
                ", quantity=" + quantity +
                ", date='" + date + '\'' +
                '}';
    }
}
