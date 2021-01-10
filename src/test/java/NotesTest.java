import application.Note;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class NotesTest {

    Note note = new Note(1, "New Note", LocalDateTime.now(), "");

    @Test
    public void testGetId() {
        assertEquals(1, note.getId());
    }

    @Test
    public void testGetTitle() {
        assertEquals("New Note", note.getTitle());
    }

    @Test
    public void testGetDateCreated() {
        boolean actualValue = note.getDateCreated().minusMinutes(1).isBefore(LocalDateTime.now()) && note.getDateCreated().plusMinutes(1).isAfter(LocalDateTime.now());
        assertEquals(true, actualValue);
    }

    @Test
    public void testGetBody_isEmptyOnInitialisation() {
        assertEquals(note.getBody().length(), 0);
    }
}
