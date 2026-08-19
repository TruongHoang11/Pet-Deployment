package N18.haui.Pet_18.domain.mapper;

import N18.haui.Pet_18.domain.dto.response.ServiceDto;
import N18.haui.Pet_18.domain.entity.PetService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = ServiceImageMapper.class)
public interface ServiceMapper {

    @Mapping(target = "categoryName", source = "category.name")
    ServiceDto toDto(PetService service);

    List<ServiceDto> toDtos(List<PetService> services);

    PetService toEntity(ServiceDto dto);
}
