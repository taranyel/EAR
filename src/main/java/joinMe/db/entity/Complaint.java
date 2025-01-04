package joinMe.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
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

    @Basic(optional = false)
    @Column(name = "time", nullable = false)
    private LocalDateTime time;
}
