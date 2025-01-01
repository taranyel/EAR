package joinMe.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class WishlistDTO {
    private Integer id;
    private UserDTO owner;
    private TripDTO trip;
}
