package joinMe.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class CommentDTO {
    private Integer id;
    private UserDTO author;
    private String text;
    private TripDTO trip;
}
