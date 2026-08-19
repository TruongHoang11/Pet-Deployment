package N18.haui.Pet_18.service;


import N18.haui.Pet_18.domain.dto.response.CartDto;
import N18.haui.Pet_18.domain.dto.response.CommonResponseDto;

public interface CartService {

    CartDto getCart();

    CommonResponseDto deleteCart();
}
