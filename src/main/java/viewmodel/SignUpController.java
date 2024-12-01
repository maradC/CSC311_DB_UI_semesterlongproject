package viewmodel;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import service.UserSession;  // Import UserSession class

import java.util.prefs.Preferences;
import java.util.regex.Pattern;

public class SignUpController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField dobField;  // TextField for DOB input (or use DatePicker if you prefer)
    @FXML
    private TextField ramIdField;

    // Regex patterns for validation
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@farmingdale\\.edu$");  // Username like email
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[A-Za-z0-9]{6,25}$"); // Password should be 6-25 characters
    private static final Pattern DOB_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");  // Date format: YYYY-MM-DD
    private static final Pattern RAM_ID_PATTERN = Pattern.compile("^R\\d{8,10}$");  // RAM ID: Starts with 'R' followed by 8-10 digits

    // Initialize the mouse click events for fields
    public void initialize() {
        Platform.runLater(() -> usernameField.requestFocus());

        usernameField.setOnMouseClicked(this::onFieldSelected);
        passwordField.setOnMouseClicked(this::onFieldSelected);
        dobField.setOnMouseClicked(this::onFieldSelected);
        ramIdField.setOnMouseClicked(this::onFieldSelected);
    }

    // Event handler for mouse click on any field
    @FXML
    private void onFieldSelected(MouseEvent event) {
        System.out.println("Field clicked: " + event.getSource());
        if (event.getSource() == usernameField) {
            validateUsername(usernameField.getText());
        } else if (event.getSource() == passwordField) {
            validatePassword(passwordField.getText());
        } else if (event.getSource() == dobField) {
            validateDob(dobField.getText().trim());
        } else if (event.getSource() == ramIdField) {
            validateRamId(ramIdField.getText().trim());
        }
    }

    // Validate username field using regex pattern
    private boolean validateUsername(String username) {
        if (username.isEmpty()) {
            showAlert("Invalid Username", "Username cannot be empty.");
            return false;
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            showAlert("Invalid Username", "Username must be in the format: user@farmingdale.edu");
            return false;
        }
        return true;
    }

    // Validate password field
    private boolean validatePassword(String password) {
        if (password.isEmpty() || !PASSWORD_PATTERN.matcher(password).matches()) {
            showAlert("Invalid Password", "Password must be between 6 and 25 characters.");
            return false;
        }
        return true;
    }

    // Validate date of birth field (TextField now)
    private boolean validateDob(String dob) {
        if (dob.isEmpty() || !DOB_PATTERN.matcher(dob).matches()) {
            showAlert("Invalid DOB", "Date of birth must be in the format YYYY-MM-DD.");
            return false;
        }
        return true;
    }

    // Validate RAM ID field
    private boolean validateRamId(String ramId) {
        if (ramId.isEmpty()) {
            showAlert("Invalid RAM ID", "RAM ID cannot be empty.");
            return false;
        }
        if (!RAM_ID_PATTERN.matcher(ramId).matches()) {
            showAlert("Invalid RAM ID", "RAM ID must start with 'R' followed by 8 to 10 digits (e.g., R02057684).");
            return false;
        }
        return true;
    }

    // Handler for the create new account button click
    public void createNewAccount(ActionEvent actionEvent) {
        // Get input values
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String dob = dobField.getText().trim();  // Get DOB from TextField
        String ramId = ramIdField.getText().trim();

        // Validate all fields before proceeding
        if (!validateUsername(username)) return;
        if (!validatePassword(password)) return;
        if (!validateDob(dob)) return;
        if (!validateRamId(ramId)) return;

        // Save user data in Preferences (do not store passwords in plain text for production)
        Preferences prefs = Preferences.userNodeForPackage(SignUpController.class);
        prefs.put("username", username);
        prefs.put("password", password); // NOTE: Hash and salt the password in production
        prefs.put("dob", dob);
        prefs.put("ramId", ramId);

        // Create a new UserSession instance and save session details
        try {
            UserSession session = UserSession.getInstance(username, password, "USER");

            // Show success message
            showAlert("Success", "Account created successfully!");

            // Optionally clear the fields
            usernameField.clear();
            passwordField.clear();
            dobField.clear();
            ramIdField.clear();

            // Redirect to login page or dashboard (if needed)
            goBack(actionEvent);

        } catch (Exception e) {
            // Handle any errors
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Error creating account: " + e.getMessage());
            alert.showAndWait();
        }
    }

    // Helper method to show alert messages
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Go back to the login page
    public void goBack(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
