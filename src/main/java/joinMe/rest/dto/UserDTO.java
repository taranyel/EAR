package joinMe.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String birthDate;
    private String username;
    private String status;
    private String rating;
    private String imagePath;
    private String password;
    private AddressDTO address;
}
