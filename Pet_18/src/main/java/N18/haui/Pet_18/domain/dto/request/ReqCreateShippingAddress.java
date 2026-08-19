package N18.haui.Pet_18.domain.dto.request;

import N18.haui.Pet_18.validator.annotation.ValidPhone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqCreateShippingAddress {

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name is too long")
    private String fullName;

    @NotBlank(message = "Phone is required")
    @ValidPhone
    private String phone;

    @NotBlank(message = "Address detail is required")
    private String addressDetail;

    @NotBlank(message = "Ward is required")
    private String ward;

    @NotBlank(message = "District is required")
    private String district;

    @NotBlank(message = "Province is required")
    private String province;


    @NotNull(message = "Is default is required")
    private Boolean isDefault = false; // optional


}
