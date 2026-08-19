package N18.haui.Pet_18.repository;


import N18.haui.Pet_18.constant.OrderStatus;
import N18.haui.Pet_18.constant.PaymentMethod;
import N18.haui.Pet_18.domain.entity.Order;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    Page<Order> findByUserIdAndStatus(String userId, OrderStatus status, Pageable pageable);

    Page<Order> findByUserId(String userId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.payment.paymentMethod = :paymentMethod AND o.createdDate < :threshold")
    List<Order> findByStatusAndPaymentMethodAndCreatedDateBefore(
            @Param("status") OrderStatus status, @Param("paymentMethod") PaymentMethod paymentMethod, @Param("threshold") LocalDateTime threshold
    );
}