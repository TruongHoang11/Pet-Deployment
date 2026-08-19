package N18.haui.Pet_18.domain.entity;

import N18.haui.Pet_18.domain.dto.common.UserDateAuditing;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tbl_shipping_addresses")
public class ShippingAddress extends UserDateAuditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String fullName; // Tên người nhận (có thể khác tên chủ tài khoản)

    @Column(nullable = false)
    private String phone; // Số điện thoại nhận hàng

    @Column(nullable = false)
    private String addressDetail; // Số nhà, ngõ, tên đường...

    @Column(name = "ward", nullable = false)
    private String ward; // Phường / Xã

    @Column(name = "district", nullable = false)
    private String district; // Quận / Huyện

    @Column(name = "province", nullable = false)
    private String province; // Tỉnh / Thành phố

    @Column(name = "is_default")
    private Boolean isDefault = false; // Đánh dấu đây có phải địa chỉ mặc định không


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Địa chỉ này thuộc về User nào

}

