package N18.haui.Pet_18.service.impl;

import N18.haui.Pet_18.domain.dto.pagination.ResultPaginationDto;
import N18.haui.Pet_18.domain.dto.response.CommonResponseDto;
import N18.haui.Pet_18.domain.dto.response.RoleDto;
import N18.haui.Pet_18.domain.entity.Permission;
import N18.haui.Pet_18.domain.entity.Role;
import N18.haui.Pet_18.domain.mapper.PermissionMapper;
import N18.haui.Pet_18.domain.mapper.RoleMapper;
import N18.haui.Pet_18.domain.specification.FilterProcessor;
import N18.haui.Pet_18.domain.specification.SpecificationBuilder;
import N18.haui.Pet_18.exception.BadRequestException;
import N18.haui.Pet_18.repository.PermissionRepository;
import N18.haui.Pet_18.repository.RoleRepository;
import N18.haui.Pet_18.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;

    public void checkValidRoleName(String name){
        if(roleRepository.existsByNameAndDeleteFlagFalse(name)){
            throw new BadRequestException("Role with name = " + name + " already exists!");
        }
    }



    public List<Permission> convertPermissionExist(List<Permission> permissions){
        if(permissions == null || permissions.isEmpty()){
            return new ArrayList<>();
        }
        List<Long> ids = permissions.stream()
                .map(Permission::getId)
                .toList();
        return permissionRepository.findByIdIn(ids);
    }

    @Override
    public RoleDto createRole(Role role) {
        // check valid exist name
        this.checkValidRoleName(role.getName());
        List<Permission> permissionExistList = this.convertPermissionExist(role.getPermissions());
        role.setPermissions(permissionExistList);
        roleRepository.save(role);
        return roleMapper.toDto(role);
    }

    @Override
    public RoleDto updateRole(Role role) {
        if(role.getId() == null){
            throw new BadRequestException("Role id is required!");
        }
        Role roleDB = this.getRoleById(role.getId());
        if(!role.getName().equals(roleDB.getName())){
            this.checkValidRoleName(role.getName());
        }

        List<Permission> permissionExistList = this.convertPermissionExist(role.getPermissions());

        roleDB.setName(role.getName());
        roleDB.setDescription(role.getDescription());
        roleDB.setActiveFlag(role.getActiveFlag());
        roleDB.setPermissions(permissionExistList);

        // update role
        roleDB = roleRepository.save(roleDB);
        return roleMapper.toDto(roleDB);
    }

    @Override
    public CommonResponseDto deleteRole(Long id) {
        Role role = roleRepository.findByIdAndDeleteFlagFalse(id).orElseThrow(
                () -> new BadRequestException("Role with id = " + id + " not found!")
        );
        role.setDeleteFlag(true);
        roleRepository.save(role);
        return new CommonResponseDto(true, "Role deleted successfully!");
    }

    @Override
    public ResultPaginationDto fetchAllRole(List<String> filter, Pageable pageable) {
        SpecificationBuilder<Role> specificationBuilder = new SpecificationBuilder<>();
        FilterProcessor.process(specificationBuilder, filter);

        Specification<Role> spec = specificationBuilder.build();
        Specification<Role> softDeleteSpec = (root, query, cb) -> cb.equal(root.get("deleteFlag"), false);

        Specification<Role> finalSpec = (spec == null) ? softDeleteSpec : spec.and(softDeleteSpec);

        Page<Role> pageRole = roleRepository.findAll(finalSpec, pageable);

        ResultPaginationDto resultPaginationDTO = new ResultPaginationDto();
        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageRole.getTotalPages());
        meta.setTotal(pageRole.getTotalElements());

        List<RoleDto> result = roleMapper.toDtoList(pageRole.getContent());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(result);

        return resultPaginationDTO;
    }

    @Override
    public RoleDto fetchARole(Long id) {
        Role role =  roleRepository.findById(id).orElseThrow(
                () -> new BadRequestException("Role with id = " + id + " not found!")
        );

        return roleMapper.toDto(role);
    }

    @Override
    public Role getRoleById(Long id) {

        return roleRepository.findById(id).orElseThrow(
                () -> new BadRequestException("Role with id = " + id + " not found!")
        );
    }

}
