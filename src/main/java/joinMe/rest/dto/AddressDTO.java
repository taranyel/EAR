package joinMe.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class AddressDTO {
    private String type;
    private String city;
    private String street;
    private String number;
    private String postIndex;
    private String country;
}
