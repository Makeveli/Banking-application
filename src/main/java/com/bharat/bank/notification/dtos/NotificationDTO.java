package com.bharat.bank.notification.dtos;

import com.bharat.bank.auth_users.entity.User;
import com.bharat.bank.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationDTO {
    private Long id;

    private String subject;

    @NotBlank(message = "Recipient is required")
    private String recipient;

    private String body;

    private NotificationType type;

    private LocalDateTime createdAt;

    //Values passed on to the template engine
    private String templateName;
    private Map<String ,Object> templateVariables;
}
