package N18.haui.Pet_18.configuration;

import N18.haui.Pet_18.domain.entity.Permission;
import N18.haui.Pet_18.domain.entity.Role;
import N18.haui.Pet_18.domain.entity.User;
import N18.haui.Pet_18.exception.ForbiddenException;
import N18.haui.Pet_18.security.SecurityUtil;
import N18.haui.Pet_18.service.UserService;
import N18.haui.Pet_18.constant.RoleConstant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;

// bộ chặn khi người khác gọi vào ứng dụng của mình
//HandlerInterceptor
public class PermissionInterceptor implements HandlerInterceptor {
    @Autowired
    UserService userService;

//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//       // 1. Lấy mẫu URL (ví dụ: /api/users/{id})
//        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
//
//        // 2. Lấy HTTP Method (GET, POST, DELETE...)
//        String httpMethod = request.getMethod();
//       // lấy đường dẫn nguyên bản mà người dùng nhập trên thanh địa chỉ hoặc gửi từ client.
//        //Vi du: @DeleteMapping("/api/users/{id}")
//       // request.getRequestURI(): Trả về /api/users/10. Đây là dữ liệu thực tế.
//        String requestURI = request.getRequestURI();
//        System.out.println(">>> RUN preHandle");
//        System.out.println(">>> path= " + path);
//        System.out.println(">>> httpMethod= " + httpMethod);
//        System.out.println(">>> requestURI= " + requestURI);
//
//
//
//        String id = SecurityUtil.getCurrentUserLogin().orElse("");
//
//
//                if(!id.isEmpty()){
//            User user = userService.getUserWithRoleAndPermissions(id);
//            if(user != null){
//                Role role = user.getRole();
//                if(role != null){
//                    // Nếu là ADMIN thì bỏ qua kiểm tra permission (admin full quyền)
//                    if (RoleConstant.ADMIN.equals(role.getName())){
//                        return true;
//                    }
//
//                    List<Permission> permissions = role.getPermissions();
//                    boolean checkAllow = permissions.stream().anyMatch(permission ->
//                            permission.getApiPath().equals(path) && permission.getMethod().equals(httpMethod));
//                    if (!checkAllow){
//                        throw new ForbiddenException("You do not have permission to access this endpoint.");
//                    }
//                } else{
//                    throw new ForbiddenException("You do not have permission to access this endpoint.");
//                }
//            }
//        }
//
//
//        return true;
//
//
//    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String httpMethod = request.getMethod();

        // 1. Nếu path là null (thường xảy ra với các request không map được),
        // có thể bỏ qua hoặc xử lý tùy ý.
        if (path == null) return true;

        // 2. Lấy thông tin user hiện tại từ SecurityContext
        String id = SecurityUtil.getCurrentUserLogin().orElse("");

        // 3. NẾU CHƯA ĐĂNG NHẬP:
        // Bỏ qua kiểm tra permission và trả về true.
        // Spring Security (đã cấu hình ở bước trước) sẽ quyết định
        // request này có được phép vào hay không (dựa trên permitAll hoặc authenticated).
        if (id.isEmpty()) {
            return true;
        }

        // 4. NẾU ĐÃ ĐĂNG NHẬP: Mới thực hiện check quyền từ DB
        User user = userService.getUserWithRoleAndPermissions(id);
        if (user != null) {
            Role role = user.getRole();
            if (role != null) {
                // Admin full quyền
                if (RoleConstant.ADMIN.equals(role.getName())) {
                    return true;
                }

                List<Permission> permissions = role.getPermissions();
                boolean checkAllow = permissions.stream().anyMatch(permission ->
                        permission.getApiPath().equals(path) && permission.getMethod().equals(httpMethod));

                if (!checkAllow) {
                    throw new ForbiddenException("You do not have permission to access this endpoint.");
                }
            }
        }

        return true;
    }
}