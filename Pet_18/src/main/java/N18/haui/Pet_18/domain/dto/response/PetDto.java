package N18.haui.Pet_18.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PetDto {


    private Long id; // pet id
    private String ownerId;
    private String name;
    private String specie;
    private String gender;
    private LocalDate birthday;
    private float weight;
    private String healthStatus;
    private Boolean activeFlag;
    private Boolean deleteFlag;
}
