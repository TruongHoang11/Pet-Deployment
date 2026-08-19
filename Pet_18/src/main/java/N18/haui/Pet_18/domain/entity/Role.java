package N18.haui.Pet_18.domain.entity;


import N18.haui.Pet_18.domain.dto.common.FlagUserDateAuditing;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "tbl_roles")
@Getter
@Setter
@NoArgsConstructor
public class Role extends FlagUserDateAuditing implements Serializable {

    public Role(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Ten Role khong duoc de trong!")
    private String name;

    private String description;


    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties("roles")
    @JoinTable(
            name = "tbl_permission_role",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private List<Permission> permissions;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<User> users;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Menu> menus;


    public Role(String roleName, String description, boolean active, List<Permission> permissionList) {
        this.name = roleName;
        this.description = description;
        this.setActiveFlag(active);
        this.permissions = permissionList;
    }

}
