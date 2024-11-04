package joinMe.db.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(name = "Post.findByCountry", query = "SELECT p FROM Post p WHERE p.country = :country")
})
public class Post extends AbstractEntity{
    @Basic(optional = false)
    @Column(name="title", nullable = false)
    private String title;

    @Basic(optional = false)
    @Column(name = "description", nullable = false)
    private String description;

    @Basic(optional = false)
    @Column(name = "image_path", nullable = false)
    private String imagePath;

    @Basic(optional = false)
    @Column(name = "country", nullable = false)
    private String country;

    @Basic(optional = false)
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Basic(optional = false)
    @Column(name = "from", nullable = false)
    private Date from;

    @Basic(optional = false)
    @Column(name = "till", nullable = false)
    private Date till;

    @Basic(optional = false)
    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User author;

    @OneToMany
    @JoinColumn(name = "id_post")
    private List<Comment> comments;

    @OneToMany
    @JoinColumn(name = "id_post")
    private List<PostInWishlist> postInWishlists;

    @OneToOne
    private Chat chat;

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getTill() {
        return till;
    }

    public void setTill(Date till) {
        this.till = till;
    }

    public List<PostInWishlist> getPostInWishlists() {
        return postInWishlists;
    }

    public void setPostInWishlists(List<PostInWishlist> postInWishlists) {
        this.postInWishlists = postInWishlists;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "Post{" +
                "author=" + author +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", country='" + country + '\'' +
                ", capacity=" + capacity +
                ", from=" + from +
                ", till=" + till +
                ", created=" + created +
                ", comments=" + comments +
                ", postInWishlists=" + postInWishlists +
                ", chat=" + chat +
                '}';
    }
}
