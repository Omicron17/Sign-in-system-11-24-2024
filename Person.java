import java.io.Serializable;

/**
 * Represents a person in the sign-in/out system.
 * Implements Serializable to allow for storage in files.
 */
public class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String department;

    public Person(String id, String name, String department) {
        this.id = id;
        this.name = name;
        this.department = department;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }

    @Override
    public String toString() {
        return String.format("ID: %s, Name: %s, Department: %s", id, name, department);
    }
}