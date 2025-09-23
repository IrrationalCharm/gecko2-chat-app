package eu.irrationalcharm.userservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Entity
@Table(name = "t_user_friendship_preference")
@Getter @Setter
public class UserFriendshipPreferenceEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "friend_id")
    private UUID friendId;

    @Column(name = "is_blocked")
    private boolean isBlocked;

    @Column(name = "is_muted")
    private boolean isMuted;

    @Column(name = "is_pinned")
    private boolean isPinned;

}