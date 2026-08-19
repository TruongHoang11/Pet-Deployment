package N18.haui.Pet_18.repository;

import N18.haui.Pet_18.constant.BookingStatus;
import N18.haui.Pet_18.domain.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {

    Page<Booking> findByUserId(String userId, Pageable pageable);

    Page<Booking> findByUserIdAndStatus(String userId, BookingStatus status, Pageable pageable);

    List<Booking> findByBookingDateAndStatusNot(LocalDate bookingDate, BookingStatus status);

    List<Booking> findByStatusNotAndDeleteFlagFalseAndActiveFlagTrue(BookingStatus status);

    List<Booking> findByStartTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    Page<Booking> findByStatus(BookingStatus status, Pageable pageable);

    Long countByStatus(BookingStatus status);

    Optional<Booking> findByOrderId(Long orderId);
}
