package N18.haui.Pet_18.domain.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class CartDto {

    private Long id;
    private List<CartItemDto> itemDtoList; //danh sach item
    private BigDecimal totalAmount; // tổng tiền toàn giỏ
    private Integer totalItem; // tổng số lượng item trong giỏ
}
