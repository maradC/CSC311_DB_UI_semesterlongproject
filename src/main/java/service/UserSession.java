package service;

import java.util.prefs.Preferences;

public class UserSession {

    // Volatile ensures visibility and prevents instruction reordering
    private static volatile UserSession instance;

    private String userName;
    private String password;
    private String privileges;

    private final Preferences userPreferences = Preferences.userRoot(); // Initialize directly

    // Private constructor to prevent direct instantiation
    private UserSession(String userName, String password, String privileges) {
        this.userName = userName;
        this.password = password;
        this.privileges = privileges;

        // Store user session data in preferences
        userPreferences.put("USERNAME", userName);
        userPreferences.put("PASSWORD", password);
        userPreferences.put("PRIVILEGES", privileges);
    }

    // Thread-safe Singleton initialization using Double-Checked Locking
    public static UserSession getInstance(String userName, String password, String privileges) {
        if (instance == null) {
            synchronized (UserSession.class) {
                if (instance == null) {
                    instance = new UserSession(userName, password, privileges);
                }
            }
        }
        return instance;
    }

    // Overloaded method to create session with default privileges
    public static UserSession getInstance(String userName, String password) {
        return getInstance(userName, password, "NONE");
    }

    // Thread-safe method to clean the user session
    public synchronized void cleanUserSession() {
        this.userName = "";
        this.password = "";
        this.privileges = "";

        userPreferences.remove("USERNAME");
        userPreferences.remove("PASSWORD");
        userPreferences.remove("PRIVILEGES");
    }

    // Thread-safe getters
    public synchronized String getUserName() {
        return this.userName;
    }

    public synchronized String getPassword() {
        return this.password;
    }

    public synchronized String getPrivileges() {
        return this.privileges;
    }

    // Check if a user is already signed in (optional utility method)
    public boolean isUserSignedIn() {
        return userPreferences.get("USERNAME", null) != null;
    }

    // Thread-safe toString method
    @Override
    public synchronized String toString() {
        return "UserSession{" +
                "userName='" + this.userName + '\'' +
                ", privileges='" + this.privileges + '\'' +
                '}';
    }

    // Static method to load session from preferences
    public static UserSession loadSessionFromPreferences() {
        Preferences userPreferences = Preferences.userRoot();
        String userName = userPreferences.get("USERNAME", "");
        String password = userPreferences.get("PASSWORD", "");
        String privileges = userPreferences.get("PRIVILEGES", "NONE");

        if (userName.isEmpty() || password.isEmpty()) {
            return null;  // No user data saved
        }
        return new UserSession(userName, password, privileges);
    }
}
