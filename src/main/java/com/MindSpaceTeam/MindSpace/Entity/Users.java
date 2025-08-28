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

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String oauthProvider;

    @Column(nullable = false)
    private String role;

    @Builder
    public Users(String email, String name, String oauthProvider, String role) {
        this.email = email;
        this.name = name;
        this.oauthProvider = oauthProvider;
        this.role = role;
    }
}
