package N18.haui.Pet_18.domain.dto.response;


import N18.haui.Pet_18.constant.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusHistoryDto {
    private Long id;
    private OrderStatus status;
    private String note;
    private LocalDateTime changedAt;
}