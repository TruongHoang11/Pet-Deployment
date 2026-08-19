package N18.haui.Pet_18.controller;

import N18.haui.Pet_18.base.RestApiV1;
import N18.haui.Pet_18.base.VsResponseUtil;
import N18.haui.Pet_18.constant.UrlConstant;
import N18.haui.Pet_18.domain.dto.request.ReqCreateProduct;
import N18.haui.Pet_18.domain.dto.request.ReqRecommendationDto;
import N18.haui.Pet_18.domain.dto.request.ReqUpdateProduct;
import N18.haui.Pet_18.domain.dto.response.CommonResponseDto;
import N18.haui.Pet_18.domain.dto.response.ProductDto;
import N18.haui.Pet_18.domain.dto.response.ResRecommendationDto;
import N18.haui.Pet_18.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestApiV1
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;


    @GetMapping(UrlConstant.Product.GET_PRODUCT)
    public ResponseEntity<?> getProduct(@PathVariable Long id){
        return VsResponseUtil.success(HttpStatus.OK,productService.getProductById(id) );

    }

    @PostMapping(UrlConstant.Product.CREATE_PRODUCT)
    public ResponseEntity<?> createProduct(@RequestBody @Valid ReqCreateProduct reqCreateProduct)  {
        ProductDto productDto = productService.createProduct(reqCreateProduct);
        return VsResponseUtil.success(HttpStatus.OK, productDto);
    }

    @PutMapping(UrlConstant.Product.UPDATE_PRODUCT)
    public ResponseEntity<?> updateProduct(@RequestBody @Valid ReqUpdateProduct reqUpdateProduct) {
        return VsResponseUtil.success(HttpStatus.OK,productService.updateProduct(reqUpdateProduct) );

    }

    @DeleteMapping(UrlConstant.Product.DELETE_PRODUCT)
    public ResponseEntity<?> deleteProduct(@PathVariable Long id){
        CommonResponseDto commonResponseDto = productService.deleteProduct(id);

        return VsResponseUtil.success(HttpStatus.OK, commonResponseDto);

    }

    @GetMapping(UrlConstant.Product.GET_PRODUCTS)
    public ResponseEntity<?> getAllProduct(
            @RequestParam(value = "filter", required = false) List<String> filter,
            Pageable pageable){

        return VsResponseUtil.success(HttpStatus.OK,productService.getAllProduct(filter,pageable) );

    }

    @PostMapping(UrlConstant.Product.GET_RECOMMENDATIONS)
    public ResponseEntity<?> recommendProducts(@Valid @RequestBody ReqRecommendationDto req) {
        return VsResponseUtil.success(HttpStatus.OK, new ResRecommendationDto(productService.getRecommendedProductIds(req.getItemIds())));
    }
}
