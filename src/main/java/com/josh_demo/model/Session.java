package com.josh_demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "session")
public class Session {
    @Id
    private String sessionId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime loginTime;
    private LocalDateTime lastActivity;
    private LocalDateTime expirationTime;
    private String ipAddress;
    private String userAgent;
}
