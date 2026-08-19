package N18.haui.Pet_18.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqPermissionDto {

    @NotBlank(message = "Tên Permission không được để trống!")
    private String name; // Ví dụ: "Tạo mới dịch vụ chăm sóc"

    @NotBlank(message = "API Path không được để trống!")
    private String apiPath; // Ví dụ: "/api/v1/services/**"

    @NotBlank(message = "HTTP Method không được để trống!")
    @Pattern(regexp = "^(GET|POST|PUT|DELETE|PATCH)$", message = "HTTP Method phải là một trong các giá trị: GET, POST, PUT, DELETE, PATCH")
    private String method; // Ép cấu hình chuẩn chuỗi phương thức HTTP

    @NotBlank(message = "Module không được để trống!")
    private String module; // Ví dụ: "SERVICES", "BOOKINGS", "USERS"
}