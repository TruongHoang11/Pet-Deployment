package N18.haui.Pet_18.domain.mapper;


import N18.haui.Pet_18.domain.dto.response.InventoryDto;
import N18.haui.Pet_18.domain.entity.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(target="productId", source = "product.id")
    @Mapping(target="productName", source = "product.name")
    @Mapping(target="productPrice", source = "product.price")
    InventoryDto toDto(Inventory inventory);

    List<InventoryDto> toListInventory(List<Inventory> inventoryList);


}
