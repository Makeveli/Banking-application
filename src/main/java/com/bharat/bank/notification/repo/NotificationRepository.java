package com.bharat.bank.notification.repo;

import com.bharat.bank.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification,Long> {

}
