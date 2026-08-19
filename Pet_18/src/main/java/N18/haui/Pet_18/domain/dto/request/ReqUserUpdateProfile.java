package N18.haui.Pet_18.domain.dto.request;

import N18.haui.Pet_18.constant.GenderEnum;
import N18.haui.Pet_18.validator.annotation.EnumValue;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
public class ReqUserUpdateProfile {


    private String name;

    private LocalDate dateOfBirth;

    @EnumValue(name = "gender", enumClass = GenderEnum.class)
    private String gender;
}
