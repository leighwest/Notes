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

    List<Note> existingNotes;

    int count;  // variable to assign unique Note ID

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

        existingNotes = datasource.retrieveNotes();

        count = existingNotes.size();

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

                        Integer dayCreated = n.getDateCreated().getDayOfMonth();
                        String monthCreated = n.getDateCreated().getMonth().toString();
                        Integer yearCreated = n.getDateCreated().getYear();
                        dateCreated.setText("Date created: " + dayCreated.toString() + ' ' +
                        monthCreated.substring(0, 1) + monthCreated.substring(1).toLowerCase() + ' ' +
                        yearCreated);

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

        Datasource datasource = new Datasource();
        datasource.open();
        datasource.insertNote(newNote.getId(), newNote.getTitle(), noteDateAsString, newNote.getBody());

        datasource.close();

        existingNotes.add(newNote);
        count++;

        // create a new button to allow user to select the new note
        Button btn = new Button("New Note");
        btn.setId(Integer.toString(newNote.getId()));

        btn.setUserData(btn);


        // add button to the button container
        noteContainer.getChildren().add(btn);

        btn.setTextFill(Color.BLACK);

        btn.setOnAction(e -> {
            System.out.println(btn.getId() + " clicked!");
            title.setText(newNote.getTitle());
            Integer dayCreated = newNote.getDateCreated().getDayOfMonth();
            String monthCreated = newNote.getDateCreated().getMonth().toString();
            Integer yearCreated = newNote.getDateCreated().getYear();
            dateCreated.setText("Date created: " + dayCreated.toString() + ' ' +
                    monthCreated.substring(0, 1) + monthCreated.substring(1).toLowerCase() + ' ' +
                    yearCreated);
            body.setText(newNote.getBody());
            currentButton = (Button) e.getSource();
        }
        );

//        return notesList;
        return existingNotes;
    }



    @FXML
    public void onSaveData(ActionEvent event) throws SQLException {

        currentButton.setText(title.getText());

        Note note = existingNotes.get(Integer.parseInt(currentButton.getId()));
        System.out.println(currentButton.getId());
        System.out.println("the notes' contents are: " + note);
        note.setTitle(title.getText());
        System.out.println("date created is: " + note.getDateCreated());

        note.setBody(body.getText());

        Datasource datasource = new Datasource();

        System.out.println("saving to db, id: " + note.getId() + " note.getTitle: " + note.getTitle() + " note.getText(): " + title.getText());
        datasource.updateNote(note.getId(), title.getText(), body.getText());

    }
}
