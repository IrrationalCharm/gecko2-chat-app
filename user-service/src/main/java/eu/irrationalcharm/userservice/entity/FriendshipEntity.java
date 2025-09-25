package eu.irrationalcharm.userservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Getter @Setter
@Table(name = "t_friendships")
@EntityListeners(AuditingEntityListener.class)
public class FriendshipEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_a", referencedColumnName = "id")
    private UserEntity friendA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_b", referencedColumnName = "id")
    private UserEntity friendB;

    @CreatedDate
    private LocalDateTime created_at;

    /**
     * Makes sure friendA UUID has a higher value than friendB.
     * This is in order to avoid duplicate friendships when inserting into the DB in reverse.
     */
    @PrePersist
    public void prePersist() {
        UUID friendA_id = friendA.getId();
        UUID friendB_id = friendB.getId();

        if (friendA_id == null || friendB.getId() == null)
            return;

        if (friendA_id.compareTo(friendB_id) < 0) {
            UserEntity placeholder = friendA;
            friendA = friendB;
            friendB = placeholder;
        }
    }
}