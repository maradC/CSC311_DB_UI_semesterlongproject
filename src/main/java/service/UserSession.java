package service;

import java.util.prefs.Preferences;

public class UserSession {

    private static volatile UserSession instance;
    private String userName;
    private String password;
    private String privileges;

    // Private constructor to initialize session details
    private UserSession(String userName, String password, String privileges) {
        this.userName = userName;
        this.password = password;
        this.privileges = privileges;
        Preferences userPreferences = Preferences.userRoot();
        // Store user credentials and privileges in preferences (not secure, use hashing for password in production)
        userPreferences.put("USERNAME", userName);
        userPreferences.put("PASSWORD", password);
        userPreferences.put("PRIVILEGES", privileges);
    }

    // Singleton pattern: Create or return the existing instance
    public static UserSession getInstance(String userName, String password, String privileges) {
        if (instance == null) {  // First check (no synchronization needed)
            synchronized (UserSession.class) {
                if (instance == null) {  // Second check (synchronized block)
                    instance = new UserSession(userName, password, privileges);
                }
            }
        }
        return instance;
    }

    // Overloaded method: default privileges as "NONE"
    public static UserSession getInstance(String userName, String password) {
        return getInstance(userName, password, "NONE");
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

    // Getter methods
    public String getUserName() {
        return this.userName;
    }

    public String getPassword() {
        return this.password;
    }

    public String getPrivileges() {
        return this.privileges;
    }

    // Method to clear user session data from preferences
    public static void clearUserSession() {
        Preferences userPreferences = Preferences.userRoot();
        userPreferences.remove("USERNAME");
        userPreferences.remove("PASSWORD");
        userPreferences.remove("PRIVILEGES");
    }

    @Override
    public String toString() {
        return "UserSession{" +
                "userName='" + this.userName + '\'' +
                ", privileges='" + this.privileges + '\'' +
                '}';
    }
}
