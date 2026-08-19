package N18.haui.Pet_18.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingTimeSlotDto {

    private LocalTime startTime;
    private LocalTime endTime;
}
