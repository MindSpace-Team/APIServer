package com.MindSpaceTeam.MindSpace.Entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
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

    @Column
    private String role;

    @Builder
    public Users(String email, String name, String oauthProvider, String role) {
        this.email = email;
        this.name = name;
        this.oauthProvider = oauthProvider;
        this.role = role;
    }

}
