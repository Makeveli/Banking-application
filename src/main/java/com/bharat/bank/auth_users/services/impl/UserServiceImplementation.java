package com.bharat.bank.auth_users.services.impl;

import com.bharat.bank.account.dtos.AccountDTO;
import com.bharat.bank.auth_users.dtos.UpdatePasswordRequest;
import com.bharat.bank.auth_users.dtos.UserDTO;
import com.bharat.bank.auth_users.entity.User;
import com.bharat.bank.auth_users.repo.UserRepository;
import com.bharat.bank.auth_users.services.UserService;
import com.bharat.bank.exceptions.BadRequestException;
import com.bharat.bank.exceptions.NotFoundException;
import com.bharat.bank.notification.dtos.NotificationDTO;
import com.bharat.bank.notification.services.NotificationService;
import com.bharat.bank.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    private final String uploadDirectory ="uploads/profile-pictures";

    @Override
    public User getCurrentLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null){
            throw new NotFoundException("User is not authenticated");
        }
        String email =authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(()-> new NotFoundException("User not found."));
    }

    @Override
    public Response<UserDTO> getMyProfile() {
        User user = getCurrentLoggedInUser();
        UserDTO userDTO = modelMapper.map(user,UserDTO.class);

        return Response.<UserDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User retreived")
                .data(userDTO)
                .build();
    }

    @Override
    public Response<Page<UserDTO>> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        Page<User> userPage = userRepository.findAll(pageable);
        Page<UserDTO> userDTOPage = userPage.map(user -> modelMapper.map(user,UserDTO.class));

        return Response.<Page<UserDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("All users retrieved")
                .data(userDTOPage)
                .build();
    }

    @Override
    public Response<?> updatePassword(UpdatePasswordRequest updatePasswordRequest) {
        User user = getCurrentLoggedInUser();
        if(!passwordEncoder.matches(updatePasswordRequest.getOldPassword(),user.getPassword())){
            throw new BadRequestException("Password incorrect!");
        }
        user.setPassword(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now() );
        userRepository.save(user);

        //Send confirmation email for password change
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("name",user.getFirstName());

        NotificationDTO notificationDTO = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("Your password was changed.")
                .templateName("password-change")
                .templateVariables(templateVariables)
                .build();

        notificationService.sendMail(notificationDTO,user);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Password updated successfully!")
                .build();
    }

    @Override
    public Response<?> uploadProfilePicture(MultipartFile file) {
        User user = getCurrentLoggedInUser();

        try {
            Path uploadPath = Paths.get(uploadDirectory);
            if(!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }
            if(user.getProfilePictureUrl() !=null && !user.getProfilePictureUrl().isEmpty()){
                Path oldFile = Paths.get(user.getProfilePictureUrl());
                if(Files.exists(oldFile)){
                    Files.delete(oldFile);
                }
            }

            String originalFileName = file.getOriginalFilename();
            String fileExtension ="";
            if(originalFileName!=null && originalFileName.contains(".")){
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            String newFileName = UUID.randomUUID() + fileExtension;
            Path filePath = uploadPath.resolve(newFileName);

            Files.copy(file.getInputStream(), filePath);

            String fileUrl = uploadDirectory+newFileName;
            user.setProfilePictureUrl(fileUrl);
            user.setUpdatedAt(LocalDateTime.now());

            userRepository.save(user);
            return Response.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Profile picture uploaded successfully!")
                    .data(fileUrl)
                    .build();
        }
        catch (IOException ex){
            throw new RuntimeException(ex.getMessage());
        }
    }


}
