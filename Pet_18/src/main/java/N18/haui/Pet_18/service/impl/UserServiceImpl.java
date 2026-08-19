package N18.haui.Pet_18.service.impl;

import N18.haui.Pet_18.constant.GenderEnum;
import N18.haui.Pet_18.constant.RoleConstant;
import N18.haui.Pet_18.domain.dto.pagination.ResultPaginationDto;
import N18.haui.Pet_18.domain.dto.request.ReqUserUpdateProfile;
import N18.haui.Pet_18.domain.dto.request.UserCreateDto;
import N18.haui.Pet_18.domain.dto.request.UserUpdateDto;
import N18.haui.Pet_18.domain.dto.response.CommonResponseDto;
import N18.haui.Pet_18.domain.dto.response.ResUploadFileResultDto;
import N18.haui.Pet_18.domain.dto.response.UserDto;
import N18.haui.Pet_18.domain.entity.Role;
import N18.haui.Pet_18.domain.entity.User;
import N18.haui.Pet_18.domain.mapper.UserMapper;
import N18.haui.Pet_18.domain.specification.FilterProcessor;
import N18.haui.Pet_18.domain.specification.SpecificationBuilder;
import N18.haui.Pet_18.exception.ConflictException;
import N18.haui.Pet_18.exception.NotFoundException;
import N18.haui.Pet_18.exception.UnauthorizedException;
import N18.haui.Pet_18.repository.RoleRepository;
import N18.haui.Pet_18.repository.UserRepository;
import N18.haui.Pet_18.security.SecurityUtil;
import N18.haui.Pet_18.service.FileService;
import N18.haui.Pet_18.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final FileService fileService;




    public void checkExistedUserByEmail(String email) {
        if (userRepository.existsByEmailAndDeleteFlagFalse(email)) {
            throw new ConflictException("Email already exists");
        }
    }

    @Override
    public UserDto getUserById(String userId) {
        User user = userRepository.findByIdAndDeleteFlagFalse(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto createUser(UserCreateDto userCreateDto) {
        checkExistedUserByEmail(userCreateDto.getEmail());
        User createUser = userMapper.toUser(userCreateDto);


        Role defaultRole = (userCreateDto.getRole() != null && userCreateDto.getRole().getId() != null)
                ? roleRepository.findByIdAndDeleteFlagFalse(userCreateDto.getRole().getId())
                .orElseThrow(() -> new NotFoundException("Role not found with id: " + userCreateDto.getRole().getId()))
                : roleRepository.findByNameAndDeleteFlagFalse(RoleConstant.USER)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(RoleConstant.USER);
                    return roleRepository.save(newRole);
                });
        createUser.setRole(defaultRole);
        userRepository.save(createUser);

        return userMapper.toUserDto(createUser);
    }

    @Override
    @Transactional
    public UserDto updateUser(UserUpdateDto userUpdateDto) {
        User updateUser = userRepository.findByIdAndDeleteFlagFalse(userUpdateDto.getId()).orElseThrow(
                () -> new NotFoundException("User not found with id: " + userUpdateDto.getId())
        );
        if(userUpdateDto.getRole() != null && userUpdateDto.getRole().getId() != null){
            Role role = roleRepository.findById(userUpdateDto.getRole().getId()).orElseThrow(
                    () -> new NotFoundException("Role not found with id: " + userUpdateDto.getRole().getId())
            );
            updateUser.setRole(role);
        }
        updateUser.setName(userUpdateDto.getName());
        updateUser.setDateOfBirth(userUpdateDto.getDateOfBirth());
        updateUser.setGender(GenderEnum.valueOf(userUpdateDto.getGender()));
        updateUser.setAvatarUrl(userUpdateDto.getAvatarUrl());


        userRepository.save(updateUser);
        return userMapper.toUserDto(updateUser);
    }

    @Override
    @Transactional
    public CommonResponseDto deleteUser(String id) {
        User deleteUser = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("User not found with id: " + id)
        );
        // set flag(true) -> da xoa
        deleteUser.setDeleteFlag(true);
        userRepository.save(deleteUser);
        return new CommonResponseDto(true, "Delete user success");
    }



    @Override
    public ResultPaginationDto getAllUser(List<String> filter, Pageable pageable) {
        SpecificationBuilder<User> specificationBuilder = new SpecificationBuilder<>();
        FilterProcessor.process(specificationBuilder, filter);

        Specification<User> spec = specificationBuilder.build();
        Specification<User> softDeleteSpec = (root, query, cb) -> cb.equal(root.get("deleteFlag"), false);

        Specification<User> finalSpec = (spec == null) ? softDeleteSpec : spec.and(softDeleteSpec);

        // Thực thi query
        Page<User> pageUser = userRepository.findAll(finalSpec, pageable);


        // Mapping kết quả
        ResultPaginationDto resultPaginationDTO = new ResultPaginationDto();
        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());

        List<UserDto> result = userMapper.toUserDtos(pageUser.getContent());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(result);

        return resultPaginationDTO;
    }

    @Override
    @Transactional
    public CommonResponseDto changeUserStatus(String id) {
        User updateUser = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("User not found with id: " + id)
        );
        updateUser.setActiveFlag(!updateUser.getActiveFlag());
        userRepository.save(updateUser);
        return new CommonResponseDto(true, "Change user status success");
    }

    @Override
    public User getUserLogin() {
        String id = SecurityUtil.getCurrentUserLogin().orElseThrow(
                () -> new UnauthorizedException("Login required")
        );
        User currentUser = userRepository.findByIdAndDeleteFlagFalse(id).orElseThrow(
                () -> new NotFoundException("User not found with id: " + id)
        );
        return currentUser;
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmailAndDeleteFlagFalse(email).orElseThrow(
                () -> new NotFoundException("User not found with email: " + email)
        );
    }

    @Override
    @Transactional
    public void updateUserToken(String token, String email) {
        User currentUser = this.getUserByEmail(email);

            currentUser.setRefreshToken(token);
            userRepository.save(currentUser);
    }

    @Override
    public User getUserWithRoleAndPermissions(String id) {
        return userRepository.findByIdWithFullInfor(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }

    @Override
    public CommonResponseDto addAvatar(String userId, MultipartFile file) throws URISyntaxException, IOException {

       User user  = userRepository.findById(userId).orElseThrow(
               () -> new NotFoundException("User not found with id: " + userId)
       );


        ResUploadFileResultDto.ResUploadFileDto uploadFileResultDto = fileService.uploadFile(file, "avatars");
        if(uploadFileResultDto != null){
            String avatarUrl = uploadFileResultDto.getFileName();
            user.setAvatarUrl(avatarUrl);
            userRepository.save(user);
            return new CommonResponseDto(true, "Upload avatar success");
        }
        return new CommonResponseDto(false, "Không có ảnh nào được thêm (file lỗi hoặc trống");
    }

    @Override
    public UserDto updateProfile(ReqUserUpdateProfile reqUserUpdateProfile) {
        User currentUser = this.getUserLogin();
        currentUser.setName(reqUserUpdateProfile.getName());
        currentUser.setDateOfBirth(reqUserUpdateProfile.getDateOfBirth());
        currentUser.setGender(GenderEnum.valueOf(reqUserUpdateProfile.getGender()));
        userRepository.save(currentUser);
        return userMapper.toUserDto(currentUser);

    }

    @Override
    public UserDto getUserProfile() {
        User currentUser = this.getUserLogin();
        return userMapper.toUserDto(currentUser);
    }
}
