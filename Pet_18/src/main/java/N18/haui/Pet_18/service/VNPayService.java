package N18.haui.Pet_18.service;

import N18.haui.Pet_18.configuration.VNPayConfig;
import N18.haui.Pet_18.constant.*;
import N18.haui.Pet_18.domain.dto.response.PaymentStatusDto;
import N18.haui.Pet_18.domain.entity.*;
import N18.haui.Pet_18.exception.BadRequestException;
import N18.haui.Pet_18.exception.ForbiddenException;
import N18.haui.Pet_18.exception.NotFoundException;
import N18.haui.Pet_18.repository.*;
import N18.haui.Pet_18.util.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VNPayService {

    private final VNPayConfig vnPayConfig;
    private final VNPayUtil vnPayUtil;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;

    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;

    public String createPaymentUrl(Long orderId, HttpServletRequest request) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new NotFoundException("Not found order with id: " + orderId));

        if (!order.getStatus().equals(OrderStatus.PENDING)) {
            throw new BadRequestException("Order status is not in PENDING status");
        }

        String vnpTxnRef = orderId + "_" + System.currentTimeMillis();

        String vnpAmount = String.valueOf(
                order.getTotalAmount()
                        .multiply(new BigDecimal("100"))
                        .longValue()
        );

        Map<String, String> vnpParams = new TreeMap<>();

        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", vnPayConfig.getTmnCode());
        vnpParams.put("vnp_Amount", vnpAmount);
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", vnpTxnRef);

        // Không nên dùng tiếng Việt có dấu
        vnpParams.put("vnp_OrderInfo", "Order_" + orderId);

        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Locale", "vn");

        vnpParams.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());

        vnpParams.put("vnp_IpAddr", vnPayUtil.getCurrentIp(request));

        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        vnpParams.put("vnp_CreateDate", now.format(formatter));

        vnpParams.put(
                "vnp_ExpireDate",
                now.plusMinutes(15).format(formatter)
        );

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        Iterator<Map.Entry<String, String>> itr =
                vnpParams.entrySet().iterator();

        while (itr.hasNext()) {

            Map.Entry<String, String> entry = itr.next();

            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();

            if (fieldValue != null && !fieldValue.isEmpty()) {

                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(
                        URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII)
                );

                query.append(
                        URLEncoder.encode(fieldName, StandardCharsets.US_ASCII)
                );
                query.append('=');
                query.append(
                        URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII)
                );

                if (itr.hasNext()) {
                    hashData.append('&');
                    query.append('&');
                }
            }
        }

        String secureHash = vnPayUtil.hmacSHA512(
                vnPayConfig.getHashSecret(),
                hashData.toString()
        );

        log.info("HASH DATA = {}", hashData);
        log.info("QUERY = {}", query);
        log.info("SECURE HASH = {}", secureHash);

        return vnPayConfig.getUrl()
                + "?"
                + query
                + "&vnp_SecureHash="
                + secureHash;
    }


