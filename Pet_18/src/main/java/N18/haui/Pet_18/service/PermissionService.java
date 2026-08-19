package N18.haui.Pet_18.service;

import N18.haui.Pet_18.domain.dto.pagination.ResultPaginationDto;
import N18.haui.Pet_18.domain.dto.response.PermissionDto;
import N18.haui.Pet_18.domain.entity.Permission;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PermissionService {
    PermissionDto createPermission(Permission permission);
    PermissionDto updatePermission(Permission permission);
    void deletePermission(Long id);
    ResultPaginationDto fetchAllPermission(List<String> filter, Pageable pageable);

    PermissionDto fetchAPermission(Long id);
}
