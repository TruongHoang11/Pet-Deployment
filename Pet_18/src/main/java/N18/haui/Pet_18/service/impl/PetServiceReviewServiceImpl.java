package N18.haui.Pet_18.service.impl;

import N18.haui.Pet_18.domain.dto.pagination.ResultPaginationDto;
import N18.haui.Pet_18.domain.dto.request.ReqCreateServiceReview;
import N18.haui.Pet_18.domain.dto.response.ServiceReviewDto;
import N18.haui.Pet_18.domain.entity.PetService;
import N18.haui.Pet_18.domain.entity.PetServiceReview;
import N18.haui.Pet_18.domain.entity.User;
import N18.haui.Pet_18.domain.mapper.ServiceReviewMapper;
import N18.haui.Pet_18.exception.BadRequestException;
import N18.haui.Pet_18.exception.NotFoundException;
import N18.haui.Pet_18.repository.PetServiceRepository;
import N18.haui.Pet_18.repository.PetServiceReviewRepository;
import N18.haui.Pet_18.service.PetServiceReviewService;
import N18.haui.Pet_18.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PetServiceReviewServiceImpl implements PetServiceReviewService {

    private final PetServiceReviewRepository reviewRepository;
    private final PetServiceRepository serviceRepository;
    private final UserService userService;
    private final ServiceReviewMapper reviewMapper;

    @Override
    @Transactional
    public ServiceReviewDto createReview(ReqCreateServiceReview req) {
        log.info("[SERVICE_REVIEW] Creating review for service: {}", req.getServiceId());

        if (req.getRating() < 1 || req.getRating() > 5) {
            throw new BadRequestException("[SERVICE_REVIEW] Rating must be between 1 and 5");
        }

        User currentUser = userService.getUserLogin();

        PetService service = serviceRepository.findById(req.getServiceId())
                .orElseThrow(() -> new NotFoundException("[SERVICE_REVIEW] Service not found"));

        reviewRepository.findByPetServiceIdAndUserId(req.getServiceId(), currentUser.getId())
                .ifPresent(review -> {
                    throw new BadRequestException("[SERVICE_REVIEW] User already reviewed this service");
                });

        PetServiceReview review = PetServiceReview.builder()
                .petService(service)
                .user(currentUser)
                .rating(req.getRating())
                .comment(req.getComment())
                .build();

        PetServiceReview saved = reviewRepository.save(review);
        log.info("[SERVICE_REVIEW] Review created successfully with ID: {}", saved.getId());

        return reviewMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        log.info("[SERVICE_REVIEW] Deleting review with ID: {}", reviewId);

        PetServiceReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("[SERVICE_REVIEW] Review not found"));

        User currentUser = userService.getUserLogin();
        if (!review.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("[SERVICE_REVIEW] You can only delete your own reviews");
        }

        reviewRepository.delete(review);
        log.info("[SERVICE_REVIEW] Review deleted successfully");
    }

    @Override
    public ResultPaginationDto getServiceReviews(Long serviceId, Pageable pageable) {
        log.info("[SERVICE_REVIEW] Getting reviews for service: {}", serviceId);

        serviceRepository.findById(serviceId)
                .orElseThrow(() -> new NotFoundException("[SERVICE_REVIEW] Service not found"));

        Page<PetServiceReview> page = reviewRepository.findByPetServiceId(serviceId, pageable);
        List<ServiceReviewDto> dtos = reviewMapper.toDtos(page.getContent());

        return buildPaginationResponse(dtos, page);
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

    @Override
    public Double getAverageRating(Long serviceId) {
        return reviewRepository.getAverageRating(serviceId);
    }

    @Override
    public Integer getReviewCount(Long serviceId) {
        return reviewRepository.getReviewCount(serviceId);
    }
}
