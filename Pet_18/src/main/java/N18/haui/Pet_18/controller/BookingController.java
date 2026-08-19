package N18.haui.Pet_18.controller;

import N18.haui.Pet_18.base.RestApiV1;
import N18.haui.Pet_18.base.VsResponseUtil;
import N18.haui.Pet_18.constant.UrlConstant;
import N18.haui.Pet_18.domain.dto.request.ReqBookingDateDto;
import N18.haui.Pet_18.domain.dto.request.ReqCreateBooking;
import N18.haui.Pet_18.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestApiV1
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping(UrlConstant.Booking.CREATE_BOOKING)
    public ResponseEntity<?> createBooking(@Valid @RequestBody ReqCreateBooking req) {
        return VsResponseUtil.success(HttpStatus.CREATED, bookingService.createBooking(req));
    }

    @GetMapping(UrlConstant.Booking.GET_BOOKING)
    public ResponseEntity<?> getBooking(@PathVariable Long id) {
        return VsResponseUtil.success(HttpStatus.OK, bookingService.getBookingById(id));
    }

    @GetMapping(UrlConstant.Booking.GET_MY_BOOKINGS)
    public ResponseEntity<?> getMyBookings(Pageable pageable) {
        return VsResponseUtil.success(HttpStatus.OK, bookingService.getMyBookings(pageable));
    }

    @GetMapping(UrlConstant.Booking.GET_BOOKINGS_BY_STATUS)
    public ResponseEntity<?> getBookingsByStatus(
            @RequestParam String status,
            Pageable pageable) {
        return VsResponseUtil.success(HttpStatus.OK, bookingService.getBookingsByStatus(status, pageable));
    }

    @GetMapping(UrlConstant.Booking.GET_ALL_BOOKINGS)
    public ResponseEntity<?> getAllBookings(
            @RequestParam(required = false) List<String> filter,
            Pageable pageable) {
        return VsResponseUtil.success(HttpStatus.OK, bookingService.getAllBookings(filter, pageable));
    }

    // @GetMapping(UrlConstant.Booking.GET_BOOKED_TIMES)
    // public ResponseEntity<?> getOccupiedBookingTimes() {
    // return VsResponseUtil.success(HttpStatus.OK,
    // bookingService.getBookedTimeSlots());
    // }
    @GetMapping(UrlConstant.Booking.GET_BOOKED_TIMES)
    public ResponseEntity<?> getOccupiedBookingTimes(@Valid ReqBookingDateDto reqDto) {
        return VsResponseUtil.success(HttpStatus.OK, bookingService.getBookedTimeSlots(reqDto.getBookingDate()));
    }

    @PatchMapping(UrlConstant.Booking.CANCEL_BOOKING)
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        return VsResponseUtil.success(HttpStatus.OK, bookingService.cancelBooking(id));
    }

    @PatchMapping(UrlConstant.Booking.UPDATE_BOOKING_STATUS)
    public ResponseEntity<?> updateBookingStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return VsResponseUtil.success(HttpStatus.OK, bookingService.updateBookingStatus(id, status));
    }
}
