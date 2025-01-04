package joinMe.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class AddressDTO {
    private Integer id;
    private String type;
    @NotBlank(message = "City must not be blank")
    private String city;
    @NotBlank(message = "Street must not be blank")
    private String street;
    @NotBlank(message = "Number must not be blank")
    private String number;
    @NotBlank(message = "Post index must not be blank")
    private String postIndex;
    @NotBlank(message = "Country must not be blank")
    private String country;
    private List<UserDTO> residents;
}
