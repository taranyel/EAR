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
public class ComplaintDTO {
    private Integer id;
    private UserDTO accused;
    private String description;
    private LocalDateTime time;
}