// Xử lý callback từ VNPAY
    @Transactional
    public String handleReturn(HttpServletRequest request) {
        Map<String, String> params = new TreeMap<>();
        request.getParameterMap().forEach((k, v) -> {
            // Chỉ lấy các tham số bắt đầu bằng vnp_ và bỏ qua chữ ký bảo mật
            if (k != null && k.startsWith("vnp_") && !k.equals("vnp_SecureHash") && !k.equals("vnp_SecureHashType")) {
                if (v != null && v.length > 0 && v[0] != null && !v[0].isEmpty()) {
                    params.put(k, v[0]);
                }
            }
        });

        // 1. Tạo chuỗi raw dữ liệu từ VNPAY gửi về (KHÔNG dùng URLEncoder)
        String hashData = params.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));

        String secureHash = request.getParameter("vnp_SecureHash");

        // 2. Tính toán lại mã băm dựa trên hashSecret của bạn
        String computedHash = vnPayUtil.hmacSHA512(vnPayConfig.getHashSecret(), hashData);

        // 3. So sánh chữ ký của VNPAY gửi sang với chữ ký tự tính
        if (!computedHash.equals(secureHash)) {
            log.warn("[VNPAY] Chữ ký không hợp lệ | Computed: {} | Received: {}", computedHash, secureHash);
            return "INVALID_SIGNATURE";
        }

        String responseCode = request.getParameter("vnp_ResponseCode");

        String txnRef = request.getParameter("vnp_TxnRef");

        Long orderId = Long.parseLong(txnRef.split("_")[0]);

        String transactionId = request.getParameter("vnp_TransactionNo");

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đơn hàng"));

        if (order.getPayment().getStatus() != PaymentStatus.PENDING) {
            log.warn("[VNPAY] Đơn hàng đã được xử lý trước đó | Order ID: {}", orderId);
            return order.getPayment().getStatus() == PaymentStatus.SUCCESS ? "SUCCESS" : "FAILED";
        }

        boolean isSuccess = "00".equals(responseCode);
        boolean stockShortage = false; //cờ đánh dấu thiếu hàng
        if (isSuccess) {
            // Thanh toán thành công
            order.getPayment().setStatus(PaymentStatus.SUCCESS);
            order.getPayment().setTransactionId(transactionId);
            order.getPayment().setPaymentMethod(PaymentMethod.VNPAY);
            order.setStatus(OrderStatus.PROCESSING);

            if (order.getOrderType() == OrderType.PRODUCT) {
                for (OrderDetail orderDetail : order.getOrderDetails()) {
                    Inventory inventory = inventoryRepository.findByProductId(orderDetail.getProduct().getId()).orElseThrow(
                            () -> new NotFoundException("[ORDER] Sản phẩm không tồn tại trong kho")
                    );
                    Integer oldQuantity = inventory.getQuantity();
                    if (oldQuantity < orderDetail.getQuantity()) {
                        //  Không throw - chỉ đánh dấu và dừng vòng lặp
                        stockShortage = true;
                        log.warn("[VNPAY] Tồn kho không đủ | Product ID: {} | Order ID: {}",
                                orderDetail.getProduct().getId(), orderId);
                        break;
                    }

                    Integer newQuantity = oldQuantity - orderDetail.getQuantity();
                    inventory.setQuantity(newQuantity);
                    inventoryRepository.save(inventory);

                    InventoryTransaction inventoryTransaction = new InventoryTransaction();
                    inventoryTransaction.setInventory(inventory);
                    inventoryTransaction.setQuantity(orderDetail.getQuantity());
                    inventoryTransaction.setType(TypeInventory.EXPORT);
                    inventoryTransaction.setNote("Export product to order after successful payment");
                    inventoryTransactionRepository.save(inventoryTransaction);
                }
            }
            if (stockShortage) {
                order.setStatus(OrderStatus.CANCELLED);
                order.getPayment().setStatus(PaymentStatus.REFUNDED);
                log.warn("[VNPAY] Đơn hàng ID: {} bị hủy do thiếu tồn kho, cần Admin hoàn tiền thủ công", orderId);
            } else {
                log.info("[VNPAY] Thanh toán thành công | Order ID: {}", orderId);
            }
        } else {
            // Thanh toán thất bại
            order.getPayment().setStatus(PaymentStatus.FAILED);
            log.warn("[VNPAY] Thanh toán thất bại | Order ID: {} | Code: {}", orderId, responseCode);
        }
        orderRepository.save(order);

        if (order.getOrderType() == OrderType.BOOKING) {
            boolean bookingConfirmed = isSuccess; // booking không bị ảnh hưởng bởi stockShortage
            bookingRepository.findByOrderId(order.getId()).ifPresent(booking -> {
                if (bookingConfirmed) {
                    booking.setStatus(BookingStatus.CONFIRMED);
                    log.info("[VNPAY] Booking ID: {} đã được xác nhận", booking.getId());
                } else {
                    booking.setStatus(BookingStatus.CANCELLED);
                    log.warn("[VNPAY] Booking ID: {} bị hủy do thanh toán thất bại", booking.getId());
                }
                bookingRepository.save(booking);
            });
        }

        if (stockShortage) {
            return "STOCK_SHORTAGE";
        }
        return isSuccess ? "SUCCESS" : "FAILED";
    }


    public PaymentStatusDto getPaymentStatus(Long orderId) {
        log.info("[PAYMENT] Tra cứu trạng thái thanh toán | Order ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đơn hàng ID: " + orderId));

        // Check owner - tránh user A xem được đơn của user B
        User currentUser = userService.getUserLogin();
        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You are not allowed to perform this action");
        }

        PaymentStatusDto dto = new PaymentStatusDto();
        dto.setOrderId(order.getId());
        dto.setOrderStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setTransactionId(order.getPayment() != null ? order.getPayment().getTransactionId() : null);

        if (order.getPayment() != null) {
            dto.setPaymentStatus(order.getPayment().getStatus());
            dto.setTransactionId(order.getPayment().getTransactionId());
            dto.setPaymentMethod(order.getPayment().getPaymentMethod().name());
        }

        return dto;
    }
}