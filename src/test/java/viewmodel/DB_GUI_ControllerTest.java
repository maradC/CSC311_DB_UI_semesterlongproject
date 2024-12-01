package viewmodel;

import dao.DbConnectivityClass;
import model.Person;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
class DB_GUI_ControllerTest {

    @Test
    public void testExportCsv() throws IOException {
        DbConnectivityClass db = new DbConnectivityClass();
        db.insertUser(new Person("Test", "User", "CS", "Software", "test.user@example.com", ""));
        String csvData = db.stringAllUsers();
        assertTrue(csvData.contains("Test"));
        assertTrue(csvData.contains("User"));
    }


}