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
public class MessageDTO {
    private Integer id;
    private AttendlistDTO attendlist;
    private String authorUsername;
    private String text;
    private LocalDateTime time;
}
