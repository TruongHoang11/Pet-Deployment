package N18.haui.Pet_18.domain.dto.response;


import N18.haui.Pet_18.constant.GenderEnum;
import N18.haui.Pet_18.domain.dto.common.FlagUserDateAuditing;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto extends FlagUserDateAuditing {

  private String id;
  private String name;
  private String email;
  private LocalDate dateOfBirth;
  private GenderEnum gender;
  private String avatarUrl;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String roleName;

}

