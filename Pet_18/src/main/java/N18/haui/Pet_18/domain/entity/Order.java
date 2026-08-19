package N18.haui.Pet_18.domain.entity;


import N18.haui.Pet_18.constant.OrderStatus;
import N18.haui.Pet_18.constant.OrderType;
import N18.haui.Pet_18.domain.dto.common.FlagUserDateAuditing;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_orders")
public class Order extends FlagUserDateAuditing {
    // đặt hàng
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "shipping_name")
    private String shippingName; // tên người nhận
    @Column(name = "shipping_phone")
    private String shippingPhone;
    @Column(name = "shipping_address_full", columnDefinition = "TEXT")
    private String shippingAddressFull;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status; //PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // Thêm cascade cho Payment
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;



    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails;


    @Enumerated(EnumType.STRING)
    @Column(name = "order_type")
    private OrderType orderType = OrderType.PRODUCT;


}
