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
public class ComplaintDTO {
    private Integer id;
    private UserDTO accused;
    @NotBlank(message = "Description must not be blank")
    private String description;
    private LocalDateTime time;
}
