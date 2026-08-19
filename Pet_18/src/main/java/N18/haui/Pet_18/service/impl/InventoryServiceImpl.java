package N18.haui.Pet_18.service.impl;

import N18.haui.Pet_18.constant.TypeInventory;
import N18.haui.Pet_18.domain.dto.pagination.ResultPaginationDto;
import N18.haui.Pet_18.domain.dto.request.ReqAdjustProduct;
import N18.haui.Pet_18.domain.dto.request.ReqInventoryProduct;
import N18.haui.Pet_18.domain.dto.response.InventoryDto;
import N18.haui.Pet_18.domain.dto.response.InventoryTransactionDto;
import N18.haui.Pet_18.domain.entity.Inventory;
import N18.haui.Pet_18.domain.entity.InventoryTransaction;
import N18.haui.Pet_18.domain.entity.Product;
import N18.haui.Pet_18.domain.mapper.InventoryMapper;
import N18.haui.Pet_18.domain.mapper.InventoryTransactionMapper;
import N18.haui.Pet_18.domain.specification.FilterProcessor;
import N18.haui.Pet_18.domain.specification.SpecificationBuilder;
import N18.haui.Pet_18.exception.BadRequestException;
import N18.haui.Pet_18.exception.NotFoundException;
import N18.haui.Pet_18.repository.InventoryRepository;
import N18.haui.Pet_18.repository.InventoryTransactionRepository;
import N18.haui.Pet_18.repository.ProductRepository;
import N18.haui.Pet_18.service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final InventoryTransactionMapper mapper;
    private final InventoryMapper inventoryMapper;
    @Override
    @Transactional
    public InventoryTransactionDto importProduct(ReqInventoryProduct reqInventoryProduct) {

        // kiểm tra xem product có tồn tại không
        Product product = productRepository.findById(reqInventoryProduct.getProductId()).orElseThrow(
                () -> new NotFoundException("Product not found with id: " + reqInventoryProduct.getProductId())
        );

        // Tìm iventory tương ứng với product
        Inventory inventory = inventoryRepository.findByProductId(reqInventoryProduct.getProductId()).orElseThrow(
                () -> new NotFoundException("Inventory not found with productId: " + reqInventoryProduct.getProductId())
        );

        // validate quantity
        if (reqInventoryProduct.getQuantity() <= 0){
            throw new BadRequestException("Invalid quantity is negative (<= 0)");
        }

        // cộng quantity
        Integer oldQty = inventory.getQuantity();
        Integer newQty = oldQty + reqInventoryProduct.getQuantity();
        inventory.setQuantity(newQty);
        inventoryRepository.save(inventory);
        log.info("[IMPORT] Tồn kho thay đổi {} → {}", oldQty, newQty);

        // tạo inventory transaction
        InventoryTransaction inventoryTransaction = createInventoryTransaction(inventory, reqInventoryProduct.getQuantity(), TypeInventory.IMPORT, reqInventoryProduct.getNote());
        InventoryTransaction savedTransaction = inventoryTransactionRepository.save(inventoryTransaction);
        log.info("[IMPORT] Ghi transaction thành công | Product ID: {} | quantity: {}",
                reqInventoryProduct.getProductId(), reqInventoryProduct.getQuantity());

     InventoryTransactionDto inventoryTransactionDto = new InventoryTransactionDto();
//        inventoryTransactionDto.setQuantity(reqImportProduct.getQuantity());
//        inventoryTransactionDto.setCurrentStock(newQty);
//        inventoryTransactionDto.setType(TypeInventory.IMPORT.name());
//        inventoryTransactionDto.setNote(reqImportProduct.getNote());
//        inventoryTransactionDto.setCreatedBy(savedTransaction.getCreatedBy());
//        inventoryTransactionDto.setCreatedDate(savedTransaction.getCreatedDate());
//        inventoryTransactionDto.setLastModifiedBy(savedTransaction.getLastModifiedBy());
//        inventoryTransactionDto.setLastModifiedDate(savedTransaction.getLastModifiedDate());
        inventoryTransactionDto =  mapper.toInventoryTransactionDto(savedTransaction);
   //     inventoryTransactionDto.setCurrentStock(newQty);

        return inventoryTransactionDto;
    }

    @Override
    @Transactional
    public InventoryTransactionDto exportProduct(ReqInventoryProduct reqInventoryProduct) {
        Product product = productRepository.findById(reqInventoryProduct.getProductId()).orElseThrow(
                () -> new NotFoundException("Product not found with id: " + reqInventoryProduct.getProductId())
        );
        Inventory inventory = inventoryRepository.findByProductId(reqInventoryProduct.getProductId()).orElseThrow(
                () -> new NotFoundException("Inventory not found with productId: " + reqInventoryProduct.getProductId())
        );

        if (reqInventoryProduct.getQuantity() <= 0){
            throw new BadRequestException("Invalid quantity is negative (<= 0)");
        }

        // kiểm tra tồn kho đủ k
        Integer oldQty = inventory.getQuantity();
        if(oldQty < reqInventoryProduct.getQuantity()){
            log.info("[EXPORT] Tồn kho không đủ | Product ID: {} | quantity: {} | current stock: {}",
                    reqInventoryProduct.getProductId(), reqInventoryProduct.getQuantity(), oldQty);
            throw new BadRequestException("Not enough quantity in stock");
        }
        Integer newQty = oldQty - reqInventoryProduct.getQuantity();
        inventory.setQuantity(newQty);
        inventoryRepository.save(inventory);
        log.info("[EXPORT] Tồn kho thay đổi {} → {}", oldQty, newQty);

        InventoryTransaction inventoryTransaction = createInventoryTransaction(inventory, reqInventoryProduct.getQuantity(), TypeInventory.EXPORT, reqInventoryProduct.getNote());
        InventoryTransaction savedTransaction = inventoryTransactionRepository.save(inventoryTransaction);
        log.info("[EXPORT] Ghi transaction thành công | Product ID: {} | quantity: {}",
                reqInventoryProduct.getProductId(), reqInventoryProduct.getQuantity());
        InventoryTransactionDto inventoryTransactionDto = new InventoryTransactionDto();
        inventoryTransactionDto =  mapper.toInventoryTransactionDto(savedTransaction);
     //   inventoryTransactionDto.setCurrentStock(newQty);

        return inventoryTransactionDto;

    }

    @Override
    @Transactional
    public InventoryTransactionDto adjustProduct(ReqAdjustProduct reqAdjustProduct) {
        Product product = productRepository.findById(reqAdjustProduct.getProductId()).orElseThrow(
                () -> new NotFoundException("Product not found with id: " + reqAdjustProduct.getProductId())
        );
        Inventory inventory = inventoryRepository.findByProductId(reqAdjustProduct.getProductId()).orElseThrow(
                () -> new NotFoundException("Inventory not found with productId: " + reqAdjustProduct.getProductId())
        );

        if (reqAdjustProduct.getNewQuantity() < 0){
            throw new BadRequestException("Invalid quantity is negative (<= 0)");
        }
        Integer oldQty = inventory.getQuantity();
        Integer newQty = reqAdjustProduct.getNewQuantity();

        Integer delta = newQty - oldQty;

        if(delta == 0){
            log.info("[ADJUST] Số lượng không thay đổi | Product ID: {} | quantity: {}",
                    reqAdjustProduct.getProductId(), reqAdjustProduct.getNewQuantity());
            throw new BadRequestException("New quantity is the same as current quantity");
        }

        // update inventory
        inventory.setQuantity(newQty);
        inventoryRepository.save(inventory);

        // tao inventory transaction
        String note =  ("[" + (delta > 0 ? "+" : "") + delta + "] " + reqAdjustProduct.getNote()); // VD: [+5] Kiểm kê tháng 6
        InventoryTransaction inventoryTransaction = createInventoryTransaction(inventory, Math.abs(delta), TypeInventory.ADJUST, note);
        InventoryTransaction savedTransaction = inventoryTransactionRepository.save(inventoryTransaction);

        InventoryTransactionDto transactionDto = mapper.toInventoryTransactionDto(savedTransaction);
     //   transactionDto.setCurrentStock(newQty);
        log.info("[ADJUST] Ghi transaction thành công | Product ID: {} | delta: {}",
                reqAdjustProduct.getProductId(), delta);
        return transactionDto;
    }

    @Override
    public InventoryDto getInventoryByProductId(Long productId) {
        log.info("[INVENTORY] Xem tồn kho cho Product ID: {}", productId);
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new NotFoundException("Product not found with id: " + productId)
                );
        if(product.getDeleteFlag() == true || product.getActiveFlag() == false){
            throw new NotFoundException("Product not found with id: " + productId);
        }
        Inventory inventory = inventoryRepository.findByProductId(productId).orElseThrow(
                () -> new NotFoundException("Inventory not found with productId: " + productId)
        );

        log.info("[INVENTORY] Product ID: {} | Tồn kho hiện tại: {}", productId, inventory.getQuantity());
        return inventoryMapper.toDto(inventory);
    }

    @Override
    public ResultPaginationDto getInventoryList(List<String> filter, Pageable pageable) {
        SpecificationBuilder<Inventory> specificationBuilder = new SpecificationBuilder<>();

        FilterProcessor.process(specificationBuilder,filter);

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdDate").descending());

        Page<Inventory> page = inventoryRepository.findAll(specificationBuilder.build(), pageable);

        ResultPaginationDto resultPaginationDto = new ResultPaginationDto();
        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        List<InventoryDto> dtoList = inventoryMapper.toListInventory(page.getContent());
        resultPaginationDto.setMeta(meta);
        resultPaginationDto.setResult(dtoList);
        return resultPaginationDto;
    }

    @Override
    public ResultPaginationDto getInventoryTransactionHistory(List<String> filter, Pageable pageable) {
        SpecificationBuilder<InventoryTransaction> specificationBuilder = new SpecificationBuilder<>();
        FilterProcessor.process(specificationBuilder,filter);

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdDate").descending());

        Page<InventoryTransaction> page = inventoryTransactionRepository.findAll(specificationBuilder.build(), pageable);

        ResultPaginationDto resultPaginationDto = new ResultPaginationDto();
        ResultPaginationDto.Meta meta = new ResultPaginationDto.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        List<InventoryTransactionDto> dtoList = mapper.toListInventoryTransaction(page.getContent());
        resultPaginationDto.setMeta(meta);
        resultPaginationDto.setResult(dtoList);
        return resultPaginationDto;
    }

    private InventoryTransaction createInventoryTransaction(Inventory inventory, Integer quantity, TypeInventory type, String note){
        InventoryTransaction inventoryTransaction = new InventoryTransaction();
        inventoryTransaction.setQuantity(quantity);
        inventoryTransaction.setType(type);
        inventoryTransaction.setNote(note);
        inventoryTransaction.setInventory(inventory);
        return inventoryTransactionRepository.save(inventoryTransaction);
    }
}
