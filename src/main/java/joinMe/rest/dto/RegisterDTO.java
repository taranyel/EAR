package joinMe.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class RegisterDTO {

    @Valid
    @NotNull(message = "User must not be blank")
    UserDTO user;

    @Valid
    @NotNull(message = "Address must not be blank")
    AddressDTO address;
}
