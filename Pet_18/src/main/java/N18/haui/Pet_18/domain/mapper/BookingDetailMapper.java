package N18.haui.Pet_18.domain.mapper;

import N18.haui.Pet_18.domain.dto.response.BookingDetailDto;
import N18.haui.Pet_18.domain.entity.BookingDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingDetailMapper {

    @Mapping(target = "bookingId", source = "booking.id")
    @Mapping(target = "serviceId", source = "service.id")
    @Mapping(target = "serviceName", source = "service.name")
    @Mapping(target = "servicePrice", source = "service.basePrice")
    @Mapping(target = "serviceDuration", source = "service.durationMin")
    BookingDetailDto toDto(BookingDetail bookingDetail);

    List<BookingDetailDto> toDtos(List<BookingDetail> bookingDetails);

    BookingDetail toEntity(BookingDetailDto dto);
}
