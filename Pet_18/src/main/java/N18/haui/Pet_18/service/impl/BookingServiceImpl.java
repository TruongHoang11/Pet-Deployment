package N18.haui.Pet_18.service.impl;

import N18.haui.Pet_18.constant.BookingStatus;
import N18.haui.Pet_18.constant.OrderStatus;
import N18.haui.Pet_18.constant.OrderType;
import N18.haui.Pet_18.constant.PaymentStatus;
import N18.haui.Pet_18.domain.dto.pagination.ResultPaginationDto;
import N18.haui.Pet_18.domain.dto.request.ReqCreateBooking;
import N18.haui.Pet_18.domain.dto.response.BookingDto;
import N18.haui.Pet_18.domain.dto.response.BookingTimeSlotDto;
import N18.haui.Pet_18.domain.entity.*;
import N18.haui.Pet_18.domain.mapper.BookingMapper;
import N18.haui.Pet_18.domain.specification.FilterProcessor;
import N18.haui.Pet_18.domain.specification.SpecificationBuilder;
import N18.haui.Pet_18.exception.BadRequestException;
import N18.haui.Pet_18.exception.NotFoundException;
import N18.haui.Pet_18.repository.*;
import N18.haui.Pet_18.service.BookingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
// @RequiredArgsConstructor
@Slf4j
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingDetailRepository bookingDetailRepository;
    private final PetServiceRepository petServiceRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public BookingDto createBooking(ReqCreateBooking req) {
        log.info("[BOOKING] Starting to create booking for user: {}", req.getUserId());

        // ============ VALIDATION 1: Check start time < end time ============
        if (req.getStartTime().isAfter(req.getEndTime())) {
            throw new BadRequestException("[BOOKING] Start time must be before end time");
        }
        LocalDateTime startDateTime = LocalDateTime.of(req.getBookingDate(), req.getStartTime());
        // LocalDateTime endDateTime = LocalDateTime.of(req.getBookingDate(), req.getEndTime());


        // ============ VALIDATION 2: Check if start time and end time are in future ============
        if (startDateTime.isBefore(java.time.LocalDateTime.now())) {
            throw new BadRequestException("[BOOKING] Booking start time cannot be in the past");
        }

        // ============ VALIDATION 3: Check user exists ============
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new NotFoundException("[BOOKING] User not found with ID: " + req.getUserId()));
        log.info("[BOOKING] User found: {}", user.getName());

        // ============ VALIDATION 4: Check all services exist and enabled ============
        List<PetService> services = new ArrayList<>();
        for (Long serviceId : req.getServiceIds()) {
            PetService service = petServiceRepository.findById(serviceId)
                    .orElseThrow(() -> new NotFoundException("[BOOKING] Service not found with ID: " + serviceId));

            if (Boolean.TRUE.equals(service.getDeleteFlag()) || Boolean.FALSE.equals(service.getActiveFlag())) {
                throw new BadRequestException("[BOOKING] Service is disabled: " + service.getName());
            }

            services.add(service);
        }


        BigDecimal totalPrice = BigDecimal.ZERO;
        for (PetService service : services) {
            totalPrice = totalPrice.add(service.getBasePrice());
        }
        log.info("[BOOKING] All services validated. Total services: {}", services.size());

        // ============ VALIDATION 5: Check total duration ============
        long totalDurationMin = services.stream()
                .mapToLong(s -> s.getDurationMin())
                .sum();

        Duration availableDuration = Duration.between(req.getStartTime(), req.getEndTime());
        long availableMinutes = availableDuration.toMinutes();

        if (totalDurationMin > availableMinutes) {
            throw new BadRequestException(
                    String.format("[BOOKING] Total service duration (%d min) exceeds available time window (%d min)",
                            totalDurationMin, availableMinutes)
            );
        }
        log.info("[BOOKING] Duration validation passed. Total: {} min, Available: {} min",
                totalDurationMin, availableMinutes);

        // ============ VALIDATION 6: Check overlapping bookings ============
        List<Booking> existingBookings = bookingRepository.findByBookingDateAndStatusNot(req.getBookingDate(), BookingStatus.CANCELLED);
        for (Booking existing : existingBookings) {
            // Hai lịch chỉ xung đột khi phần thời gian bên trong thực sự giao nhau.
            // Các lịch liền kề (08:00-09:00 và 09:00-10:00) vẫn hợp lệ.
            boolean overlaps = req.getStartTime().isBefore(existing.getEndTime())
                    && req.getEndTime().isAfter(existing.getStartTime());

            if (overlaps) {
                throw new BadRequestException(
                        String.format("[BOOKING] Requested time %s-%s conflicts with existing booking %s-%s",
                                req.getStartTime(), req.getEndTime(), existing.getStartTime(), existing.getEndTime())
                );
            }
        }

        // ============ CREATE BOOKING ============
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setBookingDate(req.getBookingDate());
        booking.setStartTime(req.getStartTime());
        booking.setActualPrice(totalPrice);
        booking.setEndTime(req.getEndTime());
        booking.setStatus(BookingStatus.PENDING);  // Set initial status as PENDING
        if (req.getPetId() != null) {
            // Validate pet if provided
            Pet pet = petRepository.findById(req.getPetId())
                    .orElseThrow(() -> new NotFoundException("[BOOKING] Pet not found with ID: " + req.getPetId()));
            booking.setPet(pet);  // Will be set if pet service supports it
        }

        Booking savedBooking = bookingRepository.save(booking);
        log.info("[BOOKING] Booking created with ID: {}", savedBooking.getId());

        // ============ CREATE BOOKING DETAILS ============
        List<BookingDetail> bookingDetails = new ArrayList<>();
        for (PetService service : services) {
            BookingDetail detail = BookingDetail.builder()
                    .booking(savedBooking)
                    .service(service)
                    .build();
            bookingDetails.add(detail);
        }

        bookingDetailRepository.saveAll(bookingDetails);
        log.info("[BOOKING] Booking details saved. Count: {}", bookingDetails.size());

        // ============ CREATE ORDER + PAYMENT ============
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmount(totalPrice);

        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(totalPrice);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderType(OrderType.BOOKING);
        order.setPayment(payment);
        order.setOrderDetails(new ArrayList<>()); // Booking orders do not contain product order details.

        orderRepository.save(order);
        log.info("[BOOKING] Linked order created | Order ID: {}", order.getId());

        savedBooking.setOrder(order);
        bookingRepository.save(savedBooking);

