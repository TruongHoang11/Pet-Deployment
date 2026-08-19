package N18.haui.Pet_18.domain.entity;


import N18.haui.Pet_18.domain.dto.common.UserDateAuditing;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "tbl_permissions")
@Getter
@Setter
@NoArgsConstructor
public class Permission extends UserDateAuditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Ten Permission khong duoc de trong!")
    private String name;

    private String apiPath;

    private String method;

    private String module;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Role> roles;

    public Permission(String name, String apiPath, String method, String module){
        this.name = name;
        this.apiPath = apiPath;
        this.method = method;
        this.module = module;
    }

}
