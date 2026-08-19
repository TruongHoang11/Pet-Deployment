package N18.haui.Pet_18.service;


import N18.haui.Pet_18.domain.dto.pagination.ResultPaginationDto;
import N18.haui.Pet_18.domain.dto.request.ReqCreateOrderBuyNow;
import N18.haui.Pet_18.domain.dto.request.ReqCreateOrderFromCart;
import N18.haui.Pet_18.domain.dto.request.ReqOrderStatus;
import N18.haui.Pet_18.domain.dto.request.ReqUpdateOrderStatus;
import N18.haui.Pet_18.domain.dto.response.OrderDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderDto createOrderFromCart(ReqCreateOrderFromCart req);

    OrderDto createOrderFromBuyNow(ReqCreateOrderBuyNow req);

    ResultPaginationDto getMyOrders(ReqOrderStatus status, Pageable pageable);

    OrderDto getOrderDetail(Long orderId);

    OrderDto cancelOrder(Long orderId);

    OrderDto updateOrderStatus(ReqUpdateOrderStatus req);

    ResultPaginationDto getAllOrders(List<String> filter, Pageable pageable);




}