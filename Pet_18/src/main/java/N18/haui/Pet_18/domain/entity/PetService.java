package N18.haui.Pet_18.domain.entity;

import N18.haui.Pet_18.domain.dto.common.FlagUserDateAuditing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name ="tbl_services")
@NoArgsConstructor
@AllArgsConstructor
public class PetService extends FlagUserDateAuditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name ="description")
    private String description;

    @Column(name ="base_price",precision = 10, scale = 2)
    private BigDecimal basePrice;


    @Column(name = "duration_min")
    private int durationMin;

    @OneToMany(mappedBy = "service")
    @JsonIgnore
    private List<BookingDetail> bookingDetails;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "petService")
    private List<PetServiceImage> serviceImages;


}
