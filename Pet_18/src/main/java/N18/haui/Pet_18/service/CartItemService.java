package N18.haui.Pet_18.service;


import N18.haui.Pet_18.domain.dto.request.ReqAddCartItem;
import N18.haui.Pet_18.domain.dto.request.ReqUpdateCartItem;
import N18.haui.Pet_18.domain.dto.response.CartItemDto;
import N18.haui.Pet_18.domain.dto.response.CommonResponseDto;

public interface CartItemService {

    CartItemDto addCartItem(ReqAddCartItem reqAddCartItem);

    CartItemDto updateCartItem(ReqUpdateCartItem req);

    CommonResponseDto deleteCartItem(Long itemId);
}
