package N18.haui.Pet_18.domain.entity;


import N18.haui.Pet_18.domain.dto.common.UserDateAuditing;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_inventories")
public class Inventory extends UserDateAuditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "quantity")
    private Integer quantity;

    @OneToOne //1 Product có đúng 1 Inventory
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "inventory")
    @JsonIgnore
    private List<InventoryTransaction> inventoryTransactions;


}
