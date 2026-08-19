package N18.haui.Pet_18.controller;


import N18.haui.Pet_18.domain.entity.Role;
import N18.haui.Pet_18.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;
    public RoleController(RoleService roleService){
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    public ResponseEntity<?> createRole(@Valid @RequestBody Role role){
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.createRole(role));
    }

    @PutMapping("/roles")
    public ResponseEntity<?> updateRole(@Valid @RequestBody Role role){
        return ResponseEntity.status(HttpStatus.OK).body(roleService.updateRole(role));
    }

    @GetMapping("/roles")
    public ResponseEntity<?> fetchAllRoles(
            @RequestParam(value = "filter", required = false) List<String> filter,
            Pageable pageable
            ){
        return ResponseEntity.status(HttpStatus.OK).body(roleService.fetchAllRole(filter, pageable));
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<?> getARole(@PathVariable("id") Long id){
        return ResponseEntity.status(HttpStatus.OK).body(roleService.fetchARole(id));
    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<Void> deleteARole(@PathVariable("id") Long id){
        roleService.deleteRole(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
