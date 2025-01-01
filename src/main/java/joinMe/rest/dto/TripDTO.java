package joinMe.rest.dto;

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
    private String title;
    private String description;
    private UserDTO author;
    private Integer capacity;
    private String country;
    private LocalDate startDate;
    private LocalDate endDate;
    private String imagePath;
    private LocalDateTime created;
    private List<CommentDTO> comments;
    private List<AttendlistDTO> attendlists;
    private TripStatus status;
}
