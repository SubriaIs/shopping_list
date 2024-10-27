package com.team.e.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"userCreatedUserGroups", "userMemberGroups", "userTriggeredNotifications"}) // Ignoring these fields globally
public class User {
    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private Long userId;

    @Column(name = "userName", nullable = false, length = 100)
    private String userName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "phoneNumber", nullable = false, unique = true, length = 100)
    private String phoneNumber;

    @Column(name = "password",nullable = false, length =254, unique = true)
    private String password;

    @Column(name = "token",nullable = false, length =254)
    private String token;

    @OneToMany(mappedBy = "createdByUser", fetch = FetchType.EAGER)
    private List<UserGroup> userCreatedUserGroups;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<GroupMemberShip> userMemberGroups;

    @OneToMany(mappedBy = "triggeredBy", fetch = FetchType.EAGER)
    private List<Notification> userTriggeredNotifications;
}
