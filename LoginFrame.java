package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import controller.AuthController;
import model.User;
import controller.DashboardController;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private AuthController authController;

    public LoginFrame(AuthController authController) {
        this.authController = authController;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    private void initializeComponents() {
        setTitle("Gym Tracker - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        
        usernameField.setPreferredSize(new Dimension(200, 30));
        passwordField.setPreferredSize(new Dimension(200, 30));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Gym Tracker Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1; gbc.gridy = 1; gbc.gridx = 0;
        mainPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");
        
        loginButton.setPreferredSize(new Dimension(100, 30));
        registerButton.setPreferredSize(new Dimension(100, 30));
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });

        passwordField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
    }

    private void setupEventHandlers() {
        // Event handlers are set up in setupLayout method
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = authController.authenticateUser(username, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            DashboardController dashboardController = new DashboardController(user, authController.getDatabaseManager());
            dashboardController.showDashboardView();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRegister() {
        this.dispose();
        authController.showRegisterView();
    }
}
