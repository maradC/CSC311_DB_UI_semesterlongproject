package viewmodel;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.prefs.Preferences;

public class SignUpController {
    @FXML
    public TextField usernameField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public Button newAccountBtn;
    @FXML
    public TextField emailField;
    @FXML
    public DatePicker dobField;
    @FXML
    public TextField ramIdField;

    // Constructor if needed
    public SignUpController() {
    }

    public void createNewAccount(ActionEvent actionEvent) {
        // Get input values
        String username = usernameField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();
        String dob = dobField.getValue() != null ? dobField.getValue().toString() : "";
        String ramId = ramIdField.getText();

        // Check if all fields are filled out
        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || dob.isEmpty() || ramId.isEmpty()) {
            showAlert("Error", "All fields must be filled out!");
            return;
        }

        // Save user data in Preferences (example; do not store passwords in plain text for production)
        Preferences prefs = Preferences.userNodeForPackage(SignUpController.class);
        prefs.put("username", username);
        prefs.put("password", password); // NOTE: You should hash and salt the password in production
        prefs.put("email", email);
        prefs.put("dob", dob);
        prefs.put("ramId", ramId);

        // Show success message
        showAlert("Success", "Account created successfully!");

        // Optionally clear the fields
        usernameField.clear();
        passwordField.clear();
        emailField.clear();
        dobField.setValue(null);
        ramIdField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }


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


