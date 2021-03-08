package socialnetwork.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Friendship extends Entity<Tuple<Long,Long>> {

    LocalDateTime date;

    public Friendship(LocalDateTime date) {
        this.date=date;
    }

    /**
     *
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Friendship{ " +
                "date=" + date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ", intre: "+this.getId().getLeft()+" si "+this.getId().getRight()+
                '}';
    }
}
