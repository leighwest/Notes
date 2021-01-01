package application;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Datasource {
    public static final String DB_NAME = "notes.db";

    public static final String CONNECTION_STRING = "jdbc:sqlite:C:\\Users\\Leigh West\\OneDrive\\Documents\\Notes\\" + DB_NAME;

    public static final String TABLE_NOTES = "notes";
    public static final String COLUMN_NOTES_ID = "id";
    public static final String COLUMN_NOTES_TITLE = "title";
    public static final String COLUMN_NOTES_DATECREATED = "dateCreated";
    public static final String COLUMN_NOTES_BODY = "body";

    public static final String INSERT_NOTE = "INSERT INTO " + TABLE_NOTES +
            '(' + COLUMN_NOTES_ID + ", " + COLUMN_NOTES_TITLE + ", " + COLUMN_NOTES_DATECREATED + ", " + COLUMN_NOTES_BODY +
            ") VALUES(?, ?, ?, ?)";

    // NOT sure if I need this, all it does is return the notes ID when I'm giving it the notes ID
    public static final String QUERY_NOTE = "SELECT " + COLUMN_NOTES_ID + " FROM " +
            TABLE_NOTES + " WHERE " + COLUMN_NOTES_ID + " = ?";

    public static final String UPDATE_NOTE = "UPDATE " + TABLE_NOTES + " SET " +
            COLUMN_NOTES_TITLE + " = ?, " + COLUMN_NOTES_BODY + " = ? WHERE " + COLUMN_NOTES_ID + " = ?";


    private Connection conn;

    private PreparedStatement insertIntoNotes;

    private PreparedStatement queryNote;

    private PreparedStatement updateNote;

    public boolean open() {
        try {
            conn = DriverManager.getConnection(CONNECTION_STRING);

            // TESTING DB ERRORS, DELETE AFTER
//            Statement statement1 = conn.createStatement();
//            statement1.execute("DROP TABLE IF EXISTS " + TABLE_NOTES);

            // If database does not contain notes table, create one
            Statement statement = conn.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS " + TABLE_NOTES +
                    " (" + COLUMN_NOTES_ID + " integer, " +
                    COLUMN_NOTES_TITLE + " text, " +
                    COLUMN_NOTES_DATECREATED + " text, " +
                    COLUMN_NOTES_BODY + " text" +
                    ")");

            insertIntoNotes = conn.prepareStatement(INSERT_NOTE);
            return true;
        } catch (SQLException e) {
            System.out.println("Something went wrong: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void close() {
        try {
            if (insertIntoNotes != null) {
                insertIntoNotes.close();
            }

            if (queryNote != null) {
                queryNote.close();
            }
        } catch (SQLException e) {
            System.out.println("Couldn't close connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean insertNote(Integer id, String title, String dateCreated, String body) {
        try {
            conn = DriverManager.getConnection(CONNECTION_STRING);
            queryNote = conn.prepareStatement(QUERY_NOTE);

            queryNote.setInt(1, id);
            ResultSet results = queryNote.executeQuery();
            if (results.next()) {
                return false;
            } else {
                // Insert the note
                insertIntoNotes.setInt(1, id);
                insertIntoNotes.setString(2, title);
                insertIntoNotes.setString(3, dateCreated);
                insertIntoNotes.setString(4, body);
                int affectedRows = insertIntoNotes.executeUpdate();

                if (affectedRows != 1) {
                    return false;
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Insert failed: " + e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println("Unable to close connection: " + e.getMessage());
            }
        }
        return false;
    }

    public List<Note> retrieveNotes() {
        Statement statement = null;
        ResultSet results = null;

        try {
            conn = DriverManager.getConnection(CONNECTION_STRING);

            statement = conn.createStatement();
            results = statement.executeQuery("SELECT * FROM " + TABLE_NOTES);

            List<Note> notes = new ArrayList<Note>();
            while(results.next()) {
                Note note = new Note();
                note.setID(results.getInt(COLUMN_NOTES_ID));
                note.setTitle(results.getString(COLUMN_NOTES_TITLE));
//                TODO: need to convert sql text to java localDateTime
                note.setBody(results.getString(COLUMN_NOTES_BODY));
                notes.add(note);
            }
            return notes;
        } catch(SQLException e) {
            System.out.println("Query failed " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            try {
                conn.close();
            } catch(SQLException e) {
                System.out.println("Unable to close connection: " + e.getMessage());
            }
        }
    }

    public boolean updateNote (Integer id, String newTitle, String body) {
        try {
            conn = DriverManager.getConnection(CONNECTION_STRING);

            updateNote = conn.prepareStatement(UPDATE_NOTE);

            updateNote.setString(1, newTitle);
            updateNote.setString(2, body);
            updateNote.setInt(3, id);

            int affectedNote = updateNote.executeUpdate();

            return affectedNote == 1;
        } catch(SQLException e) {
            System.out.println("Update failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                conn.close();
            } catch(SQLException e) {
                System.out.println("Unable to close connection: " + e.getMessage());
            }
        }
    }
}

