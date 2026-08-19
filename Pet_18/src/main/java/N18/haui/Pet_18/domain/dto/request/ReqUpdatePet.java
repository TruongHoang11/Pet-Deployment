package N18.haui.Pet_18.domain.dto.request;

import N18.haui.Pet_18.constant.GenderEnum;
import N18.haui.Pet_18.validator.annotation.EnumValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ReqUpdatePet {
    @NotNull(message = "Pet ID is required")
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Species is required")
    private String specie;

    @NotNull(message = "Gender is required")
    @EnumValue(name = "gender", enumClass = GenderEnum.class)
    private String gender;

    @NotNull(message = "Birthday is required")
    @PastOrPresent(message = "Birthday must be past or present time")
    private LocalDate birthday;

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be a positive number") // Bổ sung: Tránh nhập số âm hoặc bằng 0
    private Float weight;

    private String healthStatus;
}
