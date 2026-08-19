package N18.haui.Pet_18.service.impl;

import N18.haui.Pet_18.domain.dto.request.ReqAddServiceImage;
import N18.haui.Pet_18.domain.dto.response.ServiceImageDto;
import N18.haui.Pet_18.domain.entity.PetService;
import N18.haui.Pet_18.domain.entity.PetServiceImage;
import N18.haui.Pet_18.domain.mapper.ServiceImageMapper;
import N18.haui.Pet_18.exception.NotFoundException;
import N18.haui.Pet_18.repository.PetServiceImageRepository;
import N18.haui.Pet_18.repository.PetServiceRepository;
import N18.haui.Pet_18.service.PetServiceImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PetServiceImageServiceImpl implements PetServiceImageService {

    private final PetServiceImageRepository imageRepository;
    private final PetServiceRepository serviceRepository;
    private final ServiceImageMapper imageMapper;

    @Override
    @Transactional
    public ServiceImageDto addImage(ReqAddServiceImage req) {
        log.info("[SERVICE_IMAGE] Adding image to service: {}", req.getServiceId());

        PetService service = serviceRepository.findById(req.getServiceId())
                .orElseThrow(() -> new NotFoundException("[SERVICE_IMAGE] Service not found"));

        if (req.getIsThumbnail() != null && req.getIsThumbnail()) {
            imageRepository.findByPetServiceIdAndIsThumbnailTrue(req.getServiceId())
                    .ifPresent(img -> {
                        img.setIsThumbnail(false);
                        imageRepository.save(img);
                    });
        }

        PetServiceImage image = new PetServiceImage();
        image.setImageUrl(req.getImageUrl());
        image.setIsThumbnail(req.getIsThumbnail() != null ? req.getIsThumbnail() : false);
        image.setPetService(service);

        PetServiceImage saved = imageRepository.save(image);
        log.info("[SERVICE_IMAGE] Image added successfully with ID: {}", saved.getId());

        return imageMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteImage(Long imageId) {
        log.info("[SERVICE_IMAGE] Deleting image with ID: {}", imageId);

        PetServiceImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException("[SERVICE_IMAGE] Image not found"));

        imageRepository.delete(image);
        log.info("[SERVICE_IMAGE] Image deleted successfully");
    }

    @Override
    public List<ServiceImageDto> getServiceImages(Long serviceId) {
        log.info("[SERVICE_IMAGE] Getting images for service: {}", serviceId);

        serviceRepository.findById(serviceId)
                .orElseThrow(() -> new NotFoundException("[SERVICE_IMAGE] Service not found"));

        List<PetServiceImage> images = imageRepository.findByPetServiceId(serviceId);
        return imageMapper.toDtos(images);
    }

    @Override
    @Transactional
    public void setMainImage(Long imageId) {
        log.info("[SERVICE_IMAGE] Setting main image: {}", imageId);

        PetServiceImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException("[SERVICE_IMAGE] Image not found"));

        imageRepository.findByPetServiceIdAndIsThumbnailTrue(image.getPetService().getId())
                .ifPresent(mainImg -> {
                    mainImg.setIsThumbnail(false);
                    imageRepository.save(mainImg);
                });

        image.setIsThumbnail(true);
        imageRepository.save(image);
        log.info("[SERVICE_IMAGE] Main image set successfully");
    }

    @Override
    @Transactional
    public void deleteAllServiceImages(Long serviceId) {
        log.info("[SERVICE_IMAGE] Deleting all images for service: {}", serviceId);

        List<PetServiceImage> images = imageRepository.findByPetServiceId(serviceId);
        imageRepository.deleteAll(images);
        log.info("[SERVICE_IMAGE] All images deleted for service: {}", serviceId);
    }
}
