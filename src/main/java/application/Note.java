package application;

import java.time.LocalDateTime;
import java.util.Date;

public class Note {

    private int id;
    private String title;
    private LocalDateTime dateCreated;
    private String body;

    public Note (int id, String title, LocalDateTime dateCreated, String body) {
        this.id = id;
        this.title = title;
        this.dateCreated = dateCreated;
        this.body = body;
    }

    public int getId() {return this.id;}

    public String getTitle() {return this.title;}

    public LocalDateTime getDateCreated() {
        return this.dateCreated;
    }

    public String getBody() {return this.body;}

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody (String body) {
        this.body = body;
    }

}
