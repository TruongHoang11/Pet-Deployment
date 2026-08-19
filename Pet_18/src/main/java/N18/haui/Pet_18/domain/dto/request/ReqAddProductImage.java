package N18.haui.Pet_18.domain.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class ReqAddProductImage {
    private Long productId;

    List<MultipartFile> files;
}
