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
import java.util.*;

public class Controller implements Initializable {

    HashMap<String, Note> notes = new HashMap<>();

    ArrayList<Note> notesList = new ArrayList<>();

    public int count = 1;   // used to assign id to note

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

    }

    public void CloseApp(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    public ArrayList<Note> NewNote(ActionEvent event) {
        // create new note and add to notesList
        Note newNote = new Note(count, "New Note", new Date(), "");
        // why do I have both a hashmap and arraylist of notes?
        notesList.add(newNote);
        notes.put(Integer.toString(count), newNote);
        count++;

        // create a button to allow user to select new note
        Button btn = new Button("New Note");
        btn.setId(Integer.toString(newNote.getId()));

        // add button to the button container
        noteContainer.getChildren().add(btn);

        btn.setTextFill(Color.BLACK);

        // presents the stored note data to the user when particular note button is clicked
        btn.setOnAction(e -> {
            System.out.println(btn.getId() + " clicked!");
            title.setText(newNote.getTitle());
            dateCreated.setText("Date created: " + newNote.getDateCreated().toString());
            body.setText(newNote.getBody());
            currentButton = (Button) e.getSource();
            }
        );
        return notesList;
    }

    @FXML
    public void onSaveData(ActionEvent event) {
        currentButton.setText(title.getText());
        Note note = notes.get(currentButton.getId());
        note.setTitle(title.getText());
        note.setBody(body.getText());
    }
}
