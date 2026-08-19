package N18.haui.Pet_18.service;


import N18.haui.Pet_18.domain.dto.request.ReqCreateShippingAddress;
import N18.haui.Pet_18.domain.dto.request.ReqUpdateShippingAddress;
import N18.haui.Pet_18.domain.dto.response.CommonResponseDto;
import N18.haui.Pet_18.domain.dto.response.ShippingAddressDto;

import java.util.List;

public interface ShippingAddressService {

    ShippingAddressDto createShippingAddress(ReqCreateShippingAddress req);

    ShippingAddressDto updateShippingAddress(ReqUpdateShippingAddress req);

    CommonResponseDto deleteShippingAddress(Long addressId);

    List<ShippingAddressDto> getAllShippingAddress();

    CommonResponseDto setDefaultShippingAddress(Long addressId);

}
