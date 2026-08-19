package N18.haui.Pet_18.domain.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReqRecommendationDto {
    @NotEmpty(message = "Item ids are required")
    private List<Long> itemIds;
}
