package com.MindSpaceTeam.MindSpace.Entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Builder
    public Users(String email, String name, String oauthProvider, String role) {
        this.email = email;
        this.name = name;
        this.oauthProvider = oauthProvider;
    }

}
