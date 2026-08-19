package N18.haui.Pet_18.service;

import N18.haui.Pet_18.domain.dto.pagination.ResultPaginationDto;
import N18.haui.Pet_18.domain.dto.request.ReqForMenu;
import N18.haui.Pet_18.domain.dto.response.CommonResponseDto;
import N18.haui.Pet_18.domain.dto.response.MenuDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MenuService {

    MenuDto createMenu(ReqForMenu req);

    MenuDto updateMenu(ReqForMenu req, Long id);

    CommonResponseDto deleteMenu(Long id);

    MenuDto getMenuById(Long id);

    List<MenuDto> getMenuTree();

    List<MenuDto> getActiveMenus();

    ResultPaginationDto getAllMenus(Pageable pageable);

}