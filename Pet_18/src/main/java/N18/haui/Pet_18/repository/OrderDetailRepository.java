package N18.haui.Pet_18.repository;


import N18.haui.Pet_18.constant.OrderStatus;
import N18.haui.Pet_18.domain.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long>, JpaSpecificationExecutor<OrderDetail> {

    // Kiểm tra đã mua sản phẩm chưa + đơn đã DELIVERED
    boolean existsByProductIdAndOrderUserIdAndOrderStatus(
            Long productId, String userId, OrderStatus status
    );
}
