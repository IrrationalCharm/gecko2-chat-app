package eu.irrationalcharm.userservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Entity
@Table(name = "t_user", schema = "public")
@Getter @Setter
public class UserEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    private String providerId;

    private String username;
    private String displayName;
    private String email;
    private String mobileNumber;
    private String profileBio;
    private String profileImageUrl;

}
