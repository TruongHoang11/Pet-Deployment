package N18.haui.Pet_18.controller;

import N18.haui.Pet_18.base.RestApiV1;
import N18.haui.Pet_18.base.VsResponseUtil;
import N18.haui.Pet_18.constant.UrlConstant;
import N18.haui.Pet_18.domain.dto.request.ReqAddCartItem;
import N18.haui.Pet_18.domain.dto.request.ReqUpdateCartItem;
import N18.haui.Pet_18.domain.dto.response.CartItemDto;
import N18.haui.Pet_18.domain.dto.response.CommonResponseDto;
import N18.haui.Pet_18.service.CartItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestApiV1
@Slf4j
@RequiredArgsConstructor
public class CartItemController {
    private final CartItemService cartItemService;


    @PostMapping(UrlConstant.CartItem.ADD_CART_ITEM)
    public ResponseEntity<?> addCartItem(@RequestBody @Valid ReqAddCartItem reqAddCartItem)  {
        CartItemDto cartItemDto = cartItemService.addCartItem(reqAddCartItem);
        return VsResponseUtil.success(HttpStatus.OK, cartItemDto);
    }

    @PutMapping(UrlConstant.CartItem.UPDATE_CART_ITEM)
    public ResponseEntity<?> updateCartItem(@RequestBody @Valid ReqUpdateCartItem reqUpdateCartItem) {
        return VsResponseUtil.success(HttpStatus.OK, cartItemService.updateCartItem(reqUpdateCartItem));

    }

    @DeleteMapping(UrlConstant.CartItem.DELETE_CART_ITEM)
    public ResponseEntity<?> deleteCartItem(@PathVariable Long id){
        CommonResponseDto commonResponseDto = cartItemService.deleteCartItem(id);

        return VsResponseUtil.success(HttpStatus.OK, commonResponseDto);

    }

}
