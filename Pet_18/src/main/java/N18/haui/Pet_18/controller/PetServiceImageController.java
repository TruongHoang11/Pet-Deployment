package N18.haui.Pet_18.controller;

import N18.haui.Pet_18.base.RestApiV1;
import N18.haui.Pet_18.base.VsResponseUtil;
import N18.haui.Pet_18.constant.UrlConstant;
import N18.haui.Pet_18.domain.dto.request.ReqAddServiceImage;
import N18.haui.Pet_18.service.PetServiceImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestApiV1
@RequiredArgsConstructor
public class PetServiceImageController {

    private final PetServiceImageService serviceImageService;

    @PostMapping(UrlConstant.PetServiceImages.ADD_IMAGES)
    public ResponseEntity<?> addImage(@Valid @RequestBody ReqAddServiceImage req) {
        return VsResponseUtil.success(HttpStatus.CREATED, serviceImageService.addImage(req));
    }

    @DeleteMapping(UrlConstant.PetServiceImages.DELETE_IMAGE)
    public ResponseEntity<?> deleteImage(@PathVariable Long id) {
        serviceImageService.deleteImage(id);
        return VsResponseUtil.success(HttpStatus.NO_CONTENT, null);
    }

    @GetMapping(UrlConstant.PetServiceImages.GET_SERVICE_IMAGES)
    public ResponseEntity<?> getServiceImages(@PathVariable Long serviceId) {
        return VsResponseUtil.success(HttpStatus.OK, serviceImageService.getServiceImages(serviceId));
    }

    @PatchMapping(UrlConstant.PetServiceImages.SET_MAIN_IMAGE)
    public ResponseEntity<?> setMainImage(@RequestParam Long imageId) {
        serviceImageService.setMainImage(imageId);
        return VsResponseUtil.success(HttpStatus.OK, null);
    }
}
