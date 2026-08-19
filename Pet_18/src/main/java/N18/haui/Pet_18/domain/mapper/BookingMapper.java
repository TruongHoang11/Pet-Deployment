package N18.haui.Pet_18.domain.mapper;

import N18.haui.Pet_18.domain.dto.response.BookingDto;
import N18.haui.Pet_18.domain.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = BookingDetailMapper.class)
public interface BookingMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.name")
    @Mapping(target = "petName", source = "pet.name")
    @Mapping(target = "petId", source = "pet.id")
    @Mapping(target = "orderId", source = "order.id")
    BookingDto toDto(Booking booking);

    List<BookingDto> toDtos(List<Booking> bookings);

    Booking toEntity(BookingDto dto);
}
