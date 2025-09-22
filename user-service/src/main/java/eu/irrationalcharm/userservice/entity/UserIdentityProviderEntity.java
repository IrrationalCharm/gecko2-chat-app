package eu.irrationalcharm.userservice.entity;

import eu.irrationalcharm.userservice.enums.IdentityProviderType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "user_identity_provider")
@Getter @Setter
public class UserIdentityProviderEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @NotNull
    @Column(name = "provider", nullable = false, length = 50)
    @Enumerated(value = EnumType.STRING)
    private IdentityProviderType provider;

    @NotNull
    @Column(name = "provider_user_id", nullable = false)
    private String providerUserId;

    @NotNull
    @Column(name = "email", nullable = false, length = Integer.MAX_VALUE)
    private String email;

}