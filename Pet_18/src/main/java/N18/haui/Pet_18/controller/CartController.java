package N18.haui.Pet_18.controller;

import N18.haui.Pet_18.base.RestApiV1;
import N18.haui.Pet_18.base.VsResponseUtil;
import N18.haui.Pet_18.constant.UrlConstant;
import N18.haui.Pet_18.domain.dto.response.CartDto;
import N18.haui.Pet_18.domain.dto.response.CommonResponseDto;
import N18.haui.Pet_18.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@RestApiV1
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping(UrlConstant.Cart.GET_CART)
    public ResponseEntity<?> getCard()  {
      CartDto cartDto = cartService.getCart();
        return VsResponseUtil.success(HttpStatus.OK, cartDto);
    }


    @DeleteMapping(UrlConstant.Cart.DELETE_CART)
    public ResponseEntity<?> deleteCartItem(){
        CommonResponseDto commonResponseDto = cartService.deleteCart();
        return VsResponseUtil.success(HttpStatus.OK, commonResponseDto);

    }

}
