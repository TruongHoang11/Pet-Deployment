package N18.haui.Pet_18.domain.entity;

import N18.haui.Pet_18.domain.dto.common.DateAuditing;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "tbl_carts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cart extends DateAuditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mỗi User chỉ có duy nhất 1 giỏ hàng
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;


    // 1 giỏ hàng chứa nhiều item bên trong
    // CascadeType.ALL để khi xóa Cart hoặc thao tác, các item tự động được xử lý theo
    //Thao tác trên Cart tự động áp dụng xuống CartItem
    //Khi xóa item khỏi list → tự động xóa trong DB
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems;
}
