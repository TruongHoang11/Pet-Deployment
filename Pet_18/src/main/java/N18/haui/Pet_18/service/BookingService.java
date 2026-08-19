package N18.haui.Pet_18.service;

import N18.haui.Pet_18.domain.dto.pagination.ResultPaginationDto;
import N18.haui.Pet_18.domain.dto.request.ReqCreateBooking;
import N18.haui.Pet_18.domain.dto.response.BookingDto;
import N18.haui.Pet_18.domain.dto.response.BookingTimeSlotDto;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {

    BookingDto createBooking(ReqCreateBooking req);

    BookingDto getBookingById(Long id);

    ResultPaginationDto getMyBookings(Pageable pageable);

    ResultPaginationDto getBookingsByStatus(String status, Pageable pageable);

    ResultPaginationDto getAllBookings(List<String> filter,Pageable pageable);

//    List<BookingTimeSlotDto> getBookedTimeSlots();
    List<BookingTimeSlotDto> getBookedTimeSlots(LocalDate bookingDate);


    BookingDto cancelBooking(Long id);

    BookingDto updateBookingStatus(Long id, String status);
}
