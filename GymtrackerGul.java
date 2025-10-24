import GymTrackerProject.src.core.Workout;
import GymTrackerProject.src.core.WorkoutDAO;
import GymTrackerProject.src.service.WorkoutRecommender;
import GymTrackerProject.src.models.CardioExercise;
import GymTrackerProject.src.models.Exercise;
import GymTrackerProject.src.models.Set;
import GymTrackerProject.src.models.user;
import GymTrackerProject.src.models.WeightLiftingExercise;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The main GUI for the Gym Tracker application, utilizing Swing.
 */
public class GymTrackerGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private Workout currentWorkout;
    private List<Workout> history = new ArrayList<>();
    private user currentUser;

    // --- GUI Components ---
    private JTextArea workoutLogArea;
    private JComboBox<String> exerciseTypeBox;
    private JTextField nameField;
    private JTextField durationField;
    private JTextField repField;
    private JTextField weightField;
    private JTextField distanceField;
    private JTextField caloriesField;
    private JTextField workoutTitleField;
    private JLabel currentWorkoutLabel;

    public GymTrackerGUI(user user) {
        super("Gym Tracker Application");
        this.currentUser = user;
        this.currentWorkout = new Workout("New Workout - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        
        // Setup the DAO (using a mock connection for compilation)
        try {
             // In a real app, this connection would be managed properly
             Connection mockConnection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
             new WorkoutDAO(mockConnection); 
        } catch (SQLException e) {
            System.err.println("Database connection setup failed: " + e.getMessage());
        }

        initializeGUI();
        loadHistory(); 
    }

    private void initializeGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // --- Header Panel ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        currentWorkoutLabel = new JLabel("Current Workout: " + currentWorkout.getTitle(), SwingConstants.CENTER);
        currentWorkoutLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        headerPanel.add(currentWorkoutLabel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // --- Main Content Panel (Center) ---
        JPanel mainContentPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        // Left Panel: Logging Interface
        mainContentPanel.add(createLoggingPanel());

        // Right Panel: Workout Log / History
        mainContentPanel.add(createLogHistoryPanel());

        add(mainContentPanel, BorderLayout.CENTER);

        // --- Footer Panel (Actions) ---
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton saveButton = new JButton("Save Workout");
        saveButton.addActionListener(this::saveWorkout);
        JButton loadButton = new JButton("Load History");
        loadButton.addActionListener(e -> loadHistory());
        
        footerPanel.add(saveButton);
        footerPanel.add(loadButton);
        
        add(footerPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
        
        updateLogArea();
    }
    
    private JPanel createLoggingPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Log Exercise"));

        // Controls Panel (North)
        JPanel controls = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Exercise Type
        controls.add(new JLabel("Type:"), gbc(0, row, 1));
        exerciseTypeBox = new JComboBox<>(new String[]{"WeightLifting", "Cardio"});
        exerciseTypeBox.addActionListener(e -> toggleExerciseFields());
        controls.add(exerciseTypeBox, gbc(1, row++, 3));

        // Common Fields (Name, Duration)
        controls.add(new JLabel("Name:"), gbc(0, row, 1));
        nameField = new JTextField(15);
        controls.add(nameField, gbc(1, row++, 3));
        
        controls.add(new JLabel("Duration (min):"), gbc(0, row, 1));
        durationField = new JTextField("30", 15);
        controls.add(durationField, gbc(1, row++, 3));

        // Weight Lifting Fields (Hidden by default)
        controls.add(new JLabel("Reps:"), gbc(0, row, 1));
        repField = new JTextField("10", 5);
        controls.add(repField, gbc(1, row, 1));
        
        controls.add(new JLabel("Weight (kg):"), gbc(2, row, 1));
        weightField = new JTextField("50", 5);
        controls.add(weightField, gbc(3, row++, 1));

        // Cardio Fields (Hidden by default)
        controls.add(new JLabel("Distance (km):"), gbc(0, row, 1));
        distanceField = new JTextField("5.0", 5);
        controls.add(distanceField, gbc(1, row, 1));
        
        controls.add(new JLabel("Calories (est):"), gbc(2, row, 1));
        caloriesField = new JTextField("300", 5);
        controls.add(caloriesField, gbc(3, row++, 1));
        
        // Action Buttons
        JButton logExerciseButton = new JButton("Log Exercise");
        logExerciseButton.addActionListener(this::logExercise);
        controls.add(logExerciseButton, gbc(0, row, 2));

        JButton removeLastSetButton = new JButton("Remove Last Set");
        removeLastSetButton.addActionListener(this::removeLastSet);
        controls.add(removeLastSetButton, gbc(2, row++, 2));
        
        panel.add(controls, BorderLayout.NORTH);
        
        // Workout Title (South)
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.add(new JLabel("Workout Title:"));
        workoutTitleField = new JTextField(currentWorkout.getTitle(), 20);
        workoutTitleField.addActionListener(e -> updateWorkoutTitle());
        titlePanel.add(workoutTitleField);
        panel.add(titlePanel, BorderLayout.SOUTH);
        
        toggleExerciseFields(); // Initial state setup
        return panel;
    }
    
    // Helper for GridBagConstraints
    private GridBagConstraints gbc(int x, int inty, int w) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = x;
        gbc.gridy = inty;
        gbc.gridwidth = w;
        return gbc;
    }

    private void toggleExerciseFields() {
        boolean isWeightLifting = "WeightLifting".equals(exerciseTypeBox.getSelectedItem());
        
        repField.setEnabled(isWeightLifting);
        weightField.setEnabled(isWeightLifting);
        
        distanceField.setEnabled(!isWeightLifting);
        caloriesField.setEnabled(!isWeightLifting);
    }

    private JPanel createLogHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Current Workout Log"));

        workoutLogArea = new JTextArea(20, 30);
        workoutLogArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(workoutLogArea);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JButton showHistoryButton = new JButton("Show Full History");
        showHistoryButton.addActionListener(this::showHistory);
        panel.add(showHistoryButton, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void updateWorkoutTitle() {
        String newTitle = workoutTitleField.getText().trim();
        if (!newTitle.isEmpty()) {
            currentWorkout.setTitle(newTitle);
            currentWorkoutLabel.setText("Current Workout: " + newTitle);
        }
    }

    private void updateLogArea() {
        // Displays current workout log and a summary of all history
        String currentLog = currentWorkout.getSummaryReport();
        
        String historySummary = history.stream()
            .map(w -> String.format("%s - %s", 
                w.getDate().format(DateTimeFormatter.ofPattern("MMM dd")), 
                w.getTitle()))
            .collect(Collectors.joining("\n"));
            
        workoutLogArea.setText(currentLog + "\n\n--- Workout History ---\n" + historySummary);
    }
    
    // --- Action Listeners and Logic ---

    private void logExercise(ActionEvent e) {
        try {
            String name = nameField.getText().trim();
            int duration = Integer.parseInt(durationField.getText().trim());

            if (name.isEmpty() || duration <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a valid name and positive duration.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String type = (String) exerciseTypeBox.getSelectedItem();
            Exercise newExercise = null;
            String exerciseId = UUID.randomUUID().toString();

            if ("WeightLifting".equals(type)) {
                int reps = Integer.parseInt(repField.getText().trim());
                double weight = Double.parseDouble(weightField.getText().trim());

                if (reps <= 0 || weight <= 0) {
                    JOptionPane.showMessageDialog(this, "Reps and Weight must be positive.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Find existing exercise or create a new one to log sets continuously
                WeightLiftingExercise existing = (WeightLiftingExercise) currentWorkout.getExercises().stream()
                    .filter(ex -> ex instanceof WeightLiftingExercise && ex.getName().equalsIgnoreCase(name))
                    .findFirst().orElse(null);
                
                if (existing != null) {
                    existing.addSet(new Set(reps, weight));
                    // Update duration if necessary, but keep the existing exercise instance
                    existing.setDurationMinutes(duration);
                } else {
                    newExercise = new WeightLiftingExercise(name, duration, exerciseId);
                    ((WeightLiftingExercise) newExercise).addSet(new Set(reps, weight));
                    currentWorkout.addExercise(newExercise);
                }

            } else if ("Cardio".equals(type)) {
                double distance = Double.parseDouble(distanceField.getText().trim());
                int calories = Integer.parseInt(caloriesField.getText().trim());
                
                if (distance <= 0 || calories <= 0) {
                    JOptionPane.showMessageDialog(this, "Distance and Calories must be positive.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                newExercise = new CardioExercise(name, duration, exerciseId, distance, calories);
                currentWorkout.addExercise(newExercise);
            }
            
            updateLogArea();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number format in one of the fields. Please check your inputs.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void removeLastSet(ActionEvent e) {
        if (currentWorkout.getExercises().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Workout is empty. Nothing to remove.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Only allow removal from the last logged exercise, if it's WeightLifting
        Exercise lastExercise = currentWorkout.getExercises().get(currentWorkout.getExercises().size() - 1);
        
        if (lastExercise instanceof WeightLiftingExercise) {
            WeightLiftingExercise wle = (WeightLiftingExercise) lastExercise;
            wle.removeLastSet();
            
            // If all sets are removed, remove the exercise itself
            if (wle.getSets().isEmpty()) {
                currentWorkout.getExercises().remove(currentWorkout.getExercises().size() - 1);
                JOptionPane.showMessageDialog(this, "Last set removed. Exercise '" + wle.getName() + "' removed as it had no remaining sets.", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Last set of '" + wle.getName() + "' removed.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "The last logged item is not a Weight Lifting exercise. Cannot remove sets.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        updateLogArea();
    }

    private void saveWorkout(ActionEvent e) {
        if (currentWorkout.getExercises().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cannot save an empty workout.", "Save Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 1. Add current workout to history
        history.add(currentWorkout);
        
        // 2. Serialize/save history to file
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("workout_history.ser"))) {
            oos.writeObject(history);
            JOptionPane.showMessageDialog(this, "Workout saved and history file updated successfully!", "Save Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving history: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        
        // 3. Reset for new workout
        currentWorkout = new Workout("New Workout - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        currentWorkoutLabel.setText("Current Workout: " + currentWorkout.getTitle());
        workoutTitleField.setText(currentWorkout.getTitle());
        updateLogArea();
    }

    @SuppressWarnings("unchecked")
    private void loadHistory() {
        File file = new File("workout_history.ser");
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                history = (List<Workout>) ois.readObject();
                updateLogArea();
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Error loading history: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        } else {
            // History file does not exist, initialize empty history (already done by declaration)
        }
    }
    
    private void showHistory(ActionEvent e) {
        String fullHistory = "--- Full Workout History ---\n\n" + 
            history.stream()
                .map(Workout::getSummaryReport)
                .collect(Collectors.joining("\n\n-----------------------------------------\n"));
                
        JTextArea historyArea = new JTextArea(fullHistory);
        historyArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(historyArea);
        scrollPane.setPreferredSize(new Dimension(600, 700));

        JOptionPane.showMessageDialog(this, scrollPane, "Full History Log", JOptionPane.PLAIN_MESSAGE);
    }
    
    // --- User Setup and Main Method ---
    
    /**
     * Shows a modal dialog to get user details (Age, Height, Weight).
     * @return A valid User object or null if the user cancels.
     */
    private static user getUserInput(JFrame parent) {
        JTextField ageField = new JTextField(5);
        JTextField heightField = new JTextField(5);
        JTextField weightField = new JTextField(5);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Age (years):"));
        panel.add(ageField);
        panel.add(new JLabel("Height (cm):"));
        panel.add(heightField);
        panel.add(new JLabel("Weight (kg):"));
        panel.add(weightField);

        int result = JOptionPane.showConfirmDialog(parent, panel, "Enter Your Metrics", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int age = Integer.parseInt(ageField.getText().trim());
                double heightCm = Double.parseDouble(heightField.getText().trim());
                double weightKg = Double.parseDouble(weightField.getText().trim());
                String userId = UUID.randomUUID().toString();

                if (age > 0 && heightCm > 0 && weightKg > 0) {
                    return new user(age, heightCm, weightKg, userId);
                } else {
                    JOptionPane.showMessageDialog(parent, "All fields must contain positive values.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return getUserInput(parent); // Recurse on failure
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parent, "Invalid number format. Please enter numbers only.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return getUserInput(parent); // Recurse on failure
            }
        }
        return null; // User cancelled
    }
    
    private static void showRecommendation(user user) {
        WorkoutRecommender recommender = new WorkoutRecommender();
        String recommendationHtml = recommender.generateRecommendation(user);
        
        // Use a JLabel to display the HTML-formatted recommendation
        JLabel recommendationLabel = new JLabel(recommendationHtml);
        recommendationLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        JOptionPane.showMessageDialog(null, recommendationLabel, "Personalized Workout Recommendation", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        // Use the Swing event dispatch thread for GUI initialization
        SwingUtilities.invokeLater(() -> {
            JFrame setupFrame = new JFrame(); // Dummy frame for dialog parenting
            setupFrame.setVisible(false);
            
            user user = getUserInput(setupFrame);
            
            if (user != null) {
                showRecommendation(user);
                new GymTrackerGUI(user);
            } else {
                JOptionPane.showMessageDialog(setupFrame, "Application cancelled by user.", "Exit", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }
            setupFrame.dispose();
        });
    }
}