package N18.haui.Pet_18.service;

import N18.haui.Pet_18.domain.dto.pagination.ResultPaginationDto;
import N18.haui.Pet_18.domain.dto.request.ReqCreateProduct;
import N18.haui.Pet_18.domain.dto.request.ReqUpdateProduct;
import N18.haui.Pet_18.domain.dto.response.CommonResponseDto;
import N18.haui.Pet_18.domain.dto.response.ProductDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    ProductDto createProduct(ReqCreateProduct reqCreateProduct) ;

    ProductDto updateProduct(ReqUpdateProduct reqUpdateProduct) ;


    CommonResponseDto deleteProduct(Long id);

    ProductDto getProductById(Long id);

    ResultPaginationDto getAllProduct(List<String> filter, Pageable pageable);

    List<Long> getRecommendedProductIds(List<Long> productIds);
}
