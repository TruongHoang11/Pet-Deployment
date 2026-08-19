package N18.haui.Pet_18.service;


import N18.haui.Pet_18.domain.dto.pagination.ResultPaginationDto;
import N18.haui.Pet_18.domain.dto.request.ReqAdjustProduct;
import N18.haui.Pet_18.domain.dto.request.ReqInventoryProduct;
import N18.haui.Pet_18.domain.dto.response.InventoryDto;
import N18.haui.Pet_18.domain.dto.response.InventoryTransactionDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InventoryService {

    // thêm sản phẩm vào kho lưu vết inventory transaction
    InventoryTransactionDto importProduct(ReqInventoryProduct reqInventoryProduct);

    // xuất sản phẩm ra kho lưu vết inventory transaction
    InventoryTransactionDto exportProduct(ReqInventoryProduct reqInventoryProduct);

    // điều chỉnh sl sản phẩm  kho lưu vết inventory transaction
    InventoryTransactionDto adjustProduct(ReqAdjustProduct reqAdjustProduct);

    // lấy tồn kho product (quantity)
    InventoryDto getInventoryByProductId(Long productId);


    ResultPaginationDto getInventoryList(List<String> filter, Pageable pageable);


    ResultPaginationDto getInventoryTransactionHistory(List<String> filter, Pageable pageable);



}
