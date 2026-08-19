package N18.haui.Pet_18.service.impl;

import N18.haui.Pet_18.domain.dto.pagination.ResultPaginationDto;
import N18.haui.Pet_18.domain.dto.response.PermissionDto;
import N18.haui.Pet_18.domain.entity.Permission;
import N18.haui.Pet_18.domain.mapper.PermissionMapper;
import N18.haui.Pet_18.domain.specification.FilterProcessor;
import N18.haui.Pet_18.domain.specification.SpecificationBuilder;
import N18.haui.Pet_18.exception.ConflictException;
import N18.haui.Pet_18.repository.PermissionRepository;
import N18.haui.Pet_18.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    private void checkValidExistPermission( String apiPath, String method,String module){
        if(permissionRepository.existsByApiPathIgnoreCaseAndMethodIgnoreCaseAndModuleIgnoreCase(apiPath, method, module )){
            throw new ConflictException("Permission already exists!");
        }
    }

    @Override
    public PermissionDto createPermission(Permission permission)  {
        this.checkValidExistPermission(permission.getApiPath(), permission.getMethod(), permission.getModule());

        return permissionMapper.toDto(permissionRepository.save(permission));
    }

    @Override
    public PermissionDto updatePermission(Permission permission) {
        //check exist id
        Permission permissionDB = permissionRepository.findById(permission.getId()).orElseThrow(()
                -> new ConflictException("Permission with id = " + permission.getId() + " not found!"));
        // check exist by apiPath, method, module
        this.checkValidExistPermission(permission.getApiPath(), permission.getMethod(), permission.getModule());

        permissionDB.setName(permission.getName());
        permissionDB.setApiPath(permission.getApiPath());
        permissionDB.setMethod(permission.getMethod());
        permissionDB.setModule(permission.getModule());
        //update
        permissionDB = permissionRepository.save(permissionDB);

        return permissionMapper.toDto(permissionDB);
    }

    @Override
    public void deletePermission(Long id) {
        // de delete permission -> can vo bang permission and role xoa di cai rang buoc
        // co thang role nao chua cai skill xoa cai skill trong bang roi xoa skill o bang skill
        Permission permissionDB = permissionRepository.findById(id).orElseThrow(()
                -> new ConflictException("Permission with id = " + id + " not found!"));
        if(permissionDB.getRoles() != null){
            permissionDB.getRoles()
                    .forEach(role -> role.getPermissions().remove(permissionDB));
        }

        // delete permission
        permissionRepository.delete(permissionDB);
    }

    @Override
    public ResultPaginationDto fetchAllPermission(List<String> filter, Pageable pageable) {
        SpecificationBuilder<Permission> specificationBuilder = new SpecificationBuilder<>();
        FilterProcessor.process(specificationBuilder, filter);


        // Thực thi query
        Page<Permission> pagePermission = permissionRepository.findAll(specificationBuilder.build(), pageable);


        // Mapping kết quả
        ResultPaginationDto resultPaginationDTO = new ResultPaginationDto();
        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pagePermission.getTotalPages());
        meta.setTotal(pagePermission.getTotalElements());

        List<PermissionDto> result = permissionMapper.toDtoList(pagePermission.getContent());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(result);

        return resultPaginationDTO;
    }

    @Override
    public PermissionDto fetchAPermission(Long id) {
        return permissionMapper.toDto(permissionRepository.findById(id).orElseThrow(()
                -> new ConflictException("Permission with id = " + id + " not found!")));
    }


}
