import GymTrackerProject.src.models.User; // FIX: Import the User model class
import GymTrackerProject.src.service.WorkoutRecommender; // FIX: Import the WorkoutRecommender service class
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.UUID;

/**
 * Main application class. Handles user input, creates the User object,
 * and calls the WorkoutRecommender service.
 */
public class GymTrackerApp {

    /**
     * Helper method to get a validated integer input from the console.
     */
    private static int getValidIntInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                if (scanner.hasNextInt()) {
                    int input = scanner.nextInt();
                    if (input > 0) {
                        return input;
                    } else {
                        System.out.println("Input must be a positive number.");
                    }
                } else {
                    System.out.println("Invalid input. Please enter a whole number.");
                    scanner.next(); // Consume the invalid token
                }
                System.out.print(prompt); // Re-prompt
            } catch (Exception e) {
                System.out.println("An error occurred during input: " + e.getMessage());
                scanner.nextLine(); // Clear the buffer
                System.out.print(prompt); // Re-prompt
            }
        }
    }

    /**
     * Helper method to get a validated double input from the console.
     */
    private static double getValidDoubleInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                if (scanner.hasNextDouble()) {
                    double input = scanner.nextDouble();
                    if (input > 0.0) {
                        return input;
                    } else {
                        System.out.println("Input must be a positive number.");
                    }
                } else {
                    System.out.println("Invalid input. Please enter a number (e.g., 70.5).");
                    scanner.next(); // Consume the invalid token
                }
                System.out.print(prompt); // Re-prompt
            } catch (Exception e) {
                System.out.println("An error occurred during input: " + e.getMessage());
                scanner.nextLine(); // Clear the buffer
                System.out.print(prompt); // Re-prompt
            }
        }
    }
    
    public static void main(String[] args) {
        System.out.println("\n--- Workout Recommendation System ---");
        
        // Initialize Scanner for user input
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Welcome! Please enter your details for a personalized workout plan.");

        // --- 1. Get Validated User Input ---
        int age = getValidIntInput(scanner, "Enter your Age (years): ");
        double heightCm = getValidDoubleInput(scanner, "Enter your Height (cm, e.g., 175): ");
        double weightKg = getValidDoubleInput(scanner, "Enter your Weight (kg, e.g., 70.5): ");
        
        // Consume the final newline character after the double input
        scanner.nextLine(); 

        // --- 2. Create User Model ---
        // FIX: The constructor requires 4 arguments (age, heightCm, weightKg, userId)
        User currentUser = new User(age, heightCm, weightKg, UUID.randomUUID().toString()); 
        System.out.println("\nUser Profile Created: " + currentUser.toString());

        // --- 3. Generate and Display Recommendation (Service Call) ---
        // FIX: WorkoutRecommender class is now resolved
        WorkoutRecommender recommender = new WorkoutRecommender();
        String recommendation = recommender.generateRecommendation(currentUser);
        
        System.out.println("\n--- Personalized Recommendation ---");
        System.out.println(recommendation);
        System.out.println("-----------------------------------");
        
        // Close the scanner
        scanner.close(); 
    }
}