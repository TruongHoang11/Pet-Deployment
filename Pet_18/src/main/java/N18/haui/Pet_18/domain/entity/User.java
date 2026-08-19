package N18.haui.Pet_18.domain.entity;


import N18.haui.Pet_18.constant.GenderEnum;
import N18.haui.Pet_18.domain.dto.common.FlagUserDateAuditing;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "tbl_users")
public class User extends FlagUserDateAuditing implements Serializable {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private String id;


    @Column(name = "name")
    private String name;

    @NotBlank(message = "email khong duoc de trong")
    @Column(name = "email")
    private String email;

    @NotBlank(message = "password khong duoc de trong")
    @Column(name = "password")
    private String password;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private GenderEnum gender;

    @Column(name = "refresh_token", columnDefinition = "TEXT")
    private String refreshToken;



    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Pet> pets;


    @OneToMany(mappedBy = "user", fetch =  FetchType.LAZY)
    @JsonIgnore
    private List<Booking> bookings;


    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Order> orders;


    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<ShippingAddress> shippingAddresses;

    @OneToMany(mappedBy = "user")
    private List<ProductReview> reviews;

    @OneToMany(mappedBy = "user")
    private List<PetServiceReview> petServiceReviews;

    @OneToOne(mappedBy = "user")
    private Cart cart;

    @Column(name = "avatar_url")
    private String avatarUrl;



}
