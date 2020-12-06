package application;

import java.sql.*;
import java.time.LocalDateTime;

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


    private Connection conn;

    private PreparedStatement insertIntoNotes;

    private PreparedStatement queryNote;

    public boolean open() {
        try {
            conn = DriverManager.getConnection(CONNECTION_STRING);

            // If database does not contain notes table, create one
            Statement statement = conn.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS " + TABLE_NOTES +
                    " (" + COLUMN_NOTES_ID + " integer, " +
                    COLUMN_NOTES_TITLE + " text, " +
                    COLUMN_NOTES_DATECREATED + " text, " +
                    COLUMN_NOTES_BODY + " text" +
                    ")");

            // note sure if this should go here or somewhere else
            insertIntoNotes = conn.prepareStatement(INSERT_NOTE);

            queryNote = conn.prepareStatement(QUERY_NOTE);

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

        } catch (SQLException e) {
            System.out.println("Couldn't close connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int insertNote(Integer id, String title, String dateCreated, String body) throws SQLException {
        queryNote.setInt(1, id);
        ResultSet results = queryNote.executeQuery();
        if (results.next()) {
            return results.getInt(1);
        } else {
            // Insert the note
            insertIntoNotes.setInt(1, id);
            insertIntoNotes.setString(2, title);
            insertIntoNotes.setString(3, dateCreated);
            insertIntoNotes.setString(4, body);
            int affectedRows = insertIntoNotes.executeUpdate();

            // not sure if then I then need to commit

            if (affectedRows != 1) {
                throw new SQLException("Couldn't insert note");
            }

            ResultSet generatedKeys = insertIntoNotes.getGeneratedKeys();
            if(generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Couldn't get id for note");
            }
        }
    }
}
