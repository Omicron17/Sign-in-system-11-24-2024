import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single sign-in/out record.
 * Handles serialization of LocalDateTime objects.
 */
public class SignInOutRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private String personId;
    private long signInTimestamp;
    private Long signOutTimestamp; // Nullable for when person hasn't signed out

    private transient LocalDateTime signInTime;
    private transient LocalDateTime signOutTime;

    public SignInOutRecord(String personId) {
        this.personId = personId;
        this.signInTime = LocalDateTime.now();
        this.signInTimestamp = signInTime.toEpochSecond(java.time.ZoneOffset.UTC);
    }

    public void signOut() {
        this.signOutTime = LocalDateTime.now();
        this.signOutTimestamp = signOutTime.toEpochSecond(java.time.ZoneOffset.UTC);
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.signInTime = LocalDateTime.ofEpochSecond(signInTimestamp, 0, java.time.ZoneOffset.UTC);
        if (signOutTimestamp != null) {
            this.signOutTime = LocalDateTime.ofEpochSecond(signOutTimestamp, 0, java.time.ZoneOffset.UTC);
        }
    }

    // Getters
    public String getPersonId() { return personId; }
    public LocalDateTime getSignInTime() { return signInTime; }
    public LocalDateTime getSignOutTime() { return signOutTime; }

    public boolean isFromDate(LocalDateTime date) {
        return signInTime.toLocalDate().equals(date.toLocalDate());
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String signInStr = signInTime.format(formatter);
        String signOutStr = signOutTime != null ? signOutTime.format(formatter) : "Not signed out";
        return String.format("Sign in: %s, Sign out: %s", signInStr, signOutStr);
    }
}