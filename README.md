
# Chris's Student Database System

## Overview
The **Chris's Student Database System** is a JavaFX application designed to manage employee data, including their names, majors, and other related information. The application allows users to add, edit, delete, and view employee records stored in a database. It also provides functionality for importing and exporting data, as well as ensuring data integrity through form validation.

### Features:
- **UI State Management**: Dynamic enabling/disabling of buttons based on user actions and form validation.
- **Form Validation**: Ensures correct data input using regular expressions.
- **Data Import/Export**: Support for importing and exporting employee data via CSV files.
- **User Session Management**: Tracks user login and saves preferences.
- **Thread Safety**: Enhances the UserSession class to be thread-safe for concurrent operations.

## Requirements
- Java 11 or higher
- JavaFX SDK (ensure it's properly linked to your IDE or project)
- Maven (if using Maven for dependency management)
- Access to a database (set your own server name, username, and password for database connectivity)

## Setup

### 1. Clone the Repository
Start by cloning the repository to your local machine:

```bash
git clone https://github.com/moaathalrajab/CSC311_DB_UI_semesterlongproject.git
```

### 2. Open the Project in Your IDE
Open the project using your preferred Java IDE (e.g., IntelliJ IDEA, Eclipse).

### 3. Database Configuration
- Make sure you have access to your own database server.
- Update the database connection details (server name, username, and password) in the configuration files where needed.

### 4. Run the Application
Once you’ve set up the database and configured the necessary details, you can run the JavaFX application.

In your IDE, locate the `Main` class and run it as a Java application.

---

## Features & Functionality

### UI State Management
- **Edit/Delete Buttons**: The "Edit" and "Delete" buttons are enabled only if a record is selected in the table view. Otherwise, these buttons are disabled.
- **Add Button**: The "Add" button is enabled only when the form fields are valid. The form includes validation for name and other fields (e.g., email, major).
- **Menu Items**: Menu items corresponding to actions (e.g., import/export) are disabled or grayed out if no record is selected.

### Form Validation
- **Regular Expressions**: Input fields such as name, email, and phone number are validated using regular expressions to ensure data integrity.
- **Major Dropdown**: The "Major" field uses a dropdown menu populated with predefined values such as "CS", "CPIS", and "English", defined as an enum.

### Data Import/Export (CSV)
- **Import CSV**: You can import employee data from a CSV file and populate the table with the records.
- **Export CSV**: You can export the current records in the table to a CSV file.

### User Session Management
- The application keeps track of the user session by storing the username and password in a preference file upon user login.
- The `UserSession` class has been enhanced to be thread-safe to ensure proper handling of multiple concurrent operations.

---

## How to Use

### 1. Sign Up / Log In
- When you first launch the application, you'll be presented with a login screen.
- You can either sign up for a new account or log in with your credentials.
- The username and password are saved in preferences for future logins.

### 2. Managing Employee Records
- After logging in, you will see the main screen where employee records are displayed in a table.
- **Add Record**: Click the "Add" button to add a new employee. Ensure all fields are filled out correctly.
- **Edit Record**: Select an employee record from the table, click "Edit", and modify the details.
- **Delete Record**: Select an employee record and click "Delete" to remove it from the table.

### 3. Import and Export Data
- **Import CSV**: You can import data from a CSV file using the "Import" option from the menu.
- **Export CSV**: The "Export" menu item allows you to export the current records to a CSV file.

---

## Additional Features (Your Touch)
Here are some improvements made to enhance the overall user experience and the functionality of the application:
1. **Branding and UI Theme**: The application features a customized theme and logo, aligning with the brand identity of the Pet Employee Registration System.
2. **Error Handling**: Comprehensive error handling to notify users of invalid inputs, failed database connections, or file import/export issues.
3. **Responsive UI**: The layout adjusts based on window size to ensure a responsive and user-friendly interface.
4. **Database Optimizations**: Optimized database queries and improved UI data binding to ensure faster loading and efficient handling of employee records.

---

## Acknowledgments
- Thanks to the course instructors and the JavaFX community for their contributions to this project.
- Special thanks to the GitHub repository [https://github.com/moaathalrajab/CSC311_DB_UI_semesterlongproject](https://github.com/moaathalrajab/CSC311_DB_UI_semesterlongproject) for providing the base code.

