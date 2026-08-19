package N18.haui.Pet_18.controller;

import N18.haui.Pet_18.base.RestApiV1;
import N18.haui.Pet_18.base.VsResponseUtil;
import N18.haui.Pet_18.constant.OrderStatus;
import N18.haui.Pet_18.constant.UrlConstant;
import N18.haui.Pet_18.domain.dto.request.ReqCreateOrderBuyNow;
import N18.haui.Pet_18.domain.dto.request.ReqCreateOrderFromCart;
import N18.haui.Pet_18.domain.dto.request.ReqOrderStatus;
import N18.haui.Pet_18.domain.dto.request.ReqUpdateOrderStatus;
import N18.haui.Pet_18.domain.dto.response.OrderDto;
import N18.haui.Pet_18.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestApiV1
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping(UrlConstant.Order.GET_ORDER_DETAIL)
    public ResponseEntity<?> getOrderDetail(@PathVariable Long id) {
        return VsResponseUtil.success(HttpStatus.OK, orderService.getOrderDetail(id));
    }


    @GetMapping(UrlConstant.Order.GET_ALL_ORDERS)
    public ResponseEntity<?> getAllOrders (
            @RequestParam(required = false) List<String> filter,
            Pageable pageable
    ){
        return VsResponseUtil.success(HttpStatus.OK,orderService.getAllOrders(filter, pageable));

    }

    @PostMapping(UrlConstant.Order.CREATE_ORDER_FROM_CART)
    public ResponseEntity<?> createOrderFromCart(@Valid @RequestBody  ReqCreateOrderFromCart req)  {
        OrderDto orderDto = orderService.createOrderFromCart(req);

        return VsResponseUtil.success(HttpStatus.OK, orderDto);
    }


    @PostMapping(UrlConstant.Order.CREATE_ORDER_FROM_BUY_NOW)
    public ResponseEntity<?> createOrderFromBuyNow(@Valid  @RequestBody ReqCreateOrderBuyNow req)  {
        OrderDto orderDto = orderService.createOrderFromBuyNow(req);

        return VsResponseUtil.success(HttpStatus.OK, orderDto);
    }

    @PatchMapping(UrlConstant.Order.CANCEL_ORDER)
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        return VsResponseUtil.success(HttpStatus.OK, orderService.cancelOrder(id));

    }

    @PatchMapping(UrlConstant.Order.UPDATE_ORDER_STATUS)
    public ResponseEntity<?> updateOrderStatus(@RequestBody @Valid ReqUpdateOrderStatus req) {
        return VsResponseUtil.success(HttpStatus.OK, orderService.updateOrderStatus(req));
    }

    @GetMapping(UrlConstant.Order.GET_MY_ORDERS)
    public ResponseEntity<?> getMyOrders(
            @RequestBody @Valid ReqOrderStatus status,
            Pageable pageable){

        return VsResponseUtil.success(HttpStatus.OK,orderService.getMyOrders(status, pageable) );

    }
}
