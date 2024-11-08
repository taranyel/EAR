package joinMe.db.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@NamedQueries({
        @NamedQuery(name = "Complaint.findByAccused", query = "SELECT c FROM Complaint c WHERE c.accused = :accused")
})
public class Complaint extends AbstractEntity {
    @Basic(optional = false)
    @Column(name="description", nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User accused;
}
