package N18.haui.Pet_18.domain.entity;

import N18.haui.Pet_18.constant.GenderEnum;
import N18.haui.Pet_18.domain.dto.common.FlagUserDateAuditing;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_pets")
public class Pet extends FlagUserDateAuditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    //Chủng loại thú cưng (Ví dụ: Mèo Anh lông ngắn, Chó Poodle...)
    @Column(name = "specie")
    private String specie;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private GenderEnum gender;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "weight")
    private float weight;

    //Tình trạng sức khỏe, tiền sử bệnh lý hoặc lưu ý đặc biệt khi làm spa
    @Column(name = "health_status")
    private String healthStatus;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "pet")
    @JsonIgnore
    private List<Booking> bookings;

}
