package N18.haui.Pet_18.util.scheduled;

import N18.haui.Pet_18.constant.*;
import N18.haui.Pet_18.domain.entity.Order;
import N18.haui.Pet_18.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderExpirationScheduler {

    private final OrderRepository orderRepository;

    // Chạy mỗi 1 phút
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cancelExpiredPendingOrders() {
        LocalDateTime expiredThreshold = LocalDateTime.now().minusMinutes(15);

        // Chỉ xử lý đơn VNPAY (COD không có khái niệm "hết hạn thanh toán online")
        List<Order> expiredOrders = orderRepository
            .findByStatusAndPaymentMethodAndCreatedDateBefore(
                    OrderStatus.PENDING, PaymentMethod.VNPAY, expiredThreshold)
            .stream()
            .filter(order -> order.getPayment() != null
                && order.getPayment().getPaymentMethod() == PaymentMethod.VNPAY)
            .toList();

        if (expiredOrders.isEmpty()) {
            return;
        }

        log.info("[SCHEDULER] Tìm thấy {} đơn VNPAY quá hạn thanh toán", expiredOrders.size());

        for (Order order : expiredOrders) {
            order.setStatus(OrderStatus.CANCELLED);
            order.getPayment().setStatus(PaymentStatus.FAILED);

            orderRepository.save(order);
            log.info("[SCHEDULER] Đã hủy đơn quá hạn | Order ID: {}", order.getId());
        }
    }
}