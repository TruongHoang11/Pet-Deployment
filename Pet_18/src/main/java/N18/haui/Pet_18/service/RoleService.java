package N18.haui.Pet_18.service;

import N18.haui.Pet_18.domain.dto.pagination.ResultPaginationDto;
import N18.haui.Pet_18.domain.dto.response.CommonResponseDto;
import N18.haui.Pet_18.domain.dto.response.RoleDto;
import N18.haui.Pet_18.domain.entity.Role;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface RoleService {

    RoleDto createRole(Role role);
    RoleDto updateRole(Role role);
    CommonResponseDto deleteRole(Long id);
    ResultPaginationDto fetchAllRole(List<String> filter, Pageable pageable);

    RoleDto fetchARole(Long id);

    Role getRoleById(Long id);
}
