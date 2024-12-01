package viewmodel;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import com.azure.storage.blob.BlobClient;
import dao.DbConnectivityClass;
import dao.StorageUploader;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Person;
import service.MyLogger;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class DB_GUI_Controller implements Initializable {

    // Add regex patterns for validation
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z'-]{2,50}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern DEPARTMENT_PATTERN = Pattern.compile("^[A-Za-z ]{2,50}$");

    // Debounce delay for form validation (300ms)
    private PauseTransition debounce = new PauseTransition(Duration.millis(300));

    // The StorageUploader instance for blob storage operations
    StorageUploader store = new StorageUploader();

    @FXML
    private Label statusLabel;

    @FXML
    private Button editBtn;
    @FXML
    private Button delBtn;
    @FXML
    private Button addBtn;
    @FXML
    TextField statusText;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private TextField first_name, last_name, department, major,email, imageURL;

    @FXML
    private Label statusBar;

    @FXML
    private ImageView img_view;
    @FXML
    private MenuBar menuBar;
    @FXML
    private TableView<Person> tv;
    @FXML
    private TableColumn<Person, Integer> tv_id;
    @FXML
    private TableColumn<Person, String> tv_fn, tv_ln, tv_department, tv_major, tv_email;

    private final DbConnectivityClass cnUtil = new DbConnectivityClass();
    private final ObservableList<Person> data = cnUtil.getData();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            tv_id.setCellValueFactory(new PropertyValueFactory<>("id"));
            tv_fn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            tv_ln.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            tv_department.setCellValueFactory(new PropertyValueFactory<>("department"));
            tv_major.setCellValueFactory(new PropertyValueFactory<>("major"));
            tv_email.setCellValueFactory(new PropertyValueFactory<>("email"));
            tv.setItems(data);

            // Initially disable buttons
            editBtn.setDisable(true);
            delBtn.setDisable(true);
            addBtn.setDisable(true);

            // Add listeners to the TableView for selection change
            tv.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                updateUIState();
            });

            // Add listeners to form fields for validation with debounce
            setupValidationListeners();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Set up listeners with debounce logic
    private void setupValidationListeners() {
        first_name.textProperty().addListener((observable, oldValue, newValue) -> setupValidationDelay());
        last_name.textProperty().addListener((observable, oldValue, newValue) -> setupValidationDelay());
        email.textProperty().addListener((observable, oldValue, newValue) -> setupValidationDelay());
        department.textProperty().addListener((observable, oldValue, newValue) -> setupValidationDelay());
        major.textProperty().addListener((observable, oldValue, newValue) -> setupValidationDelay());
    }

    // Reset and start the debounce timer each time the user types in a field
    private void setupValidationDelay() {
        debounce.setOnFinished(e -> validateForm()); // Trigger validation after the delay
        debounce.playFromStart(); // Reset and start the timer
    }

    private void updateUIState() {
        Person selectedPerson = tv.getSelectionModel().getSelectedItem();
        boolean recordSelected = selectedPerson != null;
        editBtn.setDisable(!recordSelected);
        delBtn.setDisable(!recordSelected);
    }

    private void validateForm() {
        boolean formValid = validateTextField(first_name, NAME_PATTERN, "Invalid first name") &&
                validateTextField(last_name, NAME_PATTERN, "Invalid last name") &&
                validateTextField(email, EMAIL_PATTERN, "Invalid email format") &&
                validateTextField(department, DEPARTMENT_PATTERN, "Invalid department") &&
                !major.getText().isEmpty();

        // Enable the Add button if form is valid
        addBtn.setDisable(!formValid);
    }

    private boolean validateTextField(TextField textField, Pattern pattern, String errorMessage) {
        String text = textField.getText().trim();
        boolean isValid = pattern.matcher(text).matches();

        if (!isValid) {
            textField.setStyle("-fx-border-color: red;");
            showValidationError(errorMessage);

            // Update the status bar with the error message
            updateStatusMessage(errorMessage, "red");
        } else {
            textField.setStyle("");
        }

        return isValid;
    }


    private void showValidationError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void updateStatusMessage(String message, String color) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-font-size: 25px; -fx-text-fill: " + color + ";");

        // Optionally, clear the message after a few seconds
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> statusLabel.setText(""));
        pause.play();
    }

    // Add New Record Action
    @FXML
    protected void addNewRecord() {
        Person p = new Person(first_name.getText(), last_name.getText(), department.getText(),
                major.getText(), email.getText(), imageURL.getText());
        cnUtil.insertUser(p);
        cnUtil.retrieveId(p);
        p.setId(cnUtil.retrieveId(p));
        data.add(p);
        clearForm();
        // Set the status message to inform the user about the success
        updateStatusMessage("Record added successfully!", "green");

    }

    // Clear Form Action
    @FXML
    protected void clearForm() {
        first_name.setText("");
        last_name.setText("");
        department.setText("");
        major.setText("");
        email.setText("");
        imageURL.setText("");
    }

    @FXML
    protected void logOut(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").getFile());
            Stage window = (Stage) menuBar.getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void closeApplication() {
        System.exit(0);
    }

    @FXML
    protected void displayAbout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/about.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root, 600, 500);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void editRecord() {
        Person p = tv.getSelectionModel().getSelectedItem();
        int index = data.indexOf(p);
        Person p2 = new Person(index + 1, first_name.getText(), last_name.getText(), department.getText(),
                major.getText(), email.getText(), imageURL.getText());
        cnUtil.editUser(p.getId(), p2);
        data.remove(p);
        data.add(index, p2);
        tv.getSelectionModel().select(index);
        updateStatusMessage("Record updated successfully!", "green");


    }

    @FXML
    protected void deleteRecord() {
        Person p = tv.getSelectionModel().getSelectedItem();
        int index = data.indexOf(p);
        cnUtil.deleteRecord(p);
        data.remove(index);
        tv.getSelectionModel().select(index);
        // Inform the user about the deletion
        updateStatusMessage("Record deleted successfully.", "green");
    }

    @FXML
    protected void showImage() {
        File file = (new FileChooser()).showOpenDialog(img_view.getScene().getWindow());
        if (file != null) {
            img_view.setImage(new Image(file.toURI().toString()));

            Task<Void> uploadTask = createUploadTask(file, progressBar);
            progressBar.progressProperty().bind(uploadTask.progressProperty());

            // Add success and failure handlers for the upload task
            uploadTask.setOnSucceeded(e -> {
                updateStatusMessage("Upload completed successfully!", "green");
            });

            uploadTask.setOnFailed(e -> {
                updateStatusMessage("Upload failed: " + uploadTask.getException().getMessage(), "red");
            });

            new Thread(uploadTask).start();
        }
    }


    @FXML
    protected void addRecord() {
        showSomeone();
    }

    @FXML
    protected void selectedItemTV(MouseEvent mouseEvent) {
        Person p = tv.getSelectionModel().getSelectedItem();
        first_name.setText(p.getFirstName());
        last_name.setText(p.getLastName());
        department.setText(p.getDepartment());
        major.setText(p.getMajor());
        email.setText(p.getEmail());
        imageURL.setText(p.getImageURL());
    }

    public void lightTheme(ActionEvent actionEvent) {
        try {
            Scene scene = menuBar.getScene();
            Stage stage = (Stage) scene.getWindow();
            stage.getScene().getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void darkTheme(ActionEvent actionEvent) {
        try {
            Scene scene = menuBar.getScene();
            Stage stage = (Stage) scene.getWindow();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/darkTheme.css").toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSomeone() {
        Dialog<Results> dialog = new Dialog<>();
        dialog.setTitle("New User");
        dialog.setHeaderText("Please specify…");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField textField1 = new TextField("Name");
        TextField textField2 = new TextField("Last Name");
        TextField textField3 = new TextField("Email ");
        ObservableList<Major> options =
                FXCollections.observableArrayList(Major.values());
        ComboBox<Major> comboBox = new ComboBox<>(options);
        comboBox.getSelectionModel().selectFirst();
        dialogPane.setContent(new VBox(8, textField1, textField2,textField3, comboBox));
        Platform.runLater(textField1::requestFocus);
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new Results(textField1.getText(),
                        textField2.getText(), comboBox.getValue());
            }
            return null;
        });
        Optional<Results> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((Results results) -> {
            MyLogger.makeLog(
                    results.fname + " " + results.lname + " " + results.major);
        });
    }

    @FXML
    public void generateReport(ActionEvent actionEvent) {
        // Count the number of students by major
        Map<String, Long> majorCounts = data.stream()
                .collect(Collectors.groupingBy(Person::getMajor, Collectors.counting()));

        // Generate the PDF
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            // Create a content stream
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Write to the PDF
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
            contentStream.beginText();
            contentStream.setLeading(14.5f);
            contentStream.newLineAtOffset(50, 750);

            contentStream.showText("Report: Number of Students by Major");
            contentStream.newLine();
            contentStream.newLine();

            // Add the data
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            for (Map.Entry<String, Long> entry : majorCounts.entrySet()) {
                contentStream.showText(entry.getKey() + ": " + entry.getValue() + " students");
                contentStream.newLine();
            }

            contentStream.endText();
            contentStream.close();

            // Save the PDF to a file
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save PDF Report");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File file = fileChooser.showSaveDialog(menuBar.getScene().getWindow());

            if (file != null) {
                document.save(file);
                statusBar.setText("Report saved successfully to: " + file.getAbsolutePath());
            }

        } catch (IOException e) {
            statusBar.setText("Error generating the report.");
            e.printStackTrace();
        }
    }

    private static enum Major {Business, CSC, CPIS}

    private static class Results {

        String fname;
        String lname;
        Major major;

        public Results(String name, String date, Major venue) {
            this.fname = name;
            this.lname = date;
            this.major = venue;
        }
    }
    @FXML
    protected void importCsv() throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(menuBar.getScene().getWindow());

        if (file != null) {
            try {
                ObservableList<Person> importedData = FXCollections.observableArrayList();
                List<String> lines = Files.readAllLines(file.toPath());

                for (String line : lines) {
                    String[] values = line.split(","); // Assuming CSV fields are comma-separated

                    // Validate each row in the CSV file
                    if (values.length != 6 || Arrays.stream(values).anyMatch(String::isBlank)) {
                        throw new IllegalArgumentException("Invalid CSV format in row: " + line);
                    }

                    // Create a Person object from valid data
                    Person person = new Person(values[0], values[1], values[2], values[3], values[4], values[5]);
                    importedData.add(person);
                }

                // Add imported data to table and database
                for (Person person : importedData) {
                    cnUtil.insertUser(person);
                    cnUtil.retrieveId(person);
                    person.setId(cnUtil.retrieveId(person));
                }
                data.addAll(importedData);
                tv.setItems(data);

                statusBar.setText("CSV file imported successfully.");
            } catch (IllegalArgumentException e) {
                statusBar.setText("Error importing CSV: " + e.getMessage());
            } catch (Exception e) {
                statusBar.setText("Error importing CSV file.");
                e.printStackTrace();
            }
        }
    }
    @FXML
    protected void exportCsv(ActionEvent actionEvent) {
        // Open a FileChooser to select the save location
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setTitle("Save CSV File");
        File file = fileChooser.showSaveDialog(menuBar.getScene().getWindow());

        if (file != null) {
            try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
                // Write the CSV header (column names)
                writer.write("First Name,Last Name,Department,Major,Email,Image URL\n");

                // Iterate over the data list (ObservableList<Person>) and write each person's data
                for (Person person : data) {
                    writer.write(String.join(",",
                            person.getFirstName(),
                            person.getLastName(),
                            person.getDepartment(),
                            person.getMajor(),
                            person.getEmail(),
                            person.getImageURL())
                    );
                    writer.newLine();
                }

                // Update status message
                updateStatusMessage("CSV file exported successfully.", "green");

            } catch (IOException e) {
                // Handle any errors during file writing
                updateStatusMessage("Error exporting CSV file.", "red");
                e.printStackTrace();
            }
        }
    }


    private Task<Void> createUploadTask(File file, ProgressBar progressBar) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                BlobClient blobClient = store.getContainerClient().getBlobClient(file.getName());
                long fileSize = Files.size(file.toPath());
                long uploadedBytes = 0;

                try (FileInputStream fileInputStream = new FileInputStream(file);
                     OutputStream blobOutputStream = blobClient.getBlockBlobClient().getBlobOutputStream()) {

                    byte[] buffer = new byte[1024 * 1024]; // 1 MB buffer size
                    int bytesRead;

                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        blobOutputStream.write(buffer, 0, bytesRead);
                        uploadedBytes += bytesRead;

                        // Calculate and update progress as a percentage
                        int progress = (int) ((double) uploadedBytes / fileSize * 100);
                        updateProgress(progress, 100); // Update progress
                    }
                } catch (IOException e) {
                    // Handle IO errors (e.g., file read errors, network issues)
                    updateMessage("Error uploading file: " + e.getMessage());
                    updateProgress(0, 100);  // Reset progress on failure
                }

                return null;
            }
        };
    }

}