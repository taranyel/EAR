package joinMe.db.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Chat extends AbstractEntity{
    @Basic(optional = false)
    @Column(name="id_admin", nullable = false)
    private Integer id_admin;

    @OneToMany
    @JoinColumn(name = "id_chat")
    private List<UserInChat> usersInChat;

    @OneToMany
    @JoinColumn(name = "id_chat")
    private List<Message> messages;

    @OneToOne
    private Post post;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User admin;

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public List<UserInChat> getUsersInChat() {
        return usersInChat;
    }

    public void setUsersInChat(List<UserInChat> usersInChat) {
        this.usersInChat = usersInChat;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public Integer getId_admin() {
        return id_admin;
    }

    public void setId_admin(Integer id_admin) {
        this.id_admin = id_admin;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "admin=" + admin +
                ", id_admin=" + id_admin +
                ", usersInChat=" + usersInChat +
                ", messages=" + messages +
                ", post=" + post +
                '}';
    }
}
