package N18.haui.Pet_18.domain.dto.response;

import java.util.List;

import N18.haui.Pet_18.domain.dto.common.FlagUserDateAuditing;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MenuDto extends FlagUserDateAuditing {
    private Long id;
    private String name;
    private String path;
    private String icon;
    private List<MenuDto> children;
    private List<String> roles;
}
