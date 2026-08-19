package N18.haui.Pet_18.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqForMenu {
    private String name;
    private String path;
    private String icon;
    private Long parentId;
}
