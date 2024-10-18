package com.team.e.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class GroupMemberShip {
    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupMemberShipId ;

    @ManyToOne
    @MapsId("userId")
    @JsonIgnoreProperties({"userMemberGroups","userCreatedUserGroups"})
    @JoinColumn(name = "userId", nullable = false, foreignKey = @ForeignKey(name = "fk_user_membership"))
    private User user;

    @ManyToOne
    @MapsId("groupId")
    @JsonIgnoreProperties({"userMemberGroups", "createdByUser", "groupUsers"})
    @JoinColumn(name = "groupId", nullable = false, foreignKey = @ForeignKey(name = "fk_group_membership"))
    private UserGroup userGroup;

}
