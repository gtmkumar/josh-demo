package com.josh_demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "token")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String tokenValue;
    private String tokenType;
    private LocalDateTime expirationTime;
    private LocalDateTime createdAt;
    private Boolean isUsed;
}
