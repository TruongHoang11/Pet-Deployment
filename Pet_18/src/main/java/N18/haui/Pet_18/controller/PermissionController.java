package N18.haui.Pet_18.controller;

import N18.haui.Pet_18.base.RestApiV1;
import N18.haui.Pet_18.constant.UrlConstant;
import N18.haui.Pet_18.domain.entity.Permission;
import N18.haui.Pet_18.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestApiV1
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @PostMapping(UrlConstant.Permission.CREATE_PERMISSION)
    public ResponseEntity<?> createPermission(@RequestBody @Valid Permission permission){
        return ResponseEntity.status(HttpStatus.CREATED).body(permissionService.createPermission(permission));
    }

    @PutMapping(UrlConstant.Permission.UPDATE_PERMISSION)
    public ResponseEntity<?> updatePermission(@RequestBody @Valid Permission permission){
        return ResponseEntity.status(HttpStatus.OK).body(permissionService.updatePermission(permission));
    }

    @GetMapping(UrlConstant.Permission.GET_ALL_PERMISSION)
    public ResponseEntity<?> getAllPermission(
             @RequestParam(required = false) List<String> filter,
            Pageable pageable
            ){
        return ResponseEntity.status(HttpStatus.OK).body(permissionService.fetchAllPermission(filter, pageable));
    }

    @GetMapping(UrlConstant.Permission.GET_PERMISSION)
    public ResponseEntity<?> getAPermission(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(permissionService.fetchAPermission(id));
    }

    @DeleteMapping(UrlConstant.Permission.DELETE_PERMISSION)
    public ResponseEntity<?> deleteAPermission(@PathVariable Long id){
        permissionService.deletePermission(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
