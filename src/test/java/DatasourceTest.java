import application.Datasource;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.*;

import static org.junit.Assert.*;


public class DatasourceTest {

    Datasource ds = new Datasource();

    /*

    @Test
    public void testOpen_whenNoDatabase() throws Exception {

        boolean madeBackup = false;

//        File f = new File("C:\\Users\\Leigh West\\OneDrive\\Documents\\Notes\\notes.db");
//        if(f.exists() && !f.isDirectory()) {
////            assertTrue(f.renameTo(new File("C:\\Users\\Leigh West\\OneDrive\\Documents\\Notes\\notesBackup.db")));
//            madeBackup = true;
//
//        }


//        String HOME = System.getProperty("C:\\Users\\Leigh West\\OneDrive\\Documents\\Notes\\");
        String HOME = System.getProperty("user.home");
        Path p = Paths.get(HOME+"\\OneDrive\\Documents\\Notes\\notes.db");
        if (Files.exists(p)) {
            Path notesCopy = Paths.get(HOME+"\\OneDrive\\Documents\\Notes\\notesCopy.db");
//            Files.createFile(notesCopy);
            Files.copy(p, notesCopy, StandardCopyOption.REPLACE_EXISTING);
            madeBackup = true;
            Files.delete(p);
        }

        Connection conn = DriverManager.getConnection(Datasource.CONNECTION_STRING);

        assertTrue(ds.open());

        DatabaseMetaData dbm = conn.getMetaData();
        ResultSet tables = dbm.getTables(null, null, Datasource.TABLE_NOTES, null);
        assertTrue (tables.next());

        ds.close();


        if(madeBackup) {
//            f.renameTo(new File("C:\\Users\\Leigh West\\OneDrive\\Documents\\Notes\\notes.db"));
//            System.out.println(f.getAbsoluteFile());
            Path notesCopy = Paths.get(HOME+"\\OneDrive\\Documents\\Notes\\notesCopy.db");
            Path originalNotes = Paths.get(HOME+"\\OneDrive\\Documents\\Notes\\notes.db");
//            Files.copy(notesCopy, originalNotes, StandardCopyOption.REPLACE_EXISTING);

        }

    }


     */

    // Tests when it is assumed that the DB file exists, but the table does not
    @Test
    public void testOpen_whenNoTable() throws Exception {

        Connection conn = DriverManager.getConnection(Datasource.CONNECTION_STRING);

        Statement statement1 = conn.createStatement();
        statement1.execute("DROP TABLE IF EXISTS " + Datasource.TABLE_NOTES);

        // check if table has actually been dropped
        DatabaseMetaData dbm = conn.getMetaData();
        ResultSet tables = dbm.getTables(null, null, Datasource.TABLE_NOTES, null);
        assertFalse (tables.next());

        assertTrue(ds.open());

        tables = dbm.getTables(null, null, Datasource.TABLE_NOTES, null);

        assertTrue(tables.next());

    }
}
