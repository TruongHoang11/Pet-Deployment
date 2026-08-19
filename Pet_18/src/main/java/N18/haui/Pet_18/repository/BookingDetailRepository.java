package N18.haui.Pet_18.repository;

import N18.haui.Pet_18.domain.entity.BookingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingDetailRepository extends JpaRepository<BookingDetail, Long> {

    List<BookingDetail> findByBookingId(Long bookingId);

    void deleteByBookingId(Long bookingId);
}
