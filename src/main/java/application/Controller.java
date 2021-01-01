package application;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class Controller implements Initializable {

    HashMap<String, Note> notes = new HashMap<>();

    List<Note> existingNotes;

    // count is used to poorly implement a random ID for each note
    // TODO: Need to change this value based on highest out of note_id when read in DB
//    Random rand = new Random();


    int count;

    Button currentButton;

    @FXML
    public VBox noteContainer;

    @FXML
    public TextField title;

    @FXML
    public TextField dateCreated;

    @FXML
    public TextArea body;

    @FXML
    public Button save;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Open database
        Datasource datasource = new Datasource();
        datasource.open();

        // this might not be a good way to do this...
//        List<Note> existingNotes = datasource.retrieveNotes();
        existingNotes = datasource.retrieveNotes();

        count = existingNotes.size();

//        TEST
        for (int i = 0; i < existingNotes.size(); i++) {

            String buttonTitle = existingNotes.get(i).getTitle();
            Integer buttonId = existingNotes.get(i).getId();
            Button btn = new Button(buttonTitle);

            // attach note to button as user data
            btn.setUserData(existingNotes.get(i));

            btn.setText(buttonTitle);

            btn.setId(buttonId.toString());
            noteContainer.getChildren().add(btn);

            btn.setOnAction(e -> {
                        System.out.println(btn.getId() + " clicked!");
                        Note n = (Note) (((Button) e.getSource()).getUserData());
                        title.setText(n.getTitle());
//                        dateCreated.setText("Date created: " + n.getDateCreated().toString());
                        body.setText(n.getBody());
                        currentButton = (Button) e.getSource();
                    }
            );

            System.out.println(existingNotes.get(i).getId());
            System.out.println(btn.getId());
        }
    }

    public void CloseApp(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    public List<Note> NewNote(ActionEvent event) throws SQLException {
        // create new note
        Note newNote = new Note(count, "New Note", LocalDateTime.now(), "");

        // convert dateCreated to String because sqlite doesn't have a date data type
        String noteDateAsString = newNote.getDateCreated().toString();

        // TODO: Is there a way I can do this without opening up the DB again? It's already opened by Main
        Datasource datasource = new Datasource();
        datasource.open();
        datasource.insertNote(newNote.getId(), newNote.getTitle(), noteDateAsString, newNote.getBody());

        // now should I close the DB?
        datasource.close();

        // why do I have both a hashmap and arraylist of notes?
        // add note to noteslist
//        notesList.add(newNote);
        existingNotes.add(newNote);
//        notes.put(Integer.toString(count), newNote);
        count++;

        // create a new button to allow user to select the new note
        Button btn = new Button("New Note");
        btn.setId(Integer.toString(newNote.getId()));

        btn.setUserData(btn);


        // add button to the button container
        noteContainer.getChildren().add(btn);

        btn.setTextFill(Color.BLACK);

        // presents the stored note data to the user when particular note button is clicked
//        btn.setOnAction(e -> {
//            System.out.println(btn.getId() + " clicked!");
//            Note n = (Note) (((Button) e.getSource()).getUserData());
//            title.setText(n.getTitle());
//            dateCreated.setText("Date created: " + n.getDateCreated().toString());
//            body.setText(n.getBody());




//            currentButton = (Button) e.getSource();
//            }
//        );
        // old code
        btn.setOnAction(e -> {
                    System.out.println(btn.getId() + " clicked!");
                    title.setText(newNote.getTitle());
                    dateCreated.setText("Date created: " + newNote.getDateCreated().toString());
                    body.setText(newNote.getBody());
                    currentButton = (Button) e.getSource();
                }
        );

//        return notesList;
        return existingNotes;
    }

//    ###################
//    Problem: before the below changes, if DB was empty the code would break on save.
//    with my changes below (reversion to old code) a newly created note will save and the data persists
//    when the program is loaded again, but if I then try to save again it breaks.

//    Update: I determined that it was because I was creating a new Note by calling .get() on notes. Notes was empty
//    because it's existingNotes that is populated on initialisation. I've since tried only using existingNotes to store
//    both old and new notes but now I'm getting an index out of bounds error on save.

//    Update2: The index out of bounds error was occuring when I passed currentButton.getId to existingNotes.get(). This value
//    was a random int up to 1000, so it was trying to get a note with an ID of 436 for example from a list of length 3 or 4. I've
//    changed count back to start at 0 and increment by 1 each time a new note is created but I need a way to iterate through the existing
//    notes on start up and update count so that count doesn't start from 0 on each start-up resulting in duplicate IDs.

//    Update3: I think I've fixed the count issue by setting count equal to existingNotes.size() in the initialization method. There will be an
//    issue when I create a delete method because if I delete a note that doesn't have the highest ID then when count is re-initialised on
//    start up there will be a duplicate ID when I create a new note. I could probably fix this by iterating through all IDs and setting count
//    to be the highest ID in the existingNotes list. 
//    #####################


    @FXML
    public void onSaveData(ActionEvent event) throws SQLException {

        currentButton.setText(title.getText());

//        Note note = notes.get(currentButton.getId());
        Note note = existingNotes.get(Integer.parseInt(currentButton.getId()));
        System.out.println(currentButton.getId());
        System.out.println(note);
        // it's blowing up because note is null because if the note exists at start-up it is stored in existingNotes not notes
        // I've tried fixing this by adding new notes to existingNotes instead
        note.setTitle(title.getText());
        note.setBody(body.getText());

// Brendan's 3 lines commented out below, trying mine above
//        Note n = (Note) currentButton.getUserData();
//        n.setTitle(title.getText());
//        n.setBody(body.getText());

        //TODO: have to update note title, body in database on save
        Datasource datasource = new Datasource();

//        changing n to note
        System.out.println("saving to db, id: " + note.getId() + " note.getTitle: " + note.getTitle() + " note.getText(): " + title.getText());
        datasource.updateNote(note.getId(), title.getText(), body.getText());

    }
}
