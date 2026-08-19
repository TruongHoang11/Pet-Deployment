package N18.haui.Pet_18.service.impl;

import N18.haui.Pet_18.domain.dto.pagination.ResultPaginationDto;
import N18.haui.Pet_18.domain.dto.request.ReqCreateBooking;
import N18.haui.Pet_18.domain.dto.response.BookingDto;
import N18.haui.Pet_18.domain.dto.response.BookingTimeSlotDto;
import N18.haui.Pet_18.service.BookingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
// @RequiredArgsConstructor
@Slf4j
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {


    @Override
    public BookingDto createBooking(ReqCreateBooking req) {
        return null;
    }

    @Override
    public BookingDto getBookingById(Long id) {
        return null;
    }

    @Override
    public ResultPaginationDto getMyBookings(Pageable pageable) {
        return null;
    }

    @Override
    public ResultPaginationDto getBookingsByStatus(String status, Pageable pageable) {
        return null;
    }

    @Override
    public ResultPaginationDto getAllBookings(List<String> filter, Pageable pageable) {
        return null;
    }

    @Override
    public List<BookingTimeSlotDto> getBookedTimeSlots(LocalDate bookingDate) {
        return List.of();
    }

    @Override
    public BookingDto cancelBooking(Long id) {
        return null;
    }

    @Override
    public BookingDto updateBookingStatus(Long id, String status) {
        return null;
    }
}
