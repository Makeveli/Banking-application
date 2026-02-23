package com.bharat.bank.notification.services;

import com.bharat.bank.auth_users.entity.User;
import com.bharat.bank.notification.dtos.NotificationDTO;

public interface NotificationService {
    void sendMail(NotificationDTO notificationDTO, User user);
}
