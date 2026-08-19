package N18.haui.Pet_18.service.impl;

import N18.haui.Pet_18.constant.*;
import N18.haui.Pet_18.domain.dto.pagination.ResultPaginationDto;
import N18.haui.Pet_18.domain.dto.request.ReqCreateOrderBuyNow;
import N18.haui.Pet_18.domain.dto.request.ReqCreateOrderFromCart;
import N18.haui.Pet_18.domain.dto.request.ReqOrderStatus;
import N18.haui.Pet_18.domain.dto.request.ReqUpdateOrderStatus;
import N18.haui.Pet_18.domain.dto.response.OrderDto;
import N18.haui.Pet_18.domain.entity.*;
import N18.haui.Pet_18.domain.mapper.OrderDetailMapper;
import N18.haui.Pet_18.domain.mapper.OrderMapper;
import N18.haui.Pet_18.domain.mapper.ShippingAddressMapper;
import N18.haui.Pet_18.domain.specification.FilterProcessor;
import N18.haui.Pet_18.domain.specification.SpecificationBuilder;
import N18.haui.Pet_18.exception.BadRequestException;
import N18.haui.Pet_18.exception.ForbiddenException;
import N18.haui.Pet_18.exception.NotFoundException;
import N18.haui.Pet_18.repository.*;
import N18.haui.Pet_18.service.OrderService;
import N18.haui.Pet_18.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static N18.haui.Pet_18.constant.OrderStatus.CANCELLED;
import static N18.haui.Pet_18.constant.OrderStatus.DELIVERED;
import static N18.haui.Pet_18.constant.PaymentMethod.COD;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final OrderMapper orderMapper;
    private final CartRepository cartRepository;
    private final ShippingAddressRepository shippingAddressRepository;
    private final InventoryRepository inventoryRepository;
    private final ShippingAddressMapper shippingAddressMapper;


    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final ProductRepository productRepository;
    private final OrderDetailMapper orderDetailMapper;

    @Override
    @Transactional
    public OrderDto createOrderFromCart(ReqCreateOrderFromCart req) {
        log.info("[ORDER] Bắt đầu tạo đơn hàng từ giỏ hàng");

        // 1. Lấy user hiện tại
        User currentUser = userService.getUserLogin();

        // Lấy giỏ hàng của người dùng
        Cart cart = cartRepository.findByUserId(currentUser.getId()).orElseThrow(
                () -> new NotFoundException("[ORDER] Bạn không có giỏ hàng nào")
        );


        List<CartItem> selectedItems = cart.getCartItems().stream()
                .filter(item -> req.getCartItemIds().contains(item.getId()))
                .toList();

        if (selectedItems.isEmpty()) {
            throw new BadRequestException("[ORDER] Bạn chưa chọn sản phẩm nào");
        }

        // 3. Lấy địa chỉ giao hàng
        ShippingAddress shippingAddress = shippingAddressRepository.findById(req.getAddressId()).orElseThrow(
                () -> new NotFoundException("[ORDER] Địa chỉ giao hàng không tồn tại")
        );
        if (!shippingAddress.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("[ORDER] Bạn không có quyền thao tác với địa chỉ giao hàng này");
        }


        // 4. Kiểm tra tồn kho từng sản phẩm
        for (CartItem item : selectedItems) {
            Inventory inventory = inventoryRepository.findByProductId(item.getProduct().getId()).orElseThrow(
                    () -> new NotFoundException("[ORDER] Sản phẩm không tồn tại")
            );
            if (inventory.getQuantity() < item.getQuantity()) {
                throw new NotFoundException("[ORDER] Số lượng sản phẩm trong kho không đủ");
            }
        }

        // 5. Tạo Order
        Order order = new Order();
        order.setUser(currentUser);
        order.setShippingName(shippingAddress.getFullName());
        order.setShippingPhone(shippingAddress.getPhone());
        order.setShippingAddressFull(shippingAddressMapper.buildFullAddress(shippingAddress));
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDetails(new ArrayList<>());

        // 6. Tạo OrderDetail + tính totalAmount
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem item : selectedItems) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(item.getProduct());
            orderDetail.setQuantity(item.getQuantity());
            orderDetail.setUnitPrice(item.getProduct().getPrice());

            order.getOrderDetails().add(orderDetail);


            totalAmount = totalAmount.add(
                    item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity()))
            );
        }
        order.setTotalAmount(totalAmount);

        // 7. Tạo Payment
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(req.getPaymentMethod());
        payment.setAmount(totalAmount);
        payment.setStatus(PaymentStatus.PENDING);

        order.setPayment(payment);



        // 9. Save Order → cascade tự save OrderDetail + StatusHistory + Payment
        orderRepository.save(order);

        //tru ton kho
        deductInventoryIfCod(order);

        // 11. Xóa giỏ hàng
        cart.getCartItems().removeAll(selectedItems);
        cartRepository.save(cart);
        log.info("[CART] Xóa {} item đã mua khỏi giỏ hàng", selectedItems.size());

        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto createOrderFromBuyNow(ReqCreateOrderBuyNow req) {
        log.info("[ORDER] Bắt đầu tạo đơn hàng mua ngay | Product ID: {}", req.getProductId());

        // Lấy user hiện tại
        User curentUser = userService.getUserLogin();

        // Kiểm tra product tồn tại
        Product product = productRepository.findById(req.getProductId()).orElseThrow(
                () -> new NotFoundException("[ORDER] Sản phẩm không tồn tại")
        );

        //Validate quantity
        if (req.getQuantity() <= 0) {
            throw new BadRequestException("[ORDER] Số lượng phải lớn hơn 0");
        }


        //Kiểm tra tồn kho
        Inventory inventory = inventoryRepository.findByProductId(product.getId()).orElseThrow(
                () -> new NotFoundException("[ORDER] Sản phẩm không tồn tại")
        );

        if (inventory.getQuantity() < req.getQuantity()) {
            throw new NotFoundException("[ORDER] Số lượng sản phẩm trong kho không đủ");
        }

        //Lấy địa chỉ giao hàng
        ShippingAddress shippingAddress = shippingAddressRepository.findById(req.getAddressId()).orElseThrow(
                () -> new NotFoundException("[ORDER] Địa chỉ giao hàng không tồn tại")
        );

        if (!shippingAddress.getUser().getId().equals(curentUser.getId())) {
            throw new ForbiddenException("[ORDER] Bạn không có quyền thao tác với địa chỉ giao hàng này");
        }


        //Tính totalAmount
        BigDecimal totalAmount = product.getPrice()
                .multiply(new BigDecimal(req.getQuantity()));

        //Tạo Payment
        Payment payment = new Payment();
        //    payment.setOrder(order); payment không cần set order vì
        //    // nguyên tắc bên nào giữ FK -> bên đó Set.
        //    // 1-11-1. Payment là bên không giữ FK, nên chỉ cần set order.setPayment(payment)
        //    là đủ để thiết lập quan hệ
        payment.setPaymentMethod(req.getPaymentMethod());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmount(totalAmount);

        //Tạo Order
        Order order = new Order();
        order.setUser(curentUser);
        order.setShippingName(shippingAddress.getFullName());
        order.setShippingPhone(shippingAddress.getPhone());
        order.setShippingAddressFull(shippingAddressMapper.buildFullAddress(shippingAddress));
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(totalAmount);
        order.setPayment(payment);
        order.setOrderDetails(new ArrayList<>());

        order.setPayment(payment);

        //Tạo OrderDetail
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setProduct(product);
        orderDetail.setOrder(order);
        orderDetail.setQuantity(req.getQuantity());
        orderDetail.setUnitPrice(product.getPrice());
        order.getOrderDetails().add(orderDetail);



        // 11. Save Order → cascade tự save OrderDetail + StatusHistory + Payment
        orderRepository.save(order);
        log.info("[ORDER] Tạo đơn hàng mua ngay thành công | Order ID: {}", order.getId());
        deductInventoryIfCod(order);

        return orderMapper.toDto(order);
    }

    // Helper - trừ kho nếu là COD
    private void deductInventoryIfCod(Order order) {
        if (!COD.equals(order.getPayment().getPaymentMethod())) {
            return; // VNPay → không trừ ở đây, trừ ở handleReturn
        }

        for (OrderDetail orderDetail : order.getOrderDetails()) {
            Inventory inventory = inventoryRepository.findByProductId(orderDetail.getProduct().getId())
                    .orElseThrow(() -> new NotFoundException("[ORDER] Sản phẩm không tồn tại trong kho"));

            Integer oldQuantity = inventory.getQuantity();
            Integer newQuantity = oldQuantity - orderDetail.getQuantity();
            inventory.setQuantity(newQuantity);
            inventoryRepository.save(inventory);

            InventoryTransaction inventoryTransaction = new InventoryTransaction();
            inventoryTransaction.setInventory(inventory);
            inventoryTransaction.setQuantity(orderDetail.getQuantity());
            inventoryTransaction.setType(TypeInventory.EXPORT);
            inventoryTransaction.setNote("Export product to order (COD)");
            inventoryTransactionRepository.save(inventoryTransaction);
        }

        log.info("[ORDER] Đã trừ kho cho đơn COD | Order ID: {}", order.getId());
    }

    @Override
    public ResultPaginationDto getMyOrders(ReqOrderStatus orderStatus, Pageable pageable) {
        log.info("[ORDER] Lấy danh sách đơn hàng | Status: {}", orderStatus);
        User currentUser = userService.getUserLogin();

            OrderStatus status = orderStatus.getStatus() != null
                    ? OrderStatus.valueOf(orderStatus.getStatus().toUpperCase().trim())
                    : null;



        pageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("createdDate").descending());
        if (status != null && !EnumSet.allOf(OrderStatus.class).contains(status)) {
            throw new BadRequestException("[ORDER] Status không hợp lệ");
        }

        Page<Order> pageOrder = status != null
                ? orderRepository.findByUserIdAndStatus(currentUser.getId(), status, pageable)
                : orderRepository.findByUserId(currentUser.getId(), pageable);

        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageOrder.getTotalPages());
        meta.setTotal(pageOrder.getTotalElements());

        ResultPaginationDto data = new ResultPaginationDto();
        data.setMeta(meta);

        List<OrderDto> orderDtoList = orderMapper.toDtoList(pageOrder.getContent());

        data.setResult(orderDtoList);

        log.info("[ORDER] User ID: {} | Status: {} | Tổng: {}",
                currentUser.getId(), status, pageOrder.getTotalElements());
        return data;
    }

    @Override
    public OrderDto getOrderDetail(Long orderId) {
        log.info("[ORDER] Xem chi tiết đơn hàng ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("[NOT_FOUND] Không tìm thấy đơn hàng ID: {}", orderId);
                    return new NotFoundException("[ORDER] Đơn hàng không tồn tại");
                });

        // User chỉ xem được đơn của mình, Admin xem được tất cả
        User currentUser = userService.getUserLogin();
        boolean isAdmin = currentUser.getRole().getName().equals(RoleConstant.ADMIN);
        if (!isAdmin && !order.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("[ORDER] Bạn không có quyền thao tác với đơn hàng này");
        }

        log.info("[ORDER] Xem chi tiết thành công | Order ID: {}", orderId);
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderDto cancelOrder(Long orderId) {
        // 1. Tìm Order
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new NotFoundException("[ORDER] Đơn hàng không tồn tại"));
        // 2. Kiểm tra order có thuộc về user hiện tại không
        User currentUser = userService.getUserLogin();
        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("[ORDER] Bạn không có quyền thao tác với đơn hàng này");
        }

        // 3. Kiểm tra trạng thái
        if (!order.getStatus().equals(OrderStatus.PENDING)) {
            throw new BadRequestException("[ORDER] Chỉ được hủy đơn hàng ở trạng thái PENDING");
        }

