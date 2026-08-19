package N18.haui.Pet_18.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReqBookingDateDto {

    @NotNull(message = "Booking date is required")
    private LocalDate bookingDate;
}
