package com.MindSpaceTeam.MindSpace.Entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Entity(name="users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column
    private String email;

    @Column
    private String name;

    @Column
    private String oauthProvider;

    @OneToMany(mappedBy = "users")
    private List<UserWorkspace> userWorkspace;

    @Builder
    public Users(String email, String name, String oauthProvider, String role, long userId) {
        this.email = email;
        this.name = name;
        this.oauthProvider = oauthProvider;
        this.userId = userId;
    }

}
