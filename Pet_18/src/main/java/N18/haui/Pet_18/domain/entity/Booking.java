package N18.haui.Pet_18.domain.entity;

import N18.haui.Pet_18.constant.BookingStatus;
import N18.haui.Pet_18.domain.dto.common.FlagUserDateAuditing;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_bookings")
public class Booking extends FlagUserDateAuditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingStatus status;

    @Column(name = "actual_price",precision = 10, scale = 2)
    private BigDecimal actualPrice; // gia thuc te thu cua khạc

    @Column(name = "booking_date")
    private LocalDate bookingDate; // Ngày khách hẹn, có thể dùng để


    @Column(name = "start_time")
    private LocalTime startTime; // Khách hẹn lúc mấy giờ, ngày nào

    @Column(name = "end_time")
    private LocalTime endTime; // Dự kiến xong lúc mấy giờ để hệ thống chặn không cho người khác đặt trùng

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // Khách hàng đặt lịch

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @OneToMany(mappedBy = "booking")
    @JsonIgnore
    private List<BookingDetail> bookingDetails; // Danh sách dịch vụ trong lịch hẹn đó

    // liên kết với Order để thanh toán
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
}
