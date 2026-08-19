package N18.haui.Pet_18.service;

import N18.haui.Pet_18.domain.dto.request.ReqAddServiceImage;
import N18.haui.Pet_18.domain.dto.response.ServiceImageDto;

import java.util.List;

public interface PetServiceImageService {

    ServiceImageDto addImage(ReqAddServiceImage req);

    void deleteImage(Long imageId);

    List<ServiceImageDto> getServiceImages(Long serviceId);

    void setMainImage(Long imageId);

    void deleteAllServiceImages(Long serviceId);
}
