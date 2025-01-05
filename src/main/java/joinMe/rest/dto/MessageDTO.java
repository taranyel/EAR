package joinMe.rest.dto;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Message must not be blank")
    private String text;

    private LocalDateTime time;
}
