package N18.haui.Pet_18.controller;

import N18.haui.Pet_18.base.RestApiV1;
import N18.haui.Pet_18.base.VsResponseUtil;
import N18.haui.Pet_18.constant.UrlConstant;
import N18.haui.Pet_18.domain.dto.request.ReqCreatePet;
import N18.haui.Pet_18.domain.dto.request.ReqUpdatePet;
import N18.haui.Pet_18.domain.dto.response.CommonResponseDto;
import N18.haui.Pet_18.domain.dto.response.PetDto;
import N18.haui.Pet_18.service.PetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestApiV1
@RequiredArgsConstructor
@Slf4j
public class PetController {
    private final PetService petService;

    @GetMapping(UrlConstant.Pet.GET_PET_DETAIL)
    public ResponseEntity<?> getPetDetail(@PathVariable Long id){
        return VsResponseUtil.success(HttpStatus.OK,petService.getPetDetail(id) );

    }

    @PostMapping(UrlConstant.Pet.CREATE_PET)
    public ResponseEntity<?> createPet(@RequestBody @Valid ReqCreatePet reqCreatePet)  {
        PetDto petDto = petService.createPet(reqCreatePet);
        return VsResponseUtil.success(HttpStatus.OK, petDto);
    }

    @PutMapping(UrlConstant.Pet.UPDATE_PET)
    public ResponseEntity<?> updatePet(@RequestBody @Valid ReqUpdatePet reqUpdatePet) {
        return VsResponseUtil.success(HttpStatus.OK,petService.updatePet(reqUpdatePet) );

    }

    @DeleteMapping(UrlConstant.Pet.DELETE_PET)
    public ResponseEntity<?> deletePet(@PathVariable Long id){
        CommonResponseDto commonResponseDto = petService.deletePet(id);

        return VsResponseUtil.success(HttpStatus.OK, commonResponseDto);
        
    }

    @GetMapping(UrlConstant.Pet.GET_MY_PETS)
    public ResponseEntity<?> getMyPets(){
        return VsResponseUtil.success(HttpStatus.OK,petService.getMyPets());

    }

    @PatchMapping(UrlConstant.Pet.PATCH_ACTIVATE_PET)
    public ResponseEntity<?> activatePet(@PathVariable Long id){
        CommonResponseDto commonResponseDto = petService.activatePet(id);
        return VsResponseUtil.success(HttpStatus.OK, commonResponseDto);
    }
    @PatchMapping(UrlConstant.Pet.PATCH_DEACTIVATE_PET)
    public ResponseEntity<?> deactivatePet(@PathVariable Long id){
        CommonResponseDto commonResponseDto = petService.deactivatePet(id);
        return VsResponseUtil.success(HttpStatus.OK, commonResponseDto);
    }


    @GetMapping(UrlConstant.Pet.GET_ALL_PETS)
    public ResponseEntity<?> getAllPets(
            @RequestParam(value = "filter", required = false) List<String> filter,
            Pageable pageable){

        return VsResponseUtil.success(HttpStatus.OK,petService.getAllPet(filter,pageable) );

    }



}
