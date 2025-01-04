package joinMe.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "User must not be blank")
    UserDTO user;
    @Valid
    @NotBlank(message = "Address must not be blank")
    AddressDTO address;
}
