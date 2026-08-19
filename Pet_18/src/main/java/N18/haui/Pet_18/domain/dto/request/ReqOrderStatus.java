package N18.haui.Pet_18.domain.dto.request;

import N18.haui.Pet_18.constant.OrderStatus;
import N18.haui.Pet_18.validator.annotation.EnumValue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqOrderStatus {


    @EnumValue(name = "status", enumClass = OrderStatus.class)
    private String status;


}
