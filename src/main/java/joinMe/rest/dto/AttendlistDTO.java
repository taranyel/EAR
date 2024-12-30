package joinMe.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class AttendlistDTO {
    private UserDTO joiner;
    private TripDTO trip;
}
