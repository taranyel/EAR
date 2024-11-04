package joinMe.db.entity;

import jakarta.persistence.*;

@Entity
public class Complaint extends AbstractEntity {
    @Basic(optional = false)
    @Column(name="description", nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User accused;

    public User getAccused() {
        return accused;
    }

    public void setAccused(User accused) {
        this.accused = accused;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Complaint{" +
                "accused=" + accused +
                ", description='" + description + '\'' +
                '}';
    }
}
