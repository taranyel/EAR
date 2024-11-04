package joinMe.db.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class PostInWishlist extends AbstractEntity {
    @ManyToOne
    @JoinColumn(nullable = false)
    private Wishlist wishlist;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Post post;

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Wishlist getWishlist() {
        return wishlist;
    }

    public void setWishlist(Wishlist wishlist) {
        this.wishlist = wishlist;
    }

    @Override
    public String toString() {
        return "PostInWishlist{" +
                "post=" + post +
                ", wishlist=" + wishlist +
                '}';
    }
}
