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

    @Column(name = "friend_a")
    private UUID friendA;

    @Column(name = "friend_b")
    private UUID friendB;

    @CreatedDate
    private LocalDateTime created_at;

    /**
     * Makes sure friendA UUID has a higher value than friendB.
     * This is in order to avoid duplicate friendships when inserting into the DB in reverse.
     */
    @PrePersist
    public void prePersist() {
        if (friendA == null || friendB == null)
            return;

        if (friendA.compareTo(friendB) < 0) {
            UUID placeholder = friendA;
            friendA = friendB;
            friendB = placeholder;
        }
    }
}