package com.MindSpaceTeam.MindSpace.dto;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    @Column
    private String email;

    @Column
    private String name;

    @Column(name="oauth_provider")
    private String oauthProvider;

    @Column
    private String role;

    @Builder
    public User(String email, String name, String oauthProvider, String role) {
        this.email = email;
        this.name = name;
        this.oauthProvider = oauthProvider;
        this.role = role;
    }

}
