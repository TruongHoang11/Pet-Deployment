package N18.haui.Pet_18.domain.entity;


import N18.haui.Pet_18.constant.TypeInventory;
import N18.haui.Pet_18.domain.dto.common.UserDateAuditing;
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
@Table(name = "tbl_inventories_transactions")
public class InventoryTransaction extends UserDateAuditing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "quantity")
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TypeInventory type; // export / import

    @Column(name = "note")
    private String note; // ghi chu

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

}
