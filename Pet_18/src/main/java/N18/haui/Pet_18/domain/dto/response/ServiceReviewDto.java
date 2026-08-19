package N18.haui.Pet_18.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ServiceReviewDto {
    private Long id;
    private Long serviceId;
    private String userId;
    private String userName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdDate;
}
