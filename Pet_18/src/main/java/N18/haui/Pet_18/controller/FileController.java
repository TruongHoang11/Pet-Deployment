package N18.haui.Pet_18.controller;

import N18.haui.Pet_18.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FileService fileService;

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(
            @RequestParam String fileName,
            @RequestParam String folder,
            HttpServletRequest request) throws FileNotFoundException, URISyntaxException { // 1. Inject thêm HttpServletRequest

        Resource resource = fileService.getResource(fileName, folder);

        // 2. Dùng Spring để tự động đoán Content-Type từ Resource (An toàn, không lo lỗi file JAR)
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.info("Không thể xác định định dạng file, sử dụng mặc định.");
        }

        // Nếu không đoán được (hoặc file lạ), đặt mặc định là file tải về dạng nhị phân thô
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}