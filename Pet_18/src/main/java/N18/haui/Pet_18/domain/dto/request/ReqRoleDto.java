package N18.haui.Pet_18.domain.dto.request;

import N18.haui.Pet_18.domain.entity.Permission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReqRoleDto {

    @NotBlank(message = "Tên Role không được để trống!")
    @Pattern(regexp = "^ROLE_[A-Z0-String_]+$", message = "Tên Role phải bắt đầu bằng cụm 'ROLE_' và viết hoa (Ví dụ: ROLE_ADMIN, ROLE_MANAGER)")
    private String name; // Ép chuẩn đặt tên Spring Security

    private String description; // Mô tả vai trò

    // Admin chỉ cần truyền danh sách ID của các quyền lên khi lưu
    private List<Permission> permissions;
}