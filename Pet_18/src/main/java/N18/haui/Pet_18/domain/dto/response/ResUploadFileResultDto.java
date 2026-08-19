package N18.haui.Pet_18.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResUploadFileResultDto {

    private List<ResUploadFileDto> resUploadFileDtoList;
    private List<String> resUploadFileFailedList;



    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResUploadFileDto{
        private String fileName;

        private LocalDateTime uploadedAt;
    }
}
