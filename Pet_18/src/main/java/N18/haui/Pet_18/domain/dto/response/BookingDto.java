package N18.haui.Pet_18.domain.dto.response;

import N18.haui.Pet_18.constant.BookingStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class BookingDto {

    private Long id;
    private String userId;
    private String userName;
    private BookingStatus status;
    private BigDecimal actualPrice;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long petId;
    private String petName;
    private List<BookingDetailDto> bookingDetails;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    private Long orderId;
}
