package N18.haui.Pet_18.domain.dto.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Setter
@Getter
public abstract class UserAuditingDto {
  @CreatedBy
  @Column(updatable = false)
  private String createdBy;

  @LastModifiedBy
  @Column(nullable = true)
  private String lastModifiedBy;
}
