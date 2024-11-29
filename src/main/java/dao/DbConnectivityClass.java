package dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Person;
import service.MyLogger;

import java.sql.*;
import java.util.List;

public class DbConnectivityClass {

    private List<Person> users; // List to store Person objects

    // Initialize cnUtil as a static instance (singleton pattern)
    public static DbConnectivityClass cnUtil = new DbConnectivityClass();

    // Constants for DB connection
    final static String DB_NAME = "CSC311_BD_TEMP";
    final static String SQL_SERVER_URL = "jdbc:mysql://maradcsc11.mysql.database.azure.com";
    final static String DB_URL = "jdbc:mysql://maradcsc11.mysql.database.azure.com/" + DB_NAME;
    final static String USERNAME = "marac";
    final static String PASSWORD = "Forcsc311";

    private final ObservableList<Person> data = FXCollections.observableArrayList();

    // Method to retrieve all data from the database
    public ObservableList<Person> getData() {
        connectToDatabase();
        try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT * FROM users";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.isBeforeFirst()) {
                new MyLogger().makeLog("No data found.");
            }
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String first_name = resultSet.getString("first_name");
                String last_name = resultSet.getString("last_name");
                String department = resultSet.getString("department");
                String major = resultSet.getString("major");
                String email = resultSet.getString("email");
                String imageURL = resultSet.getString("imageURL");
                data.add(new Person(id, first_name, last_name, department, major, email, imageURL));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    // Method to check if there are any registered users in the database
    public boolean connectToDatabase() {
        boolean hasRegistredUsers = false;
        try (Connection conn = DriverManager.getConnection(SQL_SERVER_URL, USERNAME, PASSWORD)) {
            Statement statement = conn.createStatement();
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            statement.close();

            conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            statement = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT(10) NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                    "first_name VARCHAR(200) NOT NULL," +
                    "last_name VARCHAR(200) NOT NULL," +
                    "department VARCHAR(200)," +
                    "major VARCHAR(200)," +
                    "email VARCHAR(200) NOT NULL UNIQUE," +
                    "imageURL VARCHAR(200))";
            statement.executeUpdate(sql);

            statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM users");

            if (resultSet.next()) {
                int numUsers = resultSet.getInt(1);
                if (numUsers > 0) {
                    hasRegistredUsers = true;
                }
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hasRegistredUsers;
    }

    // Method to retrieve all users for CSV export
    public String stringAllUsers() {
        // Access the static cnUtil to get the list of users
        List<Person> users = cnUtil.getUsers();  // Corrected to getUsers()

        // StringBuilder for building CSV data
        StringBuilder csvData = new StringBuilder();

        // CSV Header
        csvData.append("id,firstName,lastName,department,major,email,imageURL\n");

        // Loop through each user and append their data as a CSV row
        for (Person user : users) {
            csvData.append(safe(user.getId())).append(",")
                    .append(safe(user.getFirstName())).append(",")
                    .append(safe(user.getLastName())).append(",")
                    .append(safe(user.getDepartment())).append(",")
                    .append(safe(user.getMajor())).append(",")
                    .append(safe(user.getEmail())).append(",")
                    .append(safe(user.getImageURL())).append("\n");
        }

        // Return the CSV data as a string
        return csvData.toString();
    }

    // Helper method to handle null or empty values
    private String safe(Object value) {
        return value == null ? "" : value.toString();  // Return empty string if value is null
    }

    // Getter for users (fixing method call typo)
    public List<Person> getUsers() {
        return users;
    }

    // Setter for users
    public void setUsers(List<Person> users) {
        this.users = users;
    }
}
