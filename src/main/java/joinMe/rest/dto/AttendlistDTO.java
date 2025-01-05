package joinMe.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class AttendlistDTO {
    private Integer id;
    private UserDTO joiner;
    private TripDTO trip;
    private List<MessageDTO> messages;
}
