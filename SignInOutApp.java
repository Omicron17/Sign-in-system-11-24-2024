import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Main application class that handles user interaction.
 * Provides a command-line interface for the sign-in/out system.
 */
public class SignInOutApp {
    private static RecordManager recordManager;
    private static Scanner scanner;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        recordManager = new RecordManager();
        scanner = new Scanner(System.in);

        while (true) {
            try {
                printMenu();
                int choice = getValidIntInput("Enter your choice (1-6): ", 1, 6);

                switch (choice) {
                    case 1 -> addPerson();
                    case 2 -> removePerson();
                    case 3 -> signInPerson();
                    case 4 -> signOutPerson();
                    case 5 -> viewRecords();
                    case 6 -> {
                        System.out.println("Exiting...");
                        scanner.close();
                        return;
                    }
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n=== Sign In/Out System ===");
        System.out.println("1. Add Person");
        System.out.println("2. Remove Person");
        System.out.println("3. Sign In");
        System.out.println("4. Sign Out");
        System.out.println("5. View Records");
        System.out.println("6. Exit");
    }

    private static String getValidInput(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be empty");
        }
        return input;
    }

    private static int getValidIntInput(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Please enter a number between " + min + " and " + max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private static void addPerson() {
        try {
            String id = getValidInput("Enter ID: ");
            String name = getValidInput("Enter name: ");
            String department = getValidInput("Enter department: ");

            recordManager.addPerson(new Person(id, name, department));
            System.out.println("Person added successfully!");
        } catch (IllegalArgumentException e) {
            System.out.println("Error adding person: " + e.getMessage());
        }
    }

    private static void removePerson() {
        try {
            String id = getValidInput("Enter ID to remove: ");
            recordManager.removePerson(id);
            System.out.println("Person removed successfully!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error removing person: " + e.getMessage());
        }
    }

    private static void signInPerson() {
        try {
            String id = getValidInput("Enter ID to sign in: ");
            recordManager.signIn(id);
            System.out.println("Signed in successfully!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error signing in: " + e.getMessage());
        }
    }

    private static void signOutPerson() {
        try {
            String id = getValidInput("Enter ID to sign out: ");
            recordManager.signOut(id);
            System.out.println("Signed out successfully!");
        } catch (IllegalStateException e) {
            System.out.println("Error signing out: " + e.getMessage());
        }
    }

    private static void viewRecords() {
        while (true) {
            try {
                System.out.print("Enter date (YYYY-MM-DD) or 'all' for all records: ");
                String dateInput = scanner.nextLine().trim();

                List<SignInOutRecord> records;
                if (dateInput.equalsIgnoreCase("all")) {
                    records = recordManager.getAllRecords();
                } else {
                    LocalDateTime date = LocalDateTime.parse(dateInput + " 00:00",
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    records = recordManager.getRecordsForDate(date);
                }

                displayRecords(records);
                return;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD or 'all'.");
            }
        }
    }

    private static void displayRecords(List<SignInOutRecord> records) {
        if (records.isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        System.out.println("\n=== Records ===");
        for (SignInOutRecord record : records) {
            Person person = recordManager.getPerson(record.getPersonId());
            if (person != null) {
                System.out.printf("%s (%s, %s): %s%n",
                        person.getName(),
                        person.getId(),
                        person.getDepartment(),
                        record);
            }
        }
    }
}