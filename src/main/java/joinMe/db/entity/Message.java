package joinMe.db.entity;

import jakarta.persistence.*;

@Entity
public class Message extends AbstractEntity {
    @Basic(optional = false)
    @Column(name="text", nullable = false)
    private String text;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Attendlist attendlist;

    public Attendlist getAttendlist() {
        return attendlist;
    }

    public void setAttendlist(Attendlist attendlist) {
        this.attendlist = attendlist;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
