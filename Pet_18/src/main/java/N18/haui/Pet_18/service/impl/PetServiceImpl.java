package N18.haui.Pet_18.service.impl;

import N18.haui.Pet_18.constant.GenderEnum;
import N18.haui.Pet_18.domain.dto.pagination.ResultPaginationDto;
import N18.haui.Pet_18.domain.dto.request.ReqCreatePet;
import N18.haui.Pet_18.domain.dto.request.ReqUpdatePet;
import N18.haui.Pet_18.domain.dto.response.CommonResponseDto;
import N18.haui.Pet_18.domain.dto.response.PetDto;
import N18.haui.Pet_18.domain.entity.Pet;
import N18.haui.Pet_18.domain.entity.User;
import N18.haui.Pet_18.domain.mapper.PetMapper;
import N18.haui.Pet_18.domain.specification.FilterProcessor;
import N18.haui.Pet_18.domain.specification.SpecificationBuilder;
import N18.haui.Pet_18.exception.BadRequestException;
import N18.haui.Pet_18.exception.ForbiddenException;
import N18.haui.Pet_18.exception.NotFoundException;
import N18.haui.Pet_18.repository.PetRepository;
import N18.haui.Pet_18.service.PetService;
import N18.haui.Pet_18.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;
    private final UserService userService;
    private final PetMapper petMapper;

    private void checkExistPet(String userId, String petName){
        if(petRepository.existsByUserIdAndNameAndDeleteFlagFalse(userId, petName)){
            throw new BadRequestException("[PET]  Pet with name " + petName + " already exists for this user.");
        }
    }


    @Override
    @Transactional
    public PetDto createPet(ReqCreatePet req) {
        log.info("[PET] Thêm thú cưng mới cho user hiện tại");

        User currentUser = userService.getUserLogin();
        checkExistPet(currentUser.getId(), req.getName());


        Pet pet = new Pet();
        pet.setName(req.getName());
        pet.setSpecie(req.getSpecie());
        pet.setGender(GenderEnum.valueOf(req.getGender()));
        pet.setBirthday(req.getBirthday());
        pet.setWeight(req.getWeight());
        pet.setHealthStatus(req.getHealthStatus());
        pet.setUser(currentUser);

        petRepository.save(pet);
        log.info("[PET] Thêm thú cưng thành công | User ID: {}", currentUser.getId());
        return petMapper.toDto(pet);
    }

    @Override
    @Transactional
    public PetDto updatePet(ReqUpdatePet req) {
        log.info("[PET] Cập nhật thú cưng ID: {}", req.getId());

        User currentUser = userService.getUserLogin();
        Pet pet = getPetAndValidate(req.getId(), currentUser);



        pet.setName(req.getName());
        pet.setSpecie(req.getSpecie());
        pet.setGender(GenderEnum.valueOf(req.getGender()));
        pet.setBirthday(req.getBirthday());
        pet.setWeight(req.getWeight());
        pet.setHealthStatus(req.getHealthStatus());

        petRepository.save(pet);
        log.info("[PET] Cập nhật thành công thú cưng ID: {}", req.getId());
        return petMapper.toDto(pet);
    }

    @Override
    @Transactional
    public CommonResponseDto deletePet(Long id) {
        log.info("[PET] Xóa thú cưng ID: {}", id);

        User currentUser = userService.getUserLogin();
        Pet pet = getPetAndValidate(id, currentUser);

        pet.setDeleteFlag(Boolean.TRUE);
        petRepository.save(pet);

        log.info("[PET] Xóa thành công thú cưng ID: {}", id);
        return new CommonResponseDto(true, "Deleted pet: " + pet.getName() + " successfully");
    }

    @Override
    @Transactional
    public CommonResponseDto deactivatePet(Long id) {
        log.info("[PET] Khóa thú cưng ID: {}", id);

        // Security config đã đảm bảo chỉ Admin vào được
        Pet pet = getPetById(id);

        if (Boolean.FALSE.equals(pet.getActiveFlag())) {
            throw new BadRequestException("[PET] Thú cưng ID: " + id + " đã bị khóa trước đó");
        }
        pet.setActiveFlag(Boolean.FALSE);
        petRepository.save(pet);

        log.info("[PET] Khóa thành công thú cưng ID: {}", id);
        return new CommonResponseDto(true, "Lock pet: " + pet.getName() + " successfully");
    }

    @Override
    @Transactional
    public CommonResponseDto activatePet(Long id) {
        log.info("[PET] Kích hoạt thú cưng ID: {}", id);

        // Security config đã đảm bảo chỉ Admin vào được
        Pet pet = getPetById(id);

        if (Boolean.TRUE.equals(pet.getActiveFlag())) {
            throw new BadRequestException("[PET] Thú cưng ID: " + id + " đã được kích hoạt trước đó");
        }
        pet.setActiveFlag(Boolean.TRUE);
        petRepository.save(pet);

        log.info("[PET] Mở khóa thành công thú cưng ID: {}", id);
        return new CommonResponseDto(true, "Unlock pet: " + pet.getName() + " successfully");
    }

    @Override
    public List<PetDto> getMyPets() {
        log.info("[PET] Lấy danh sách thú cưng của user hiện tại");

        User currentUser = userService.getUserLogin();
        List<Pet> pets = petRepository
                .findByUserIdAndDeleteFlagFalse(currentUser.getId());

        log.info("[PET] User ID: {} | Số thú cưng: {}", currentUser.getId(), pets.size());
        return petMapper.toDtoList(pets);
    }

    @Override
    public PetDto getPetDetail(Long id) {
        log.info("[PET] Lấy chi tiết thú cưng ID: {}", id);

        User currentUser = userService.getUserLogin();
        Pet pet = getPetAndValidate(id, currentUser);

        log.info("[PET] Lấy chi tiết thành công thú cưng ID: {}", id);
        return petMapper.toDto(pet);
    }

    @Override
    public ResultPaginationDto getAllPet(List<String> filter, Pageable pageable) {
        SpecificationBuilder<Pet> specificationBuilder = new SpecificationBuilder<>();
        FilterProcessor.process(specificationBuilder, filter);

        pageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("createdDate").descending()
        );

        Specification<Pet> spec = specificationBuilder.build();
        Specification<Pet> softDeleteSpec = (root, query, cb) -> cb.equal(root.get("deleteFlag"), false);

        Specification<Pet> finalSpec = (spec == null) ? softDeleteSpec : spec.and(softDeleteSpec);

        Page<Pet> petPage = petRepository.findAll(finalSpec, pageable);

        ResultPaginationDto resultPaginationDTO = new ResultPaginationDto();
        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(petPage.getTotalPages());
        meta.setTotal(petPage.getTotalElements());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(petMapper.toDtoList(petPage.getContent()));
        return resultPaginationDTO;
    }

    // Helper - tìm pet + check owner (dùng cho User)
    private Pet getPetAndValidate(Long petId, User currentUser) {
        Pet pet = getPetById(petId);

        if (!pet.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("[PET] Bạn không có quyền thao tác thú cưng này");
        }
        return pet;
    }

    // Helper - chỉ tìm pet + check deleteFlag + activeFlag (dùng cho Admin)
    private Pet getPetById(Long petId) {
        Pet pet = petRepository.findByIdAndDeleteFlagFalse(petId)
                .orElseThrow(() -> {
                    log.warn("[NOT_FOUND] Không tìm thấy thú cưng ID: {}", petId);
                    return new NotFoundException("[PET] Không tìm thấy thú cưng ID: " + petId);
                });

        if (Boolean.TRUE.equals(pet.getDeleteFlag())) {
            throw new NotFoundException("[PET] Thú cưng ID: " + petId + " đã bị xóa");
        }

        if (Boolean.FALSE.equals(pet.getActiveFlag())) {
            throw new BadRequestException("[PET] Thú cưng ID: " + petId + " đã bị khóa");
        }

        return pet;
    }
}