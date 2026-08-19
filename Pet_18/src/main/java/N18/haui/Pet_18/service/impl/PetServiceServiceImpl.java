package N18.haui.Pet_18.service.impl;

import N18.haui.Pet_18.domain.dto.pagination.ResultPaginationDto;
import N18.haui.Pet_18.domain.dto.request.ReqCreateService;
import N18.haui.Pet_18.domain.dto.request.ReqUpdateService;
import N18.haui.Pet_18.domain.dto.response.CommonResponseDto;
import N18.haui.Pet_18.domain.dto.response.ServiceDto;
import N18.haui.Pet_18.domain.entity.Category;
import N18.haui.Pet_18.domain.entity.PetService;
import N18.haui.Pet_18.domain.mapper.ServiceMapper;
import N18.haui.Pet_18.domain.specification.FilterProcessor;
import N18.haui.Pet_18.domain.specification.SpecificationBuilder;
import N18.haui.Pet_18.exception.NotFoundException;
import N18.haui.Pet_18.repository.CategoryRepository;
import N18.haui.Pet_18.repository.PetServiceRepository;
import N18.haui.Pet_18.service.PetServiceReviewService;
import N18.haui.Pet_18.service.PetServiceService;
import N18.haui.Pet_18.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PetServiceServiceImpl implements PetServiceService {

    private final PetServiceRepository petServiceRepository;
    private final CategoryRepository categoryRepository;
    private final ServiceMapper serviceMapper;
    private final PetServiceReviewService reviewService;
    private final RecommendationService recommendationService;

    @Override
    @Transactional
    public ServiceDto createService(ReqCreateService req) {
        log.info("[SERVICE] Creating new service: {}", req.getName());

        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new NotFoundException("[SERVICE] Category not found"));

        PetService service = new PetService();
        service.setName(req.getName());
        service.setDescription(req.getDescription());
        service.setBasePrice(req.getBasePrice());
        service.setDurationMin(req.getDurationMin());
        service.setCategory(category);

        PetService saved = petServiceRepository.save(service);
        log.info("[SERVICE] Service created successfully with ID: {}", saved.getId());

        ServiceDto dto = serviceMapper.toDto(saved);
        enrichServiceDto(dto);
        return dto;
    }

    @Override
    @Transactional
    public ServiceDto updateService(ReqUpdateService req) {
        log.info("[SERVICE] Updating service with ID: {}", req.getId());

        PetService service = petServiceRepository.findById(req.getId())
                .orElseThrow(() -> new NotFoundException("[SERVICE] Service not found"));

        if (req.getName() != null) service.setName(req.getName());
        if (req.getDescription() != null) service.setDescription(req.getDescription());
        if (req.getBasePrice() != null) service.setBasePrice(req.getBasePrice());
        if (req.getDurationMin() != null) service.setDurationMin(req.getDurationMin());

        if (req.getCategoryId() != null) {
            Category category = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("[SERVICE] Category not found"));
            service.setCategory(category);
        }

        PetService updated = petServiceRepository.save(service);
        ServiceDto dto = serviceMapper.toDto(updated);
        enrichServiceDto(dto);
        return dto;
    }

    @Override
    public ServiceDto getServiceById(Long id) {
        log.info("[SERVICE] Getting service with ID: {}", id);

        PetService service = petServiceRepository.findByIdAndDeleteFlagFalseAndActiveFlagTrue(id)
                .orElseThrow(() -> new NotFoundException("[SERVICE] Service not found"));

        ServiceDto dto = serviceMapper.toDto(service);
        enrichServiceDto(dto);
        return dto;
    }

    @Override
    public ResultPaginationDto getAllServices(List<String> filter, Pageable pageable) {
        log.info("[SERVICE] Getting all services with pagination");
        SpecificationBuilder<PetService> specificationBuilder = new SpecificationBuilder<>();
        FilterProcessor.process(specificationBuilder, filter);
        Specification<PetService> spec = specificationBuilder.build();
        Specification<PetService> activeSpec = (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.isFalse(root.get("deleteFlag")),
                        criteriaBuilder.isTrue(root.get("activeFlag"))
                );
        Specification<PetService> finalSpec = spec == null ? activeSpec : spec.and(activeSpec);

        Page<PetService> page = petServiceRepository.findAll(finalSpec, pageable);
        List<ServiceDto> dtos = page.getContent().stream()
                .map(service -> {
                    ServiceDto dto = serviceMapper.toDto(service);
                    enrichServiceDto(dto);
                    return dto;
                })
                .toList();

        return buildPaginationResponse(dtos, page);
    }

    @Override
    public ResultPaginationDto searchServices(String keyword, Pageable pageable) {
        log.info("[SERVICE] Searching services with keyword: {}", keyword);

        Page<PetService> page = petServiceRepository.findByDeleteFlagFalseAndActiveFlagTrueAndNameContainingIgnoreCase(keyword, pageable);
        List<ServiceDto> dtos = page.getContent().stream()
                .map(service -> {
                    ServiceDto dto = serviceMapper.toDto(service);
                    enrichServiceDto(dto);
                    return dto;
                })
                .toList();

        return buildPaginationResponse(dtos, page);
    }

    @Override
    public ResultPaginationDto getServicesByCategory(Long categoryId, Pageable pageable) {
        log.info("[SERVICE] Getting services by category: {}", categoryId);

        Page<PetService> page = petServiceRepository.findByDeleteFlagFalseAndActiveFlagTrueAndCategoryId(categoryId, pageable);
        List<ServiceDto> dtos = page.getContent().stream()
                .map(service -> {
                    ServiceDto dto = serviceMapper.toDto(service);
                    enrichServiceDto(dto);
                    return dto;
                })
                .toList();

        return buildPaginationResponse(dtos, page);
    }

    @Override
    public CommonResponseDto deleteService(Long id) {
        log.info("[SERVICE] Deleting service with ID: {}", id);

        PetService service = petServiceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[SERVICE] Service not found"));

        service.setDeleteFlag(true);
        service.setActiveFlag(false);
        petServiceRepository.save(service);
        log.info("[SERVICE] Service marked as deleted");
        return new CommonResponseDto(true, "Service deleted successfully");
    }

    @Override
    public List<ServiceDto> getTopServices(Integer limit) {
        log.info("[SERVICE] Getting top {} services", limit);

        Page<PetService> page = petServiceRepository.findByDeleteFlagFalseAndActiveFlagTrue(
                Pageable.ofSize(limit)
        );

        return page.getContent().stream()
                .map(service -> {
                    ServiceDto dto = serviceMapper.toDto(service);
                    enrichServiceDto(dto);
                    return dto;
                })
                .toList();
    }

    @Override
    public List<Long> getRecommendedServiceIds(List<Long> serviceIds) {
        return recommendationService.recommendServices(serviceIds);
    }

    private void enrichServiceDto(ServiceDto dto) {
        if (dto.getId() != null) {
            dto.setAverageRating(reviewService.getAverageRating(dto.getId()));
            dto.setTotalReviews(reviewService.getReviewCount(dto.getId()));
        }
    }

    private <T> ResultPaginationDto buildPaginationResponse(List<T> data, Page<?> page) {
        ResultPaginationDto response = new ResultPaginationDto();
        response.setResult(data);

        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();
        meta.setPage(page.getNumber());
        meta.setPageSize(page.getSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());
        response.setMeta(meta);

        return response;
    }
}
