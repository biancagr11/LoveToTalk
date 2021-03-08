package socialnetwork.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Message extends Entity<Long>{
    private User from;
    private List<User> to;
    private String message;
    private LocalDateTime date;

    public Message(){}

    public Message( User from, List<User> to, String message, LocalDateTime date) {

        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
    }

    //Getters

    public User getFrom() { return from; }
    public List<User> getTo() { return to; }
    public String getMessage() { return message; }
    public LocalDateTime getDate() { return date; }

    @Override
    public String toString() {

        return from.getFirstName() + ": " +message;
    }
}
