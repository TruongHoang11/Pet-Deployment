package N18.haui.Pet_18.domain.mapper;


import N18.haui.Pet_18.domain.dto.response.InventoryTransactionDto;
import N18.haui.Pet_18.domain.entity.InventoryTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InventoryTransactionMapper {
    @Mapping(target = "currentStock", source = "inventory.quantity")
    @Mapping(target = "productName", source = "inventory.product.name")
    @Mapping(target = "productId", source = "inventory.product.id")
    InventoryTransactionDto toInventoryTransactionDto(InventoryTransaction inventoryTransaction);

    List<InventoryTransactionDto> toListInventoryTransaction(List<InventoryTransaction> inventoryTransactions);

}
