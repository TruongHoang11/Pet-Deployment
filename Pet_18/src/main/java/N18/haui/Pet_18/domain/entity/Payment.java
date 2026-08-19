package N18.haui.Pet_18.domain.entity;


import N18.haui.Pet_18.constant.PaymentMethod;
import N18.haui.Pet_18.constant.PaymentStatus;
import N18.haui.Pet_18.domain.dto.common.UserDateAuditing;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "tbl_payments")
@Getter
@Setter
public class Payment extends UserDateAuditing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    //mappedBy = "payment" tức là Order là bên giữ FK (payment_id), không phải Payment.
    //Nên không cần set payment.setOrder(order) mà chỉ cần order.setPayment(payment) là đủ để thiết lập quan hệ 1-1.
    // nguyên tắc bên nào giữ FK -> bên đó Set.
    @OneToOne(mappedBy = "payment", fetch = FetchType.LAZY)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod; // VD: MOMO, VNPAY, CASH

    @Column (name = "transaction_id", unique = true)
    private String transactionId; // Lưu mã từ bên thứ 3 trả về

    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PaymentStatus status; // SUCCESS, FAILED, PENDING

}
