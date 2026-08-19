package N18.haui.Pet_18.repository;

import N18.haui.Pet_18.domain.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission>{
    boolean existsByApiPathIgnoreCaseAndMethodIgnoreCaseAndModuleIgnoreCase(String apiPath, String method, String module);

    //// Tìm tất cả người dùng có ID nằm trong danh sách providedIds
    List<Permission> findByIdIn(List<Long> ids);

}
