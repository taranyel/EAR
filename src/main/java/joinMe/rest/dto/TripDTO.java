package joinMe.rest.dto;

import jakarta.validation.constraints.NotBlank;
import joinMe.db.entity.TripStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class TripDTO {
    private Integer id;
    @NotBlank(message = "Title must not be blank")
    private String title;
    @NotBlank(message = "Description must not be blank")
    private String description;
    private UserDTO author;
    @NotBlank(message = "Capacity must not be blank")
    private Integer capacity;
    @NotBlank(message = "Country must not be blank")
    private String country;
    @NotBlank(message = "Start date must not be blank")
    private LocalDate startDate;
    @NotBlank(message = "End date must not be blank")
    private LocalDate endDate;
    @NotBlank(message = "Image path must not be blank")
    private String imagePath;
    private LocalDateTime created;
    private List<CommentDTO> comments;
    private List<AttendlistDTO> attendlists;
    private TripStatus status;
}
