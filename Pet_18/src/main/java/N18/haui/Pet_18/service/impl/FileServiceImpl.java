package N18.haui.Pet_18.service.impl;

import N18.haui.Pet_18.domain.dto.response.ResUploadFileResultDto;
import N18.haui.Pet_18.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    // Giới hạn 2MB (2 * 1024 * 1024 bytes)
    private static final long MAX_FILE_SIZE = 4 * 1024 * 1024;

    @Value("${hoang.upload-file.base-uri}")
    private String baseUri;
    // Tối ưu: Đưa danh sách cấu hình ra ngoài làm hằng số để tránh khởi tạo lại trong vòng lặp
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "application/pdf",
            "image/jpeg",
            "image/png",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );


    private static final List<String> ALLOWED_EXTENSIONS_IMAGE = Arrays.asList("jpg", "jpeg", "png", "webp", "gif");
    private static final List<String> ALLOWED_MIME_TYPES_IMAGE = Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif"
    );


    public void createDirectory(String folder) {
        // Paths.get tự động xử lý dấu gạch chéo chuẩn theo OS (Windows/Linux)
        Path targetPath = Paths.get(baseUri, folder).toAbsolutePath().normalize();

        if (!Files.exists(targetPath)) {
            try {
                Files.createDirectories(targetPath);
                log.info("Tạo thư mục mới thành công: {}", targetPath);
            } catch (IOException e) {
                log.error("Không thể tạo thư mục tại: {}", targetPath, e);
                throw new RuntimeException("Lỗi khởi tạo thư mục lưu trữ: " + e.getMessage());
            }
        } else {
            log.debug("Thư mục đã tồn tại: {}", targetPath);
        }
    }



    @Override
    public ResUploadFileResultDto uploadListFile(List<MultipartFile> files, String folder) {
        createDirectory(folder);

        List<ResUploadFileResultDto.ResUploadFileDto> resUploadFileDtoList = new ArrayList<>();
        List<String> resUploadFileFailedList = new ArrayList<>();

        for (MultipartFile file : files) {
            String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

            // 1. Validate file
            String validationError = validateAllFile(file, originalFileName);
            if (validationError != null) {
                resUploadFileFailedList.add(originalFileName + " -> " + validationError);
                continue; // Bỏ qua nếu có bất kỳ lỗi validate nào
            }

            // 2. Ghi file vào ổ cứng (Tách riêng logic I/O)
            try {
                String finalName = generateUniqueFileName(originalFileName);
                saveFileToStorage(file, folder, finalName);

                // 3. Gom kết quả thành công
                ResUploadFileResultDto.ResUploadFileDto dto = new ResUploadFileResultDto.ResUploadFileDto();
                dto.setFileName(finalName);
                dto.setUploadedAt(LocalDateTime.now());
                resUploadFileDtoList.add(dto);

            } catch (IOException e) {
                log.error("Lỗi hệ thống khi ghi file vật lý: {}", originalFileName, e);
                throw new RuntimeException("Không thể lưu file " + originalFileName + ". Vui lòng thử lại!", e);
            }
        }

        return new ResUploadFileResultDto(resUploadFileDtoList, resUploadFileFailedList);
    }


    private String validateAllFile(MultipartFile file, String originalFileName) {
        if (file.isEmpty()) {
            return "File trống";
        }

        // Kiểm tra Extension
        String finalOriginalFileName = originalFileName.toLowerCase();
        boolean isValidExtension = ALLOWED_EXTENSIONS.stream()
                .anyMatch(ext -> finalOriginalFileName.endsWith("." + ext)); // Đã sửa lỗi logic .endsWith của bạn trước đó

        if (!isValidExtension) {
            return "Định dạng file không được hỗ trợ";
        }
        // Kiểm tra kích thước
        if (file.getSize() > MAX_FILE_SIZE) {
            return "Dung lượng file vượt quá giới hạn cho phép (4MB)";
        }

        // Kiểm tra MimeType
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            return "Loại nội dung (MimeType) không hợp lệ";
        }

        return null; // File hoàn toàn sạch sẽ và hợp lệ
    }


    private String generateUniqueFileName(String originalFileName) {
        return System.currentTimeMillis() + "-" + originalFileName;
    }

    private void saveFileToStorage(MultipartFile file, String folder, String finalName) throws IOException {
        //.toAbsolutePath(): Chuyển toàn bộ đường dẫn thành đường dẫn tuyệt đối (bắt đầu từ gốc ổ đĩa hoặc gốc hệ thống).
        //.normalize(): Đây chính là hàm sẽ triệt tiêu hoàn toàn các ký tự nguy hiểm.
        // Nếu hacker cố tình lọt qua tầng filter trước và truyền vào đường dẫn có dạng
        // Paths.get() -> tư biet noi voi nhau 1 cach hop ly, tự nhận diện hệ điều hành : window thì dấu: \
        // linux, macOs, docker thì dấu: /
        // vid Paths.get("C:\Users\Admin\Downloads", "avatar", "hoanganh")
        // -> C:\Users\Admin\Downloads\avatar\hoanganh
        Path targetLocation = Paths.get(baseUri, folder, finalName).toAbsolutePath().normalize();
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("Lưu file thành công: {}", targetLocation);
        }
    }

    @Override
    public long getFileLength(String fileName, String folder) {
        Path filePath = Paths.get(baseUri, folder, fileName).toAbsolutePath().normalize();
        File file = filePath.toFile();

        if (!file.exists() || file.isDirectory()) {
            log.warn("File không tồn tại hoặc là thư mục: {}", filePath);
            return 0;
        }
        return file.length();
    }

    @Override
    public Resource getResource(String fileName, String folder) throws FileNotFoundException {
        try {
            // 1. Tạo đường dẫn tuyệt đối và làm sạch (Chống Path Traversal)
            Path filePath = Paths.get(baseUri, folder, fileName).toAbsolutePath().normalize();

            // 2. Chuyển đổi Path thành Resource dữ liệu
            Resource resource = new UrlResource(filePath.toUri());

            // 3. Kiểm tra file có thực sự tồn tại và có thể đọc được không
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                log.warn("Không tìm thấy file hoặc file không thể đọc: {}", filePath);
                throw new FileNotFoundException("Không tìm thấy file: " + fileName);
            }
        } catch (MalformedURLException e) {
            log.error("Lỗi định dạng đường dẫn file: {}", fileName, e);
            throw new FileNotFoundException("Đường dẫn file không hợp lệ: " + fileName);
        }
    }

    @Override
    public ResUploadFileResultDto.ResUploadFileDto uploadFile(MultipartFile file, String folder) throws URISyntaxException, IOException {
        createDirectory(folder);

        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        // Validate file
        String validationError = validFileImage(file, originalFileName);
        if (validationError != null) {
            throw new FileUploadException(validationError);
        }



        // Save file
        String finalName = generateUniqueFileName(originalFileName);
        saveFileToStorage(file, folder, finalName);

        ResUploadFileResultDto.ResUploadFileDto dto = new ResUploadFileResultDto.ResUploadFileDto();
        dto.setFileName(finalName);
        dto.setUploadedAt(LocalDateTime.now());


        return dto;

    }

    private String validFileImage(MultipartFile file, String originalFileName){
        if (file.isEmpty()) {
            return "File trống";
        }

        // Kiểm tra Extension
        String finalOriginalFileName = originalFileName.toLowerCase();
        boolean isValidExtension = ALLOWED_EXTENSIONS_IMAGE.stream()
                .anyMatch(ext -> finalOriginalFileName.endsWith("." + ext));

        if (!isValidExtension) {
            return "Định dạng file không được hỗ trợ";
        }
        // Kiểm tra kích thước
        if (file.getSize() > MAX_FILE_SIZE) {
            return "Dung lượng file vượt quá giới hạn cho phép (4MB)";
        }

        // Kiểm tra MimeType
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES_IMAGE.contains(contentType.toLowerCase())) {
            return "Loại nội dung (MimeType) không hợp lệ";
        }

        return null; // File hoàn toàn sạch sẽ và hợp lệ
    }


}