// ============ RETURN RESPONSE ============
        savedBooking.setBookingDetails(bookingDetails);
        BookingDto response = bookingMapper.toDto(savedBooking);
        log.info("[BOOKING] Booking created successfully with ID: {}", savedBooking.getId());

        return response;

    }

    @Override
    public BookingDto getBookingById(Long id) {
        log.info("[BOOKING] Getting booking with ID: {}", id);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[BOOKING] Booking not found with ID: " + id));

        return bookingMapper.toDto(booking);
    }

    // @Override
    // public List<BookingTimeSlotDto> getBookedTimeSlots() {
    //     log.info("[BOOKING] Getting occupied booking times");
    //     List<Booking> bookings = bookingRepository.findByStatusNotAndDeleteFlagFalseAndActiveFlagTrue(BookingStatus.CANCELLED);
    //     return bookings.stream()
    //             .map(booking -> new BookingTimeSlotDto(booking.getStartTime(), booking.getEndTime()))
    //             .sorted(Comparator.comparing(BookingTimeSlotDto::getStartTime))
    //             .toList();
    // }
    @Override
    public List<BookingTimeSlotDto> getBookedTimeSlots(LocalDate bookingDate) {
        log.info("[BOOKING] Getting occupied booking times for date: {}", bookingDate);

        List<Booking> bookings = bookingRepository.findByBookingDateAndStatusNot(bookingDate, BookingStatus.CANCELLED);

        return bookings.stream()
                .map(booking -> new BookingTimeSlotDto(booking.getStartTime(), booking.getEndTime()))
                .sorted(Comparator.comparing(BookingTimeSlotDto::getStartTime))
                .toList();
    }

    @Override
    public ResultPaginationDto getMyBookings(Pageable pageable) {
        log.info("[BOOKING] Getting my bookings with pagination");

        // Get current logged-in user id from security context
        Optional<String> currentUser = N18.haui.Pet_18.security.SecurityUtil.getCurrentUserLogin();
        String userId = currentUser.orElseThrow(() -> new BadRequestException("[BOOKING] Current user not authenticated"));

        Page<Booking> page = bookingRepository.findByUserId(userId, pageable);
        List<BookingDto> dtos = page.getContent().stream()
                .map(bookingMapper::toDto)
                .toList();

        return buildPaginationResponse(dtos, page);
    }

    @Override
    public ResultPaginationDto getBookingsByStatus(String status, Pageable pageable) {
        log.info("[BOOKING] Getting bookings by status: {}", status);

        try {
            BookingStatus bookingStatus = BookingStatus.valueOf(status.toUpperCase());
            Page<Booking> page = bookingRepository.findByStatus(bookingStatus, pageable);
            List<BookingDto> dtos = page.getContent().stream()
                    .map(bookingMapper::toDto)
                    .toList();

            return buildPaginationResponse(dtos, page);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("[BOOKING] Invalid status: " + status);
        }
    }

    @Override
    public ResultPaginationDto getAllBookings(List<String> filter, Pageable pageable) {
        log.info("[BOOKING] Getting all bookings with pagination");
        SpecificationBuilder<Booking> specificationBuilder = new SpecificationBuilder<>();
        FilterProcessor.process(specificationBuilder, filter);

        Specification<Booking> spec = specificationBuilder.build();
        Specification<Booking> softDelete = (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("deleteFlag"), false);
        };
        Specification<Booking> finalSpec = spec == null ? softDelete : spec.and(softDelete);

        Page<Booking> page = bookingRepository.findAll(finalSpec, pageable);
        List<BookingDto> dtos = page.getContent().stream()
                .map(bookingMapper::toDto)
                .toList();

        return buildPaginationResponse(dtos, page);
    }

    @Override
    @Transactional
    public BookingDto cancelBooking(Long id) {
        log.info("[BOOKING] Canceling booking with ID: {}", id);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[BOOKING] Booking not found with ID: " + id));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("[BOOKING] Booking is already cancelled");
        }

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new BadRequestException("[BOOKING] Cannot cancel completed booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking updatedBooking = bookingRepository.save(booking);
        log.info("[BOOKING] Booking cancelled successfully");

        return bookingMapper.toDto(updatedBooking);
    }

    @Override
    @Transactional
    public BookingDto updateBookingStatus(Long id, String status) {
        log.info("[BOOKING] Updating booking status with ID: {} to {}", id, status);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[BOOKING] Booking not found with ID: " + id));

        try {
            BookingStatus newStatus = BookingStatus.valueOf(status.toUpperCase());
            // If confirming the booking, re-run time validations similar to createBooking
            if (newStatus == BookingStatus.CONFIRMED) {
                validateBookingForConfirmation(booking);
            }

            booking.setStatus(newStatus);
            Booking updatedBooking = bookingRepository.save(booking);
            log.info("[BOOKING] Booking status updated successfully");

            return bookingMapper.toDto(updatedBooking);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("[BOOKING] Invalid status: " + status);
        }
    }

    private <T> ResultPaginationDto buildPaginationResponse(List<T> data, Page<?> page) {
        ResultPaginationDto response = new ResultPaginationDto();
        response.setResult(data);

        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();
        meta.setPage(page.getNumber() + 1);
        meta.setPageSize(page.getSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());
        response.setMeta(meta);

        return response;
    }

    /**
     * Validate booking details and times before confirming an existing booking.
     */
    private void validateBookingForConfirmation(Booking booking) {
        if (booking.getStartTime().isAfter(booking.getEndTime())) {
            throw new BadRequestException("[BOOKING] Start time must be before end time");
        }

        LocalDateTime startDateTime = LocalDateTime.of(booking.getBookingDate(), booking.getStartTime());
        if (startDateTime.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("[BOOKING] Booking start time cannot be in the past");
        }

        // Collect services from booking details
        List<PetService> services = new ArrayList<>();
        if (booking.getBookingDetails() == null || booking.getBookingDetails().isEmpty()) {
            throw new BadRequestException("[BOOKING] Booking must contain at least one service");
        }

        for (BookingDetail detail : booking.getBookingDetails()) {
            PetService service = detail.getService();
            if (service == null) {
                throw new NotFoundException("[BOOKING] Booking service not found in booking details");
            }

            if (Boolean.TRUE.equals(service.getDeleteFlag()) || Boolean.FALSE.equals(service.getActiveFlag())) {
                throw new BadRequestException("[BOOKING] Service is disabled: " + service.getName());
            }

            services.add(service);
        }

        long totalDurationMin = services.stream()
                .mapToLong(s -> s.getDurationMin())
                .sum();

        Duration availableDuration = Duration.between(booking.getStartTime(), booking.getEndTime());
        long availableMinutes = availableDuration.toMinutes();

        if (totalDurationMin > availableMinutes) {
            throw new BadRequestException(
                    String.format("[BOOKING] Total service duration (%d min) exceeds available time window (%d min)",
                            totalDurationMin, availableMinutes)
            );
        }

        // Overlap check: exclude this booking itself
        List<Booking> existingBookings = bookingRepository.findByBookingDateAndStatusNot(booking.getBookingDate(), BookingStatus.CANCELLED);
        for (Booking existing : existingBookings) {
            if (existing.getId().equals(booking.getId())) continue;

            boolean overlaps = booking.getStartTime().isBefore(existing.getEndTime())
                    && booking.getEndTime().isAfter(existing.getStartTime());

            if (overlaps) {
                throw new BadRequestException(
                        String.format("[BOOKING] Requested time %s-%s conflicts with existing booking %s-%s",
                                booking.getStartTime(), booking.getEndTime(), existing.getStartTime(), existing.getEndTime())
                );
            }
        }
    }
}
