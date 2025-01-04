package joinMe.db.entity;

import jakarta.persistence.*;
import joinMe.rest.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Rating extends AbstractEntity {

    @Basic(optional = false)
    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Basic
    @Column(name = "text", nullable = false)
    private String text;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn
    private User owner;
}
