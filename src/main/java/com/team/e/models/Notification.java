package com.team.e.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Notification {
    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @ManyToOne
    @JoinColumn(name = "groupId", foreignKey = @ForeignKey(name = "fk_group_notification"), nullable = true)
    private UserGroup notificationUserGroup;

    @ManyToOne(optional = true)
    @JoinColumn(name = "triggeredBy",referencedColumnName = "userId", nullable = true, foreignKey = @ForeignKey(name = "fk_user_triggered_by"))
    private User triggeredBy;

    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
