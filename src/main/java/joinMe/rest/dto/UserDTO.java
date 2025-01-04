package joinMe.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "Firstname must not be blank")
    private String firstName;
    @NotBlank(message = "Lastname must not be blank")
    private String lastName;
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Invalid email format")
    private String email;
    private Role role;
    @NotBlank(message = "Birth date must not be blank")
    private LocalDate birthDate;
    @NotBlank(message = "Username must not be blank")
    private String username;
    private AccountStatus status;
    private Integer averageRating;
    private List<RatingDTO> ratings;
    private String imagePath;
    @NotBlank(message = "Password must not be blank")
    private String password;
    private AddressDTO address;
    private List<AttendlistDTO> attendlists;
    private List<TripDTO> trips;
    private List<WishlistDTO> wishlists;
    private List<ComplaintDTO> complaints;
    private List<JoinRequestDTO> joinRequests;
}
