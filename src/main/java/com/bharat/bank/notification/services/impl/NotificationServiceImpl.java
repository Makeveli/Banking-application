package com.bharat.bank.notification.services.impl;

import com.bharat.bank.auth_users.entity.User;
import com.bharat.bank.enums.NotificationType;
import com.bharat.bank.notification.dtos.NotificationDTO;
import com.bharat.bank.notification.entity.Notification;
import com.bharat.bank.notification.repo.NotificationRepository;
import com.bharat.bank.notification.services.NotificationService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Override
    @Async
    public void sendMail(NotificationDTO notificationDTO, User user) {
        try{
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );
            helper.setTo(notificationDTO.getRecipient());
            helper.setSubject(notificationDTO.getSubject());

            if(notificationDTO.getTemplateName() != null){
                Context context = new Context();
                context.setVariables(notificationDTO.getTemplateVariables());
                String htmlContent = templateEngine.process(notificationDTO.getTemplateName(), context);
                helper.setText(htmlContent,true);
            }
            else{
                helper.setText(notificationDTO.getBody(),true);
            }
            javaMailSender.send(mimeMessage);

            //Save to database table
            Notification notification = Notification.builder()
                    .recipient(notificationDTO.getRecipient())
                    .subject(notificationDTO.getSubject())
                    .body(notificationDTO.getBody())
                    .type(NotificationType.EMAIL)
                    .user(user)
                    .build();
            notificationRepository.save(notification);
        }
        catch (MessagingException ex){
            log.error(ex.getMessage());
        }
    }
}
