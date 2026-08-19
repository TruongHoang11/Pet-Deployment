package N18.haui.Pet_18.domain.entity;


import N18.haui.Pet_18.domain.dto.common.DateAuditing;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_order_details")
public class OrderDetail extends DateAuditing {

    //chi tiết đơn hàng
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "unit_price",precision = 10, scale = 2)
    private BigDecimal unitPrice; // gia ban tai thoi diem mua



    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;


    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

}
