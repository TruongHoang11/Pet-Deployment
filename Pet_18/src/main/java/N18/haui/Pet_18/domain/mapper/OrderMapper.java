package N18.haui.Pet_18.domain.mapper;


import N18.haui.Pet_18.domain.dto.response.OrderDto;
import N18.haui.Pet_18.domain.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
    uses = OrderDetailMapper.class
)
public interface OrderMapper {

// chỉ cần goi uses thì nó tự map List<OrderDetail> thành List<OrderDetailDto> không cần ghi mapping thêm
    @Mapping(target = "paymentStatus", source = "payment.status")
    @Mapping(target = "paymentMethod", source = "payment.paymentMethod")
    OrderDto toDto(Order order);

    List<OrderDto> toDtoList(List<Order> orders);



}
