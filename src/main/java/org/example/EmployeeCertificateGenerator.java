package org.example;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileOutputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;

public class EmployeeCertificateGenerator {
    public void Certificategenrate()
    {
        try {
            // Create a DocumentBuilder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Parse the XML file
            Document document = builder.parse("employees.xml");

            // Get the root element
            Element root = document.getDocumentElement();

            // Get a list of all "employee" elements
            NodeList employeeList = root.getElementsByTagName("employee");

            // Iterate over each employee
            for (int i = 0; i < employeeList.getLength(); i++) {
                Element employeeElement = (Element) employeeList.item(i);
                String id = employeeElement.getElementsByTagName("id").item(0).getTextContent();
                String name = employeeElement.getElementsByTagName("name").item(0).getTextContent();
                String department = employeeElement.getElementsByTagName("department").item(0).getTextContent();

                // Generate a key pair for the employee
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
                keyPairGenerator.initialize(2048, new SecureRandom());
                KeyPair keyPair = keyPairGenerator.generateKeyPair();

                // Generate a  X509 CA certificate
                X509Certificate certificate = generateCACertificate(keyPair, name);

                // Save the certificate and private key to a keystore file
                saveToKeystore(id, keyPair.getPrivate(), certificate);

                // Print certificate information
                System.out.println("Certificate generated for employee: " + name);
                System.out.println("Department: " + department);
                System.out.println("Employee ID: " + id);
                System.out.println("Certificate: ");
                System.out.println(certificate);
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to generate a  X509 CA certificate
    private static X509Certificate generateCACertificate(KeyPair keyPair, String name) throws Exception {
        Date startDate = new Date();
        Date expiryDate = new Date(startDate.getTime() + 365 * 24 * 60 * 60 * 1000); // 1 year validity
        String distinguishedName = "CN=" + name;
        return CertificateUtil.generateCACertificate(distinguishedName, keyPair, startDate, expiryDate);
    }
    // Method to save the certificate and private key to a keystore file
    private static void saveToKeystore(String alias, PrivateKey privateKey, Certificate certificate) throws Exception {
        char[] password = "password".toCharArray(); // Keystore password

        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(null, password);

        // Add the private key and certificate to the keystore
        keyStore.setKeyEntry(alias, privateKey, password, new Certificate[]{certificate});

        // Save the keystore to a file
        try (FileOutputStream fos = new FileOutputStream(alias + ".jks")) {
            keyStore.store(fos, password);
        }
    }
}

