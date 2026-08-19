package N18.haui.Pet_18.domain.dto.request;

// import N18.haui.Pet_18.constant.GenderEnum;
// import N18.haui.Pet_18.validator.annotation.EnumValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

// import java.time.LocalDate;

@Getter
@Setter
public class ReqRegister {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    // @NotNull(message = "Date of birth is required")
    // private LocalDate dateOfBirth;

    // @NotNull(message = "Gender is required")
    // @EnumValue(name = "gender", enumClass = GenderEnum.class)
    // private String gender;


}
