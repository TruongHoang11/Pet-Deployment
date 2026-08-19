package N18.haui.Pet_18.service;


import N18.haui.Pet_18.domain.dto.pagination.ResultPaginationDto;
import N18.haui.Pet_18.domain.dto.request.ReqCreatePet;
import N18.haui.Pet_18.domain.dto.request.ReqUpdatePet;
import N18.haui.Pet_18.domain.dto.response.CommonResponseDto;
import N18.haui.Pet_18.domain.dto.response.PetDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PetService {
    PetDto createPet(ReqCreatePet req);

    PetDto updatePet(ReqUpdatePet req);

    CommonResponseDto  deletePet(Long id);

    CommonResponseDto deactivatePet(Long id);

    CommonResponseDto activatePet(Long id);


    List<PetDto> getMyPets();

    PetDto getPetDetail(Long id);

    ResultPaginationDto getAllPet(List<String> filter, Pageable pageable);




}