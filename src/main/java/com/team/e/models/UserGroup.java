package com.team.e.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@JsonIgnoreProperties({"groupUsers", "groupNotifications", "shoppingList"})
public class UserGroup {
    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "groupId")
    private Long groupId;

    @Column(name = "groupName", nullable = false, length = 100)
    private String groupName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", nullable = false)
    @JsonIgnoreProperties({"userCreatedUserGroups"})
    private User createdByUser;

    @OneToMany(mappedBy = "userGroup", fetch = FetchType.EAGER)
    private List<GroupMemberShip> groupUsers;

    @OneToMany(mappedBy = "notificationUserGroup", fetch = FetchType.EAGER)
    private List<Notification> groupNotifications;

    // Add the one-to-one relationship back to ShoppingList
    @OneToOne(mappedBy = "userGroup", cascade = CascadeType.ALL)
    @JsonIgnore
    private ShoppingList shoppingList;

}
