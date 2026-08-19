package N18.haui.Pet_18.domain.entity;


import java.util.List;

import N18.haui.Pet_18.domain.dto.common.FlagUserDateAuditing;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_menus")
public class Menu extends FlagUserDateAuditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "path")
    private String path;

    private String icon;

    private Integer sortOrder;

    @ManyToOne //NHIỀU menu con thuộc về MỘT menu cha
    @JoinColumn(name = "parent_id")
    private Menu parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Menu> children;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "menu_roles",
        joinColumns = @JoinColumn(name = "menu_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;
}
