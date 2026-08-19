package N18.haui.Pet_18.domain.mapper;

import N18.haui.Pet_18.domain.dto.response.ServiceImageDto;
import N18.haui.Pet_18.domain.entity.PetServiceImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ServiceImageMapper {

    @Mapping(target = "serviceId", source = "petService.id")
    ServiceImageDto toDto(PetServiceImage image);

    List<ServiceImageDto> toDtos(List<PetServiceImage> images);

    PetServiceImage toEntity(ServiceImageDto dto);
}
