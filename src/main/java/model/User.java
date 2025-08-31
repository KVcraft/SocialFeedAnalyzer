package model;

public class User {
    private final String id;       // e.g., "U1"
    private final String username; // display name

    public User(String id, String username) {
        this.id = id;
        this.username = username;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }

    @Override
    public String toString() { return username + " (" + id + ")"; }
}
