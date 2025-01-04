package joinMe.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class CommentDTO {
    private Integer id;
    private String authorUsername;
    private String text;
    private TripDTO trip;
    private LocalDateTime time;
}
