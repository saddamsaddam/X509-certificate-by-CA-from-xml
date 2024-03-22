package org.example;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.KeyStore;
import java.security.cert.Certificate;

import static java.lang.System.out;

public class EmployeeCertificateReader {

    public static void main(String[] args) {
        String[] employeeIds = {"1", "2", "3","4"}; // Employee IDs
        for (String id : employeeIds) {
           out.println( readCertificate(id));
        }
    }

    public static String readCertificate(String id) {
        try {
            // Load the keystore file
            char[] password = "password".toCharArray(); // Keystore password
            KeyStore keyStore = KeyStore.getInstance("JKS");
            try (FileInputStream fis = new FileInputStream(id + ".jks")) {
                keyStore.load(fis, password);
            }

            // Retrieve the certificate using the employee ID as alias
            Certificate certificate = keyStore.getCertificate(id);

            // If certificate is null, return null indicating employee ID not found
            if (certificate == null) {
                return " not";
            } else {
                out.println(
                        "Certificate: "+certificate);

                // Print the start date of the certificate

                // Get the department information associated with the employee ID
                // This assumes that the department information is stored in the keystore attributes or elsewhere
                // Modify this part to suit your actual implementation
                String department = getDepartmentForEmployeeId(id);


                // Return the department information
                return  new EmployeeXMLReader().roleType(id);
            }
        } catch (FileNotFoundException e) {
            // Handle the FileNotFoundException here
            System.out.println("File not found for employee ID: " + id);
            return "not";
        } catch (Exception e) {
            // Handle any other exceptions
            e.printStackTrace();
            return "not"; // Return null if there's any exception
        }
    }

    private static String getDepartmentForEmployeeId(String id) {

        if (id.equals("1")) {
            return "Management";
        } else if (id.equals("2")) {
            return "Sales";
        } else if (id.equals("3")) {
            return "Operation";
        } else {
            return null; // Return null for unknown employee IDs
        }
    }

}
