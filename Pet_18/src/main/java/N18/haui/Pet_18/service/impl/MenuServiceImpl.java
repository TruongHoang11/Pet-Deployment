package N18.haui.Pet_18.service.impl;

import N18.haui.Pet_18.domain.dto.pagination.ResultPaginationDto;
import N18.haui.Pet_18.domain.dto.request.ReqForMenu;
import N18.haui.Pet_18.domain.dto.response.CommonResponseDto;
import N18.haui.Pet_18.domain.dto.response.MenuDto;
import N18.haui.Pet_18.domain.entity.Menu;
import N18.haui.Pet_18.domain.mapper.MenuMapper;
import N18.haui.Pet_18.exception.NotFoundException;
import N18.haui.Pet_18.repository.MenuRepository;
import N18.haui.Pet_18.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final MenuMapper menuMapper;

    @Override
    public MenuDto createMenu(ReqForMenu req) {

        Menu menu = new Menu();
        menu.setName(req.getName());
        menu.setPath(req.getPath());
        menu.setIcon(req.getIcon());
        menu.setParent(menuRepository.findById(req.getParentId()).orElse(null));
        Menu saved = menuRepository.save(menu);
        return menuMapper.toDto(saved);
    }

    @Override
    public MenuDto updateMenu(ReqForMenu req, Long id) {

        Menu menu = menuRepository.findById(id)
                .orElseThrow(
                        () -> new NotFoundException("Menu not found"));

        menu.setName(req.getName());
        menu.setPath(req.getPath());
        menu.setIcon(req.getIcon());
        menu.setParent(menuRepository.findById(req.getParentId()).orElse(null));

        Menu updated = menuRepository.save(menu);

        return menuMapper.toDto(updated);
    }

    @Override
    public CommonResponseDto deleteMenu(Long id) {

        Menu menu = menuRepository.findById(id)
                .orElseThrow(
                        () -> new NotFoundException("Menu not found"));

        menu.setDeleteFlag(true);
        menuRepository.save(menu);
        return new CommonResponseDto(true, "Menu deleted successfully");
    }

    @Override
    public MenuDto getMenuById(Long id) {

        Menu menu = menuRepository.findById(id)
                .orElseThrow(
                        () -> new NotFoundException("Menu not found"));

        return menuMapper.toDto(menu);
    }

    @Override
    public List<MenuDto> getMenuTree() {
        Specification<Menu> spec =
                (root, query, cb) ->
                        cb.and(
                                cb.isNull(root.get("parent")),
                                cb.equal(root.get("deleteFlag"), false),
                                cb.equal(root.get("activeFlag"), true)
                        );

        List<Menu> roots = menuRepository.findAll(spec);
        return menuMapper.toDtos(roots);
    }

    @Override
    public List<MenuDto> getActiveMenus() {

        return menuMapper.toDtos(
                menuRepository.findByActiveFlagTrueOrderBySortOrderAsc());
    }

    @Override
    public ResultPaginationDto getAllMenus(Pageable pageable) {

        Specification<Menu> deleteSpec =
                (root, query, cb) -> cb.equal(root.get("deleteFlag"), false);

        Page<Menu> page = menuRepository.findAll(deleteSpec, pageable);

        ResultPaginationDto result = new ResultPaginationDto();

        List<MenuDto> menuDtos = menuMapper.toDtos(page.getContent());
        menuDtos.forEach(menuDto -> menuDto.setChildren(null));
        result.setResult(menuDtos);

        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        result.setMeta(meta);

        return result;
    }
}