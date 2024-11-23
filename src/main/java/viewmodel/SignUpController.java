package viewmodel;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.prefs.Preferences;
import java.util.regex.Pattern;

public class SignUpController {
    @FXML
    public TextField usernameField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public DatePicker dobField;
    @FXML
    public TextField ramIdField;

    // Regex patterns for validation
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@farmingdale\\.edu$");  // For username like email
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[A-Za-z0-9]{6,25}$"); // Password should be 6-25 characters

    public SignUpController() {
    }

    // Initialize the mouse click events for fields
    public void initialize() {
        // Add mouse click event handlers for fields
        usernameField.setOnMouseClicked(this::onFieldSelected);
        passwordField.setOnMouseClicked(this::onFieldSelected);
        dobField.setOnMouseClicked(this::onFieldSelected);
        ramIdField.setOnMouseClicked(this::onFieldSelected);
    }

    // Event handler for mouse click on any field
    private void onFieldSelected(MouseEvent event) {
        // Determine which field is selected and apply the appropriate validation logic
        if (event.getSource() == usernameField) {
            validateUsername();
        } else if (event.getSource() == passwordField) {
            validatePassword();
        } else if (event.getSource() == dobField) {
            validateDob();
        } else if (event.getSource() == ramIdField) {
            validateRamId();
        }
    }

    // Validate username field using regex pattern
    private void validateUsername() {
        String username = usernameField.getText();
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            showAlert("Invalid Username", "Username must be in the format: user@farmingdale.edu");
        }
    }

    // Validate password field
    private void validatePassword() {
        String password = passwordField.getText();
        if (password.isEmpty() || !PASSWORD_PATTERN.matcher(password).matches()) {
            showAlert("Invalid Password", "Password must be between 6 and 25 characters.");
        }
    }

    // Validate date of birth field
    private void validateDob() {
        if (dobField.getValue() == null) {
            showAlert("Invalid DOB", "Please select a valid date of birth.");
        }
    }

    // Validate RAM ID field
    private void validateRamId() {
        String ramId = ramIdField.getText();
        if (ramId.isEmpty()) {
            showAlert("Invalid RAM ID", "RAM ID cannot be empty.");
        }
    }

    // Handler for the create new account button click
    public void createNewAccount(ActionEvent actionEvent) {
        // Get input values
        String username = usernameField.getText();
        String password = passwordField.getText();
        String dob = dobField.getValue() != null ? dobField.getValue().toString() : "";
        String ramId = ramIdField.getText();

        // Validate all fields before proceeding
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            showAlert("Invalid Username", "Username must be in the format: user@farmingdale.edu");
            return;
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            showAlert("Invalid Password", "Password must be between 6 and 25 characters.");
            return;
        }
        if (dob.isEmpty()) {
            showAlert("Invalid DOB", "DOB must be selected.");
            return;
        }
        if (ramId.isEmpty()) {
            showAlert("Invalid RAM ID", "RAM ID cannot be empty.");
            return;
        }

        // Save user data in Preferences (example; do not store passwords in plain text for production)
        Preferences prefs = Preferences.userNodeForPackage(SignUpController.class);
        prefs.put("username", username);
        prefs.put("password", password); // NOTE: You should hash and salt the password in production
        prefs.put("dob", dob);
        prefs.put("ramId", ramId);

        // Show success message
        showAlert("Success", "Account created successfully!");

        // Optionally clear the fields
        usernameField.clear();
        passwordField.clear();
        dobField.setValue(null);
        ramIdField.clear();
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
