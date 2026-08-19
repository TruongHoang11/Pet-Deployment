package N18.haui.Pet_18.domain.dto.request;

import N18.haui.Pet_18.constant.GenderEnum;
import N18.haui.Pet_18.domain.dto.common.FlagUserDateAuditing;
import N18.haui.Pet_18.domain.entity.Role;
import N18.haui.Pet_18.validator.annotation.EnumValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserCreateDto extends FlagUserDateAuditing {

  @NotNull(message = "Email is required")
  private String email;

  @NotNull(message = "Password is required")
  private String password;

  @NotBlank(message = "Name is required")
  private String name;

  @NotNull(message = "Date of birth is required")
  private LocalDate dateOfBirth;


  @NotNull(message = "Gender is required")
  @EnumValue(name = "gender", enumClass = GenderEnum.class)
  private String gender;

  private Role role;

}
