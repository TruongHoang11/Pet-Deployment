package N18.haui.Pet_18.controller;

import N18.haui.Pet_18.base.RestApiV1;
import N18.haui.Pet_18.base.VsResponseUtil;
import N18.haui.Pet_18.constant.UrlConstant;
import N18.haui.Pet_18.domain.dto.request.ReqCreateService;
import N18.haui.Pet_18.domain.dto.request.ReqRecommendationDto;
import N18.haui.Pet_18.domain.dto.request.ReqUpdateService;
import N18.haui.Pet_18.domain.dto.response.ResRecommendationDto;
import N18.haui.Pet_18.service.PetServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestApiV1
@RequiredArgsConstructor
public class PetServiceController {

    private final PetServiceService petServiceService;

    @PostMapping(UrlConstant.PetService.CREATE_SERVICE)
    public ResponseEntity<?> createService(@Valid @RequestBody ReqCreateService req) {
        return VsResponseUtil.success(HttpStatus.CREATED, petServiceService.createService(req));
    }

    @PutMapping(UrlConstant.PetService.UPDATE_SERVICE)
    public ResponseEntity<?> updateService(@Valid @RequestBody ReqUpdateService req) {
        return VsResponseUtil.success(HttpStatus.OK, petServiceService.updateService(req));
    }

    @GetMapping(UrlConstant.PetService.GET_SERVICE)
    public ResponseEntity<?> getService(@PathVariable Long id) {
        return VsResponseUtil.success(HttpStatus.OK, petServiceService.getServiceById(id));
    }

    @GetMapping(UrlConstant.PetService.GET_ALL_SERVICES)
    public ResponseEntity<?> getAllServices(
            @RequestParam(required = false) List<String> filter,
            Pageable pageable) {
        return VsResponseUtil.success(HttpStatus.OK, petServiceService.getAllServices(filter,pageable));
    }

    @GetMapping(UrlConstant.PetService.SEARCH_SERVICES)
    public ResponseEntity<?> searchServices(
            @RequestParam String keyword,
            Pageable pageable) {
        return VsResponseUtil.success(HttpStatus.OK, petServiceService.searchServices(keyword, pageable));
    }

    @GetMapping(UrlConstant.PetService.GET_SERVICES_BY_CATEGORY)
    public ResponseEntity<?> getServicesByCategory(
            @PathVariable Long categoryId,
            Pageable pageable) {
        return VsResponseUtil.success(HttpStatus.OK, petServiceService.getServicesByCategory(categoryId, pageable));
    }

    @DeleteMapping(UrlConstant.PetService.DELETE_SERVICE)
    public ResponseEntity<?> deleteService(@PathVariable Long id) {
        return VsResponseUtil.success(HttpStatus.NO_CONTENT, petServiceService.deleteService(id));
    }

    @GetMapping(UrlConstant.PetService.GET_TOP_SERVICES)
    public ResponseEntity<?> getTopServices(
            @RequestParam(defaultValue = "6") Integer limit) {
        return VsResponseUtil.success(HttpStatus.OK, petServiceService.getTopServices(limit));
    }

    @GetMapping(UrlConstant.PetService.GET_RECOMMENDATIONS)
    public ResponseEntity<?> recommendServices(@Valid @RequestBody ReqRecommendationDto req) {
        return VsResponseUtil.success(HttpStatus.OK, new ResRecommendationDto(petServiceService.getRecommendedServiceIds(req.getItemIds())));
    }
}
