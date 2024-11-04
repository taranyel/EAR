package joinMe.db.entity;

import jakarta.persistence.*;

@Entity
public class Comment extends AbstractEntity {
    @Basic(optional = false)
    @Column(name="text", nullable = false)
    private String text;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Post post;

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "post=" + post +
                ", text='" + text + '\'' +
                '}';
    }
}
