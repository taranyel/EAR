package joinMe.rest.dto;

import joinMe.db.entity.AccountStatus;
import joinMe.db.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private LocalDate birthDate;
    private String username;
    private AccountStatus status;
    private Integer rating;
    private String imagePath;
    private String password;
    private AddressDTO address;
    private List<AttendlistDTO> attendlists;
    private List<TripDTO> trips;
    private List<WishlistDTO> wishlists;
    private List<ComplaintDTO> complaints;
    private List<JoinRequestDTO> joinRequests;
}
