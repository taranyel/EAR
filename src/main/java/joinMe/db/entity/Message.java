package joinMe.db.entity;

import jakarta.persistence.*;

@Entity
public class Message extends AbstractEntity {
    @Basic(optional = false)
    @Column(name="text", nullable = false)
    private String text;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Chat chat;

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Message{" +
                "chat=" + chat +
                ", text='" + text + '\'' +
                '}';
    }
}
