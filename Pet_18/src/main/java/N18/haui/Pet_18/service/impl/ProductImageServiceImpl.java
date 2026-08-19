package N18.haui.Pet_18.service.impl;

import N18.haui.Pet_18.domain.dto.request.ReqSetThumbnailProduct;
import N18.haui.Pet_18.domain.dto.response.CommonResponseDto;
import N18.haui.Pet_18.domain.dto.response.ProductImageDto;
import N18.haui.Pet_18.domain.dto.response.ResUploadFileResultDto;
import N18.haui.Pet_18.domain.entity.Product;
import N18.haui.Pet_18.domain.entity.ProductImage;
import N18.haui.Pet_18.exception.NotFoundException;
import N18.haui.Pet_18.repository.ProductImageRepository;
import N18.haui.Pet_18.repository.ProductRepository;
import N18.haui.Pet_18.service.FileService;
import N18.haui.Pet_18.service.ProductImageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {
    private final FileService fileService;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    @Value("${hoang.upload-file.base-uri}")
    private String baseUri;



    @Override
    @Transactional
    public CommonResponseDto addImages(Long productId, List<MultipartFile> files) throws URISyntaxException, IOException {

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new NotFoundException("Product not found with id: " + productId)
        );

        ResUploadFileResultDto uploadFileResultDto = fileService.uploadListFile(files, "products");
        if(uploadFileResultDto.getResUploadFileDtoList() != null && !uploadFileResultDto.getResUploadFileDtoList().isEmpty()){
            List<ProductImage> images = uploadFileResultDto.getResUploadFileDtoList().stream()
                    .map(resUploadFileDto -> {
                        ProductImage productImage = new ProductImage();
                        productImage.setProduct(product);
                        productImage.setImageUrl(resUploadFileDto.getFileName());
                        return productImage;
                    }).toList();
            productImageRepository.saveAll(images);
            return new CommonResponseDto(true, "Thêm ảnh sản phẩm thành công");
        }
        return new CommonResponseDto(false, "Không có ảnh nào được thêm (file lỗi hoặc trống");
    }

    @Override
    @Transactional
    public CommonResponseDto deleteImage(Long imageId) {
        ProductImage productImage = productImageRepository.findById(imageId).orElseThrow(
                () -> new NotFoundException("Product image not found with id: " + imageId)
        );
        // 2. XÓA FILE VẬT LÝ TRÊN Ổ CỨNG TRƯỚC
        try {
            Path filePath = Paths.get(baseUri, "products", productImage.getImageUrl()).toAbsolutePath().normalize();
            boolean isDeleted = Files.deleteIfExists(filePath);
            if (isDeleted) {
                log.info("Xóa file vật lý thành công tại: {}", filePath);
            } else {
                log.warn("File vật lý không tồn tại trên ổ cứng nhưng vẫn tiến hành xóa DB: {}", filePath);
            }
        } catch (IOException e) {
            log.error("Lỗi khi xóa file vật lý của ảnh ID: {}", imageId, e);
            // Bạn có thể cân nhắc throw lỗi hoặc bỏ qua để xóa DB tiếp. Ở đây khuyên nên để tiếp tục xóa DB.
        }

        productImageRepository.delete(productImage);
        return new CommonResponseDto(true, "Xóa ảnh sản phẩm thành công");

    }

    @Override
    @Transactional
    public CommonResponseDto changeMainImage(ReqSetThumbnailProduct req) {
        // 1. Kiểm tra sản phẩm có tồn tại không
        if (!productRepository.existsById(req.getProductId())) {
            throw new NotFoundException("Product not found with id: " + req.getProductId());
        }

        // 2. Kiểm tra ảnh có tồn tại và có thuộc sản phẩm này không
        ProductImage targetImage = productImageRepository.findById(req.getImageId()).orElseThrow(
                () -> new NotFoundException("Product image not found with id: " + req.getImageId())
        );

        if (!targetImage.getProduct().getId().equals(req.getProductId())) {
            return new CommonResponseDto(false, "Ảnh không thuộc về sản phẩm này");
        }

        // 3. Đưa tất cả ảnh của sản phẩm về ảnh phụ (isMain = false)
        productImageRepository.resetMainImageByProductId(req.getProductId());

        // 4. Kích hoạt ảnh được chọn làm ảnh chính (isThumbnail = true)
        targetImage.setIsThumbnail(true);
        productImageRepository.save(targetImage);

        return new CommonResponseDto(true, "Thay đổi ảnh đại diện sản phẩm thành công");
    }

    @Override
    public List<ProductImageDto> getProductImages(Long productId) {
        Product product = productRepository.findByIdAndDeleteFlagFalse(productId).orElseThrow(
                () -> new NotFoundException("Product not found with id: " + productId)
        );
        return product.getProductImages().stream()
                .map(image -> {
                    ProductImageDto dto = new ProductImageDto();
                    dto.setId(image.getId());
                    dto.setImageUrl(image.getImageUrl());
                    dto.setIsThumbnail(image.getIsThumbnail());
                    dto.setProductId(image.getProduct().getId());
                    return dto;
                })
                .toList();
    }


}
