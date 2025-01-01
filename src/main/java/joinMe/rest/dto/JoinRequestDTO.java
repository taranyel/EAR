package joinMe.rest.dto;

import joinMe.db.entity.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class JoinRequestDTO {
    private Integer id;
    private UserDTO requester;
    private RequestStatus status;
    private TripDTO trip;
}
