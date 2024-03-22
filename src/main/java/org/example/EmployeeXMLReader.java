package org.example;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class EmployeeXMLReader {
    public String roleType(String idNumber){
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
                String salary = employeeElement.getElementsByTagName("salary").item(0).getTextContent();
                String email = employeeElement.getElementsByTagName("email").item(0).getTextContent();
                String phone = employeeElement.getElementsByTagName("phone").item(0).getTextContent();
                String access = employeeElement.getElementsByTagName("access").item(0).getTextContent();
                String sales = employeeElement.getElementsByTagName("sales").item(0).getTextContent();



                if(idNumber.equals(id))
                {
                    return department;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return " ";
    }
    public static void main(String[] args) {

    }
}


