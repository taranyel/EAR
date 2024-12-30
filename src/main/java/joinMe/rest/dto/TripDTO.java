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
public class TripDTO {
    private String title;
    private String description;
    private UserDTO author;
    private String capacity;
    private String country;
    private String startDate;
    private String endDate;
    private String imagePath;
    private String created;
    private List<CommentDTO> comments;
    private String status;
}
