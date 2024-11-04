package joinMe.db.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

import java.util.List;

@Entity
public class Wishlist extends AbstractEntity {
    @OneToOne
    private User owner;

    @OneToMany
    @JoinColumn(name = "id_wishlist")
    private List<PostInWishlist> postsInWishlist;

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<PostInWishlist> getPostsInWishlist() {
        return postsInWishlist;
    }

    public void setPostsInWishlist(List<PostInWishlist> postsInWishlist) {
        this.postsInWishlist = postsInWishlist;
    }

    @Override
    public String toString() {
        return "Wishlist{" +
                "owner=" + owner +
                ", postsInWishlist=" + postsInWishlist +
                '}';
    }
}
