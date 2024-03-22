package org.example;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static java.lang.System.out;

// The main class representing the login view, extends JFrame
public class LoginFrame extends JFrame {
    boolean result=true;

    // Components for the username and password fields
    private JTextField usernameField;
    private JPasswordField passwordField;
    JButton registerButton;
    // File system view to handle file-related operations
    FileSystemView fileSystemView = FileSystemView.getFileSystemView();

    // Get the desktop directory
    java.io.File desktopDir = fileSystemView.getHomeDirectory();

    // Create a subdirectory named "file"
    String url = desktopDir.getAbsolutePath() + "/file";
    java.io.File subdirectory = new java.io.File(url);

    // Constructor for the LoginView class
    public LoginFrame() throws IOException {
        // insert registration 3 users if not exist


        // Set up the frame
        setTitle("Login Page");
        setSize(1300, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a custom panel with background image
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add a JLabel for the text at the top-center
        JLabel topTextLabel = new JLabel("Authorisation (Access Control) System");
        topTextLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Set font and size if needed
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span across the whole width
        gbc.anchor = GridBagConstraints.PAGE_START; // Align to the top
        mainPanel.add(topTextLabel, gbc);

        // Create components
        JLabel usernameLabel = new JLabel("Username:");


        usernameField = new JTextField(15);
        usernameField.setHorizontalAlignment(JTextField.CENTER); // Center align text

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get entered username and password
                String enteredId = usernameField.getText();

                String roleType= EmployeeCertificateReader.readCertificate(enteredId);

                  out.println(roleType);
                // Check against predefined usernames and password
                if (roleType.equals("Management")) {
                   new HomePage(enteredId).setVisible(true);
                   dispose();
                }
                else if (roleType.equals("Operation")) {
                    new HomeOperation(enteredId).setVisible(true);
                    dispose();
                }
                else if (roleType.equals("Sales")) {
                    new HomeSales(enteredId).setVisible(true);
                    dispose();
                }else {
                    // Display an error message if login fails
                    JOptionPane.showMessageDialog(LoginFrame.this, "Login failed. This ID not exist.");
                }
            }
        });

        // Add components to the panel
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1; // Reset gridwidth to default (1)
        mainPanel.add(usernameField, gbc);


        gbc.gridx = 1;
        gbc.gridy = 3; // Increment the gridy for the login button
        mainPanel.add(loginButton, gbc);


        // Add the main panel to the frame
        // add(mainPanel);
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        JPanel top=new  JPanel(new BorderLayout());
        top.setBackground(Color.YELLOW);
        top.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 10)); // Set top and right margin


        // add(top, BorderLayout.NORTH);

        // Center the frame on the screen
        setLocationRelativeTo(null);
    }

    // Custom JPanel with background image

    // Method to validate login credentials
    private boolean isValidLogin(String username) {


        return result;
    }

    // Main method to launch the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // generate certificate
                    new EmployeeCertificateGenerator().Certificategenrate();
                    new LoginFrame().setVisible(true);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }
}
