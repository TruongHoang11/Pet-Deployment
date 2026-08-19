package N18.haui.Pet_18.service;


import N18.haui.Pet_18.domain.dto.response.ResUploadFileResultDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface FileService {
    void createDirectory(String folder) throws URISyntaxException;

    ResUploadFileResultDto uploadListFile(List<MultipartFile> files, String folder) throws URISyntaxException, IOException;

    long getFileLength(String fileName, String folder) throws URISyntaxException;

    Resource getResource(String fileName, String folder) throws URISyntaxException, FileNotFoundException;

    ResUploadFileResultDto.ResUploadFileDto uploadFile(MultipartFile file, String folder) throws URISyntaxException, IOException;

}
