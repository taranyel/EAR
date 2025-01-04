package joinMe.rest.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class RegisterDTO {
    @Valid
    UserDTO user;
    @Valid
    AddressDTO address;
}
