package N18.haui.Pet_18.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BookingDetailDto {

    private Long id;
    private Long bookingId;
    private Long serviceId;
    private String serviceName;
    private BigDecimal servicePrice;
    private Integer serviceDuration;  // in minutes
}
