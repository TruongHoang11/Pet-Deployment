package N18.haui.Pet_18.controller;

import N18.haui.Pet_18.base.RestApiV1;
import N18.haui.Pet_18.base.VsResponseUtil;
import N18.haui.Pet_18.domain.dto.request.ReqForMenu;
import N18.haui.Pet_18.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import N18.haui.Pet_18.constant.UrlConstant;

@RestApiV1
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @PostMapping(UrlConstant.Menu.CREATE_MENU)
    public ResponseEntity<?> createMenu(
            @RequestBody ReqForMenu req) {

        return VsResponseUtil.success(
                HttpStatus.CREATED,
                menuService.createMenu(req)
        );
    }

    @PutMapping(UrlConstant.Menu.UPDATE_MENU)
    public ResponseEntity<?> updateMenu(
                   @PathVariable Long id,
                   @RequestBody ReqForMenu req) {

        return VsResponseUtil.success(
                HttpStatus.OK,
                menuService.updateMenu(req, id)
        );
    }

    @DeleteMapping(UrlConstant.Menu.DELETE_MENU)
    public ResponseEntity<?> deleteMenu(@PathVariable Long id) {
        return VsResponseUtil.success(
                HttpStatus.NO_CONTENT,
                menuService.deleteMenu(id)
        );
    }

    @GetMapping(UrlConstant.Menu.GET_MENU)
    public ResponseEntity<?> getMenu(
            @PathVariable Long id) {

        return VsResponseUtil.success(
                HttpStatus.OK,
                menuService.getMenuById(id)
        );
    }

    @GetMapping(UrlConstant.Menu.GET_MENUS_TREE)
    public ResponseEntity<?> getMenuTree() {

        return VsResponseUtil.success(
                HttpStatus.OK,
                menuService.getMenuTree()
        );
    }

    @GetMapping(UrlConstant.Menu.GET_ACTIVE_MENUS)
    public ResponseEntity<?> getActiveMenus() {

        return VsResponseUtil.success(
                HttpStatus.OK,
                menuService.getActiveMenus()
        );
    }

    @GetMapping(UrlConstant.Menu.GET_ALL_MENUS)
    public ResponseEntity<?> getAllMenus(
            Pageable pageable) {

        return VsResponseUtil.success(
                HttpStatus.OK,
                menuService.getAllMenus(pageable)
        );
    }
}