package N18.haui.Pet_18.controller;

import N18.haui.Pet_18.base.RestApiV1;
import N18.haui.Pet_18.base.VsResponseUtil;
import N18.haui.Pet_18.constant.UrlConstant;
import N18.haui.Pet_18.domain.dto.request.ReqAdjustProduct;
import N18.haui.Pet_18.domain.dto.request.ReqInventoryProduct;
import N18.haui.Pet_18.domain.dto.response.InventoryTransactionDto;
import N18.haui.Pet_18.service.InventoryService;
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
public class InventoryController {
    private final InventoryService inventoryService;


    @GetMapping(UrlConstant.Inventory.GET_INVENTORY_BY_PRODUCT_ID)
    public ResponseEntity<?> getInventoryByProductId(@PathVariable Long id){
        return VsResponseUtil.success(HttpStatus.OK,inventoryService.getInventoryByProductId(id));
    }



    @PostMapping(UrlConstant.Inventory.IMPORT_PRODUCT)
    public ResponseEntity<?> importInventory(@RequestBody @Valid ReqInventoryProduct reqInventoryProduct)  {
        InventoryTransactionDto importInventory = inventoryService.importProduct(reqInventoryProduct);

        return VsResponseUtil.success(HttpStatus.OK, importInventory);
    }

    @PostMapping(UrlConstant.Inventory.EXPORT_PRODUCT)
    public ResponseEntity<?> exportInventory(@RequestBody @Valid ReqInventoryProduct reqInventoryProduct)  {
        InventoryTransactionDto exportInventory = inventoryService.exportProduct(reqInventoryProduct);

        return VsResponseUtil.success(HttpStatus.OK, exportInventory);
    }

    @PostMapping(UrlConstant.Inventory.ADJUST_PRODUCT)
    public ResponseEntity<?> adjustInventory(@RequestBody @Valid ReqAdjustProduct reqAdjustProduct)  {
        InventoryTransactionDto adjustInventory = inventoryService.adjustProduct(reqAdjustProduct);

        return VsResponseUtil.success(HttpStatus.OK, adjustInventory);
    }

    @GetMapping(UrlConstant.Inventory.GET_INVENTORY_TRANSACTION_HISTORY)
    public ResponseEntity<?> getInventoryTransactionHistory(
            @RequestParam(value = "filter", required = false) List<String> filter,
            Pageable pageable){

        return VsResponseUtil.success(HttpStatus.OK, inventoryService.getInventoryTransactionHistory(filter,pageable) );

    }


    @GetMapping(UrlConstant.Inventory.GET_INVENTORY_LIST)
    public ResponseEntity<?> getInventoryList(
            @RequestParam(value = "filter", required = false) List<String> filter,
            Pageable pageable){

        return VsResponseUtil.success(HttpStatus.OK, inventoryService.getInventoryList(filter,pageable) );
    }
}
