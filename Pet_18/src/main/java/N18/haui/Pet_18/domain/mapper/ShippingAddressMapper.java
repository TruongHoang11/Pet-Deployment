package N18.haui.Pet_18.domain.mapper;

import N18.haui.Pet_18.domain.entity.ShippingAddress;
import N18.haui.Pet_18.domain.dto.response.ShippingAddressDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShippingAddressMapper {

    @Mapping(target="fullAddress", expression = "java(buildFullAddress(shippingAddress))")
    ShippingAddressDto toDto(ShippingAddress shippingAddress);

    default String buildFullAddress(ShippingAddress dto){
        return dto.getAddressDetail() + ", " + dto.getWard() + ", " + dto.getDistrict() + ", " + dto.getProvince();
    }
}
