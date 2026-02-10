package com.bharat.bank.notification.entity;

import com.bharat.bank.auth_users.entity.User;
import com.bharat.bank.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;
    private String recipient;
    private String body;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    private final LocalDateTime createdAt= LocalDateTime.now();
}
