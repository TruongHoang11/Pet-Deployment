package N18.haui.Pet_18.domain.mapper;

import N18.haui.Pet_18.domain.dto.response.PetDto;
import N18.haui.Pet_18.domain.entity.Pet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PetMapper {

    @Mapping(target = "ownerId", source = "user.id")
    PetDto toDto(Pet pet);

    List<PetDto> toDtoList(List<Pet> pets);
}