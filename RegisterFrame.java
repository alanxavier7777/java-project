package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import controller.AuthController;
import model.User;

public class RegisterFrame extends JFrame {
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private AuthController authController;

    public RegisterFrame(AuthController authController) {
        this.authController = authController;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    private void initializeComponents() {
        setTitle("Gym Tracker - Register");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        usernameField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        
        usernameField.setPreferredSize(new Dimension(200, 30));
        emailField.setPreferredSize(new Dimension(200, 30));
        passwordField.setPreferredSize(new Dimension(200, 30));
        confirmPasswordField.setPreferredSize(new Dimension(200, 30));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Register New Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1; gbc.gridy = 1; gbc.gridx = 0;
        mainPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(confirmPasswordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton registerButton = new JButton("Register");
        JButton loginButton = new JButton("Back to Login");
        
        registerButton.setPreferredSize(new Dimension(100, 30));
        loginButton.setPreferredSize(new Dimension(120, 30));
        
        buttonPanel.add(registerButton);
        buttonPanel.add(loginButton);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleBackToLogin();
            }
        });

        confirmPasswordField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });
    }

    private void setupEventHandlers() {
        // Event handlers are set up in setupLayout method
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters long.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (authController.userExists(username, email)) {
            JOptionPane.showMessageDialog(this, "Username or email already exists.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User newUser = new User(username, email, password);
        if (authController.registerUser(newUser)) {
            JOptionPane.showMessageDialog(this, "Registration successful! Please login.", 
                                        "Success", JOptionPane.INFORMATION_MESSAGE);
            handleBackToLogin();
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed. Please try again.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleBackToLogin() {
        this.dispose();
        authController.showLoginView();
    }
}
