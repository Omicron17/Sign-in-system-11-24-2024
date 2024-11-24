import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Handles all data persistence operations for the application.
 * Uses atomic file operations to prevent data corruption.
 */
public class DataPersistence {
    private static final String DATA_DIR = System.getProperty("user.home") + File.separator + "signInOutData";
    private static final String PEOPLE_FILE = DATA_DIR + File.separator + "people.dat";
    private static final String RECORDS_FILE = DATA_DIR + File.separator + "records.dat";

    public static void initializeStorage() throws IOException {
        Files.createDirectories(Paths.get(DATA_DIR));

        // Create files if they don't exist
        try {
            Files.createFile(Paths.get(PEOPLE_FILE));
            Files.createFile(Paths.get(RECORDS_FILE));
        } catch (FileAlreadyExistsException e) {
            // Files already exist, this is fine
        }
    }

    public static void savePeople(Map<String, Person> people) {
        Path tempPath = Paths.get(PEOPLE_FILE + ".tmp");
        Path finalPath = Paths.get(PEOPLE_FILE);

        try {
            // Write to temporary file first
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new BufferedOutputStream(Files.newOutputStream(tempPath)))) {
                oos.writeObject(new ArrayList<>(people.values()));
            }

            // Atomic move
            Files.move(tempPath, finalPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save people data: " + e.getMessage(), e);
        }
    }

    public static void saveRecords(List<SignInOutRecord> records) {
        Path tempPath = Paths.get(RECORDS_FILE + ".tmp");
        Path finalPath = Paths.get(RECORDS_FILE);

        try {
            // Write to temporary file first
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new BufferedOutputStream(Files.newOutputStream(tempPath)))) {
                oos.writeObject(new ArrayList<>(records));
            }

            // Atomic move
            Files.move(tempPath, finalPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save records data: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Person> loadPeople() {
        Map<String, Person> people = new HashMap<>();
        Path path = Paths.get(PEOPLE_FILE);

        try {
            if (Files.exists(path)) {
                long fileSize = Files.size(path);
                if (fileSize > 0) {
                    try (ObjectInputStream ois = new ObjectInputStream(
                            new BufferedInputStream(Files.newInputStream(path)))) {
                        List<Person> personList = (List<Person>) ois.readObject();
                        for (Person person : personList) {
                            people.put(person.getId(), person);
                        }
                    }
                }
            }
        } catch (EOFException e) {
            // Empty file, return empty map
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to load people data: " + e.getMessage(), e);
        }
        return people;
    }

    @SuppressWarnings("unchecked")
    public static List<SignInOutRecord> loadRecords() {
        List<SignInOutRecord> records = new ArrayList<>();
        Path path = Paths.get(RECORDS_FILE);

        try {
            if (Files.exists(path)) {
                long fileSize = Files.size(path);
                if (fileSize > 0) {
                    try (ObjectInputStream ois = new ObjectInputStream(
                            new BufferedInputStream(Files.newInputStream(path)))) {
                        records = (List<SignInOutRecord>) ois.readObject();
                    }
                }
            }
        } catch (EOFException e) {
            // Empty file, return empty list
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to load records data: " + e.getMessage(), e);
        }
        return records;
    }
}