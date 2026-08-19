package N18.haui.Pet_18.domain.mapper;

import N18.haui.Pet_18.domain.dto.response.ServiceReviewDto;
import N18.haui.Pet_18.domain.entity.PetServiceReview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ServiceReviewMapper {

    @Mapping(target = "serviceId", source = "petService.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.name")
    ServiceReviewDto toDto(PetServiceReview review);

    List<ServiceReviewDto> toDtos(List<PetServiceReview> reviews);

    PetServiceReview toEntity(ServiceReviewDto dto);
}
