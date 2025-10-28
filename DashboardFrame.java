package view;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import controller.DashboardController;

public class DashboardFrame extends JFrame {
    private DashboardController dashboardController;
    private JTextField heightField;
    private JTextField weightField;
    private JLabel bmiLabel;
    private JTextArea workoutPlanArea;

    public DashboardFrame(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadUserData();
    }

    private void initializeComponents() {
        setTitle("Gym Tracker - Dashboard");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        heightField = new JTextField(10);
        weightField = new JTextField(10);
        bmiLabel = new JLabel("BMI: Not calculated");
        workoutPlanArea = new JTextArea(10, 40);
        workoutPlanArea.setEditable(false);
        workoutPlanArea.setWrapStyleWord(true);
        workoutPlanArea.setLineWrap(true);
        
        heightField.setPreferredSize(new Dimension(100, 30));
        weightField.setPreferredSize(new Dimension(100, 30));
        bmiLabel.setPreferredSize(new Dimension(200, 30));
        workoutPlanArea.setPreferredSize(new Dimension(500, 150));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel welcomeLabel = new JLabel("Welcome, " + dashboardController.getUser().getUsername() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomePanel.add(welcomeLabel);
        add(welcomePanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Your Details", TitledBorder.LEFT, TitledBorder.TOP));
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(5, 5, 5, 5);

        gbc2.gridx = 0; gbc2.gridy = 0;
        detailsPanel.add(new JLabel("Height (cm):"), gbc2);
        gbc2.gridx = 1;
        detailsPanel.add(heightField, gbc2);

        gbc2.gridx = 0; gbc2.gridy = 1;
        detailsPanel.add(new JLabel("Weight (kg):"), gbc2);
        gbc2.gridx = 1;
        detailsPanel.add(weightField, gbc2);

        gbc2.gridx = 0; gbc2.gridy = 2;
        detailsPanel.add(bmiLabel, gbc2);

        JButton calculateButton = new JButton("Calculate BMI & Get Plan");
        calculateButton.setPreferredSize(new Dimension(200, 30));
        gbc2.gridx = 0; gbc2.gridy = 3; gbc2.gridwidth = 2;
        detailsPanel.add(calculateButton, gbc2);

        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(detailsPanel, gbc);

        JPanel planPanel = new JPanel(new BorderLayout());
        planPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Your Personalized Workout Plan", TitledBorder.LEFT, TitledBorder.TOP));
        planPanel.add(new JScrollPane(workoutPlanArea), BorderLayout.CENTER);

        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(planPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        calculateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculateBMIAndPlan();
            }
        });
    }

    private void setupEventHandlers() {
        // Event handlers are set up in setupLayout method
    }

    private void loadUserData() {
        if (dashboardController.getUser().getHeight() > 0 && dashboardController.getUser().getWeight() > 0) {
            heightField.setText(String.valueOf(dashboardController.getUser().getHeight()));
            weightField.setText(String.valueOf(dashboardController.getUser().getWeight()));
            bmiLabel.setText("BMI: " + String.format("%.2f", dashboardController.getUser().getBmi()));
            displayWorkoutPlan(dashboardController.getUser().getBmi());
        }
    }

    private void calculateBMIAndPlan() {
        try {
            double height = Double.parseDouble(heightField.getText().trim());
            double weight = Double.parseDouble(weightField.getText().trim());

            if (height <= 0 || weight <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter valid height and weight values.", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double heightInMeters = height / 100;
            double bmi = weight / (heightInMeters * heightInMeters);

            bmiLabel.setText("BMI: " + String.format("%.2f", bmi));

            if (dashboardController.updateUserDetails(height, weight, bmi)) {
                displayWorkoutPlan(bmi);
                JOptionPane.showMessageDialog(this, "BMI calculated and saved successfully!", 
                                            "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save data.", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for height and weight.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayWorkoutPlan(double bmi) {
        StringBuilder plan = new StringBuilder();
        plan.append("Based on your BMI of ").append(String.format("%.2f", bmi)).append(":\n\n");

        if (bmi < 18.5) {
            plan.append("Category: Underweight\n\n");
            plan.append("Recommended Workout Plan:\n");
            plan.append("• Focus on strength training 4-5 times per week\n");
            plan.append("• Compound exercises: Squats, Deadlifts, Bench Press\n");
            plan.append("• Progressive overload to build muscle mass\n");
            plan.append("• 8-12 reps for muscle growth\n");
            plan.append("• Adequate rest between workouts (48-72 hours)\n");
            plan.append("• Include protein-rich foods in your diet\n");
        } else if (bmi >= 18.5 && bmi < 25) {
            plan.append("Category: Normal weight\n\n");
            plan.append("Recommended Workout Plan:\n");
            plan.append("• Balanced mix of cardio and strength training\n");
            plan.append("• Strength training 3-4 times per week\n");
            plan.append("• Cardio 2-3 times per week (30-45 minutes)\n");
            plan.append("• Full body workouts or split routines\n");
            plan.append("• 6-12 reps for muscle maintenance\n");
            plan.append("• Include variety in exercises to prevent plateau\n");
            plan.append("• Maintain consistent healthy diet\n");
        } else if (bmi >= 25 && bmi < 30) {
            plan.append("Category: Overweight\n\n");
            plan.append("Recommended Workout Plan:\n");
            plan.append("• Focus on cardio to burn calories (4-5 times per week)\n");
            plan.append("• Include strength training 2-3 times per week\n");
            plan.append("• Start with low-impact cardio: walking, swimming, cycling\n");
            plan.append("• Gradually increase intensity and duration\n");
            plan.append("• Bodyweight exercises: Push-ups, squats, planks\n");
            plan.append("• 12-15 reps for fat loss and muscle toning\n");
            plan.append("• Create a caloric deficit through diet and exercise\n");
        } else {
            plan.append("Category: Obese\n\n");
            plan.append("Recommended Workout Plan:\n");
            plan.append("• Start with low-impact cardio activities\n");
            plan.append("• Walking program: Start with 15-20 minutes daily\n");
            plan.append("• Gradually increase duration and intensity\n");
            plan.append("• Light strength training 2 times per week\n");
            plan.append("• Focus on mobility and flexibility exercises\n");
            plan.append("• Consult with a healthcare provider before starting\n");
            plan.append("• Set realistic goals for gradual weight loss\n");
            plan.append("• Prioritize consistency over intensity initially\n");
        }

        workoutPlanArea.setText(plan.toString());
    }
}