// chỉ hoàn kho nếu là COD (vì chỉ COD mới trừ lúc tạo đơn)
        if (order.getPayment().getPaymentMethod() == PaymentMethod.COD) {
            for (OrderDetail orderDetail : order.getOrderDetails()) {
                Inventory inventory = inventoryRepository.findByProductId(orderDetail.getProduct().getId())
                        .orElseThrow(() -> new NotFoundException("[ORDER] Sản phẩm không tồn tại"));

                Integer oldQuantity = inventory.getQuantity();
                Integer newQuantity = oldQuantity + orderDetail.getQuantity();
                inventory.setQuantity(newQuantity);
                inventoryRepository.save(inventory);

                InventoryTransaction inventoryTransaction = new InventoryTransaction();
                inventoryTransaction.setInventory(inventory);
                inventoryTransaction.setQuantity(orderDetail.getQuantity());
                inventoryTransaction.setType(TypeInventory.IMPORT);
                inventoryTransaction.setNote("Import product from cancel order");
                inventoryTransactionRepository.save(inventoryTransaction);

                log.info("[INVENTORY] Nhập kho do hủy đơn hàng COD | Product ID: {} | {} → {}",
                        orderDetail.getProduct().getId(), oldQuantity, newQuantity);
            }
        }
// Nếu là VNPAY và đang PENDING → chưa từng trừ kho, không cần hoàn
        // 5. Update status Order → CANCELLED
        order.setStatus(OrderStatus.CANCELLED);


        // 6. Update PaymentStatus
        if (order.getPayment() != null) {
            PaymentStatus currentPayMentStatus = order.getPayment().getStatus();
            if (currentPayMentStatus.equals(PaymentStatus.PENDING)) {
                // Chưa thanh toán → set FAILED
                order.getPayment().setStatus(PaymentStatus.FAILED);
            } else if (currentPayMentStatus.equals(PaymentStatus.SUCCESS)) {
                // Đã thanh toán rồi → set REFUNDED (admin sẽ hoàn tiền sau)
                order.getPayment().setStatus(PaymentStatus.REFUNDED);
            }
        }



        orderRepository.save(order);
        log.info("[ORDER] Hủy đơn hàng thành công | Order ID: {}", orderId);

        return orderMapper.toDto(order);
    }

    public void validateStatusTransaction(OrderStatus current, OrderStatus next) {
        Map<OrderStatus, List<OrderStatus>> validTransactions = Map.of(
                OrderStatus.PENDING, List.of(OrderStatus.PROCESSING, CANCELLED),
                OrderStatus.PROCESSING, List.of(OrderStatus.SHIPPED, CANCELLED),
                OrderStatus.SHIPPED, List.of(DELIVERED),
                DELIVERED, List.of(),
                CANCELLED, List.of()

        );
        // map.getOrDefault()
        //Lấy value theo key, nếu key không tồn tại thì trả về giá trị mặc định.
        // neu current không có trong validTransactions thì trả về list rỗng
        // neu current có trong validTransactions thì trả về list status có thể chuyển sang
        List<OrderStatus> allowed = validTransactions.getOrDefault(current, List.of());
        if (!allowed.contains(next)) {
            throw new BadRequestException(String.format("Cannot change status from %s → %s", current, next));
        }
    }

    @Override
    @Transactional
    public OrderDto updateOrderStatus(ReqUpdateOrderStatus req) {
        log.info("[ORDER] Cập nhật trạng thái đơn hàng ID: {} → {}",
                req.getOrderId(), req.getStatus());
        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(req.getStatus().toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Trạng thái không tồn tại");
        }
        // 1. Tìm Order
        Order order = orderRepository.findById(req.getOrderId()).orElseThrow(
                () -> new NotFoundException("[ORDER] Đơn hàng không tồn tại")
        );

        // 2. Kiểm tra chuyển trạng thái hợp lệ không
        validateStatusTransaction(order.getStatus(), newStatus);

        //  3. Nếu Admin hủy đơn → hoàn kho
        if (newStatus.equals(CANCELLED)) {
            // Nếu chuyển sang CANCELLED thì phải cộng lại tồn kho và ghi InventoryTransaction
            for (OrderDetail orderDetail : order.getOrderDetails()) {
                Inventory inventory = inventoryRepository.findByProductId(orderDetail.getProduct().getId()).orElseThrow(
                        () -> new NotFoundException("[INVENTORY] Sản phẩm không tồn tại")
                );
                Integer oldQuantity = inventory.getQuantity();
                Integer newQuantity = oldQuantity + orderDetail.getQuantity();
                inventory.setQuantity(newQuantity);
                inventoryRepository.save(inventory);

                InventoryTransaction inventoryTransaction = new InventoryTransaction();
                inventoryTransaction.setInventory(inventory);
                inventoryTransaction.setQuantity(orderDetail.getQuantity());
                inventoryTransaction.setType(TypeInventory.IMPORT);
                inventoryTransaction.setNote("Import product from cancel order by admin");
                inventoryTransactionRepository.save(inventoryTransaction);

                log.info("[INVENTORY] Nhập kho do hủy đơn hàng | Product ID: {} | {} → {}",
                        orderDetail.getProduct().getId(), oldQuantity, newQuantity);
            }
        }

        // 4. Update PaymentStatus tương ứng
        if (order.getPayment() != null) {
            switch (newStatus) {
                case DELIVERED:
                    order.getPayment().setStatus(PaymentStatus.SUCCESS);
                    break;
                case CANCELLED:
                    PaymentStatus paymentStatus = order.getPayment().getStatus();
                    if (paymentStatus.equals(PaymentStatus.SUCCESS)) {
                        order.getPayment().setStatus(PaymentStatus.REFUNDED);
                    } else {
                        order.getPayment().setStatus(PaymentStatus.FAILED);
                    }
                    break;
                default:
                    break;
            }
        }

        // 5. Update status Order
        order.setStatus(newStatus);



        orderRepository.save(order);
        log.info("[ORDER] Cập nhật trạng thái thành công | Order ID: {} | Status: {}",
                req.getOrderId(), newStatus);

        return orderMapper.toDto(order);
    }

    @Override
    public ResultPaginationDto getAllOrders(List<String> filter, Pageable pageable) {
        SpecificationBuilder<Order> specificationBuilder = new SpecificationBuilder<>();
        FilterProcessor.process(specificationBuilder, filter);

        Page<Order> pageOrder = orderRepository.findAll(specificationBuilder.build(), pageable);

        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageOrder.getTotalPages());
        meta.setTotal(pageOrder.getTotalElements());

        ResultPaginationDto data = new ResultPaginationDto();
        data.setMeta(meta);
        data.setResult(orderMapper.toDtoList(pageOrder.getContent()));
        return data;

    }




}
