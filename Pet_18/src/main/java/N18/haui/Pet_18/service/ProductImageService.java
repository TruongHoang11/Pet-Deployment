package N18.haui.Pet_18.service;


import N18.haui.Pet_18.domain.dto.request.ReqSetThumbnailProduct;
import N18.haui.Pet_18.domain.dto.response.CommonResponseDto;
import N18.haui.Pet_18.domain.dto.response.ProductImageDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface ProductImageService {

    CommonResponseDto addImages(Long productId, List<MultipartFile> files) throws URISyntaxException, IOException;

    CommonResponseDto deleteImage(Long imageId);

    CommonResponseDto changeMainImage(ReqSetThumbnailProduct reqSetMainImage);

    List<ProductImageDto> getProductImages(Long productId);
}
