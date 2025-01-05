package joinMe.rest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class RatingDTO {

    @NotNull(message = "Rating must not be blank")
    private Integer rating;

    private String comment;
}
