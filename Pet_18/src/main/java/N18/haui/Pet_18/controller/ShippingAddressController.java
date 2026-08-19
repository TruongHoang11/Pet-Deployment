package N18.haui.Pet_18.controller;

import N18.haui.Pet_18.base.RestApiV1;
import N18.haui.Pet_18.base.VsResponseUtil;
import N18.haui.Pet_18.constant.UrlConstant;
import N18.haui.Pet_18.domain.dto.request.ReqCreateShippingAddress;
import N18.haui.Pet_18.domain.dto.request.ReqUpdateShippingAddress;
import N18.haui.Pet_18.domain.dto.response.ShippingAddressDto;
import N18.haui.Pet_18.service.ShippingAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestApiV1
@RequiredArgsConstructor
@Slf4j
public class ShippingAddressController {
    private final ShippingAddressService service;


    @PatchMapping(UrlConstant.ShippingAddress.SET_DEFAULT_ADDRESS)
    public ResponseEntity<?> setDefaultShippingAddress(@PathVariable Long id){
        return VsResponseUtil.success(HttpStatus.OK,service.setDefaultShippingAddress(id));

    }

    @PostMapping(UrlConstant.ShippingAddress.CREATE_SHIPPING_ADDRESS)
    public ResponseEntity<?> createShippingAddress(@RequestBody @Valid ReqCreateShippingAddress req)  {
        ShippingAddressDto dto = service.createShippingAddress(req);
        return VsResponseUtil.success(HttpStatus.OK, dto);
    }

    @PutMapping(UrlConstant.ShippingAddress.UPDATE_SHIPPING_ADDRESS)
    public ResponseEntity<?> updateShippingAddress(@RequestBody @Valid ReqUpdateShippingAddress rq) {
        return VsResponseUtil.success(HttpStatus.OK,service.updateShippingAddress(rq) );

    }

    @DeleteMapping(UrlConstant.ShippingAddress.DELETE_SHIPPING_ADDRESS)
    public ResponseEntity<?> deleteShippingAddress(@PathVariable Long id){
        return VsResponseUtil.success(HttpStatus.OK,service.deleteShippingAddress(id));
    }



    @GetMapping(UrlConstant.ShippingAddress.GET_SHIPPING_ADDRESSES)
    public ResponseEntity<?> getAllShippingAddress(){
        return VsResponseUtil.success(HttpStatus.OK,service.getAllShippingAddress());

    }

}
