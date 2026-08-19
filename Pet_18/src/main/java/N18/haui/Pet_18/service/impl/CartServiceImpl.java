package N18.haui.Pet_18.service.impl;

import N18.haui.Pet_18.domain.dto.response.CartDto;
import N18.haui.Pet_18.domain.dto.response.CartItemDto;
import N18.haui.Pet_18.domain.dto.response.CommonResponseDto;
import N18.haui.Pet_18.domain.entity.Cart;
import N18.haui.Pet_18.domain.entity.User;
import N18.haui.Pet_18.domain.mapper.CartItemMapper;
import N18.haui.Pet_18.exception.NotFoundException;
import N18.haui.Pet_18.repository.CartRepository;
import N18.haui.Pet_18.service.CartService;
import N18.haui.Pet_18.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserService userService;
    private final CartItemMapper cartItemMapper;

    @Override
    public CartDto getCart() {
        log.info("[CART] Xem giỏ hàng của user hiện tại");
        User currentUser = userService.getUserLogin();
        Cart cart = cartRepository.findByUserId(currentUser.getId()).orElse(null);
        if(cart == null){
            log.info("[CART] Giỏ hàng trống | User ID: {}", currentUser.getId());
            return CartDto.builder()
                    .totalItem(0)
                    .totalAmount(BigDecimal.ZERO)
                    .itemDtoList(Collections.emptyList())
                    .build();

        }

        List<CartItemDto> cartItems = cart.getCartItems().stream()
                .map(cartItem -> {
                    return cartItemMapper.toDto(cartItem);
                }).toList();

        // Tính tổng tiền giỏ hàng bằng cách bóc tách totalPrice của
        // từng item rồi cộng dồn lại (mặc định là 0 nếu giỏ trống)
        BigDecimal totalAmount = cartItems.stream().
                map(CartItemDto::getTotalPrice)
                // .reduce(BigDecimal.ZERO, BigDecimal::add);
                .reduce(BigDecimal.ZERO, (sum, price) -> sum.add(price));
        //BigDecimal.ZERO: Giá trị bắt đầu (gán biến tích lũy ban đầu bằng 0).
        //sum: Biến tích lũy (giống như biến total giữ tổng số tiền hiện tại qua các lượt cộng).
        //price: Giá tiền của món hàng hiện tại đang được duyệt qua trên băng chuyền.
        //sum ban đầu = 0. Gặp món 100k -> lấy 0 + 100k = 100k. Lúc này sum trở thành 100k.


        log.info("[CART] User ID: {} | Số item: {} | Tổng tiền: {}",
                currentUser.getId(), cartItems.size(), totalAmount);
        return CartDto.builder()
                .id(cart.getId())
                .totalAmount(totalAmount)
                .totalItem(cartItems.size())
                .itemDtoList(cartItems)
                .build();
    }

    @Override
    public CommonResponseDto deleteCart() {
        log.info("[CART] Xóa toàn bộ giỏ hàng");

        //lay user hien tai
        User currentUser = userService.getUserLogin();

        // tim card
        Cart cart = cartRepository.findByUserId(currentUser.getId()).orElseThrow(
                () -> new NotFoundException("Giỏ hàng không tồn tại")

        );

        //xoa toan bo card item
        cart.getCartItems().clear(); // nhờ CascadeType.ALL + orphanRemoval = true
        cartRepository.save(cart);
        log.info("[CART] Xóa toàn bộ giỏ hàng thành công | User ID: {}", currentUser.getId());
        return new CommonResponseDto(true, "Xóa giỏ hàng thành công");
    }
}
