import java.time.LocalDateTime;
import java.util.*;

/**
 * Manages all operations related to people and sign-in/out records.
 * Acts as the main business logic layer of the application.
 */
public class RecordManager {
    private Map<String, Person> people;
    private List<SignInOutRecord> records;
    private Map<String, SignInOutRecord> activeSignIns;

    public RecordManager() {
        try {
            DataPersistence.initializeStorage();
            this.people = DataPersistence.loadPeople();
            this.records = DataPersistence.loadRecords();
            this.activeSignIns = new HashMap<>();

            // Reconstruct active sign-ins
            for (SignInOutRecord record : records) {
                if (record.getSignOutTime() == null) {
                    activeSignIns.put(record.getPersonId(), record);
                }
            }

            System.out.println("Loaded " + people.size() + " people and " + records.size() + " records.");
        } catch (Exception e) {
            System.err.println("Error initializing RecordManager: " + e.getMessage());
            this.people = new HashMap<>();
            this.records = new ArrayList<>();
            this.activeSignIns = new HashMap<>();
        }
    }

    public void addPerson(Person person) {
        if (people.containsKey(person.getId())) {
            throw new IllegalArgumentException("Person with ID " + person.getId() + " already exists");
        }
        people.put(person.getId(), person);
        DataPersistence.savePeople(people);
    }

    public void removePerson(String id) {
        if (activeSignIns.containsKey(id)) {
            throw new IllegalStateException("Cannot remove person who is currently signed in");
        }
        if (!people.containsKey(id)) {
            throw new IllegalArgumentException("Person with ID " + id + " not found");
        }
        people.remove(id);
        DataPersistence.savePeople(people);
    }

    public Person getPerson(String id) {
        return people.get(id);
    }

    public void signIn(String personId) {
        if (!people.containsKey(personId)) {
            throw new IllegalArgumentException("Person not found with ID: " + personId);
        }
        if (activeSignIns.containsKey(personId)) {
            throw new IllegalStateException("Person is already signed in");
        }

        SignInOutRecord record = new SignInOutRecord(personId);
        records.add(record);
        activeSignIns.put(personId, record);
        DataPersistence.saveRecords(records);
    }

    public void signOut(String personId) {
        SignInOutRecord record = activeSignIns.get(personId);
        if (record == null) {
            throw new IllegalStateException("Person is not signed in");
        }

        record.signOut();
        activeSignIns.remove(personId);
        DataPersistence.saveRecords(records);
    }

    public List<SignInOutRecord> getRecordsForDate(LocalDateTime date) {
        return records.stream()
                .filter(record -> record.isFromDate(date))
                .toList();
    }

    public List<SignInOutRecord> getAllRecords() {
        return new ArrayList<>(records);
    }
}