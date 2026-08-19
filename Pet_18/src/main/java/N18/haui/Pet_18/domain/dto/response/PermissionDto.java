package N18.haui.Pet_18.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionDto {

    private Long id;
    private String name;       // Ví dụ: "Tạo mới sản phẩm", "Cập nhật dịch vụ"
    private String apiPath;    // Ví dụ: "/api/v1/products", "/api/v1/services/**"
    private String method;     // Ví dụ: "POST", "PUT", "GET", "DELETE"
    private String module;     // Ví dụ: "PRODUCTS", "SERVICES", "BOOKINGS"
}
