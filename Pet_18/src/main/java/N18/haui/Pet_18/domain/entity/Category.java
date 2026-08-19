package N18.haui.Pet_18.domain.entity;

import N18.haui.Pet_18.constant.CategoryType;
import N18.haui.Pet_18.domain.dto.common.FlagUserDateAuditing;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "tbl_categories")
public class Category extends FlagUserDateAuditing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category_type")
    private CategoryType categoryType;


    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private List<Product> products;


    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private List<PetService> petServices;


}