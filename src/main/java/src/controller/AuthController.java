package src.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import src.config.annotation.ApiPrefixController;
import src.config.auth.JwtTokenUtil;
import src.config.exception.BadRequestException;
import src.model.User;
import src.repository.UserRepository;
import src.service.User.Dto.UserCreateDto;
import src.service.User.Dto.UserDto;
import src.service.User.Dto.UserProfileDto;
import src.service.User.auth.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@RestController
@ApiPrefixController("/auth")
@Tag(name = "User authentication")
public class AuthController {
    @Autowired
    private JwtTokenUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private MailService mailService;
    @Autowired
    private ModelMapper toDto;
    private final Map<String, String> confirmationCodes = new HashMap<>();
    private final Map<String, Timer> confirmationTimers = new HashMap<>();
    @PostMapping("/login")
    public CompletableFuture<ResponseEntity<?>> createAuthenticationToken(@RequestBody @Valid LoginInputDto loginRequest) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final User user = userRepository.findByEmail(loginRequest.getEmail());
                if (user == null) {
                    throw new Exception("Cannot find user with email");
                }
                if (!JwtTokenUtil.comparePassword(loginRequest.getPassword(), user.getPassword())) {
                    throw new Exception("Password not correct");
                }
                final String accessToken = jwtUtil.generateAccessToken(user);
                final String refreshToken = jwtUtil.generateRefreshToken(user);
                toDto.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                return ResponseEntity.ok(new LoginDto(accessToken, refreshToken, toDto.map(user, UserProfileDto.class)));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        });
    }
    @PostMapping("/refresh-token")
    public CompletableFuture<ResponseEntity<?>> refreshAuthenticationToken(@RequestBody @Valid RefreshTokenInput refreshTokenRequest) throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final String refreshToken = refreshTokenRequest.getRefreshToken();
                String username = jwtUtil.checkRefreshToken(refreshToken);
                if (username == null)
                    throw new BadRequestException("Not Type Refresh Token");
                final User userDetails = userRepository.findByEmail(jwtUtil.getUsernameFromToken(refreshToken));
                if (jwtUtil.validateToken(refreshToken, userDetails)) {
                    final String accessToken = jwtUtil.generateAccessToken(userDetails);
                    return ResponseEntity.ok(new RefreshTokenDto(accessToken, refreshToken));
        }
                throw new Exception("Invalid refresh token");
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        });
    }
    @PostMapping("/register")
    public CompletableFuture<ResponseEntity<?>> signupUser(@RequestBody UserCreateDto userCreateDto) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (isEmailAlreadyTaken(userCreateDto.getEmail())) {
                    return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
                }
                UserDto createdUser = authService.createUser(userCreateDto);
                if (createdUser == null) {
                    return new ResponseEntity<>("User not created, come again later!", HttpStatus.BAD_REQUEST);
                }
                return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        });
    }
    private boolean isEmailAlreadyTaken(String email) {
        User existingUser = userRepository.findByEmail(email);
        return existingUser != null;
    }
    @PostMapping("/forget-password")
    public CompletableFuture<ResponseEntity<?>> forgetPassword(@RequestBody @Valid ForgetPasswordDto forgetPasswordDto) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                User user = userRepository.findByEmail(forgetPasswordDto.getEmail());
                if (user == null) {
                    throw new BadRequestException("Email not found");
                }
                if (!forgetPasswordDto.getNewPassword().equals(forgetPasswordDto.getRePassword())) {
                    throw new BadRequestException("Password and rePassword do not match");
                }
                user.setPassword(JwtTokenUtil.hashPassword(forgetPasswordDto.getNewPassword())); // Thực hiện mã hóa mật khẩu
                userRepository.save(user);
                return ResponseEntity.ok("Password reset successfully");
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        });
    }
    @PostMapping("/send/message/all-users")
    public CompletableFuture<ResponseEntity<String>> sendEmailToAllUsers(@RequestBody MessageDto messageDto) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<User> users = userRepository.findAll();
                for (User user : users) {
                    String email = user.getEmail();
                    try {
                        mailService.sendMessageMail(email, messageDto);
                    } catch (Exception e) {
                        // Handle exception if required
                    }
                }
                return ResponseEntity.ok("Emails sent to all users!");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send emails to all users.");
            }
        });
    }

    @PostMapping("/send/message/{mail}")
    public CompletableFuture<ResponseEntity<String>> sendMessageMail(@PathVariable String mail, @RequestBody MessageDto messageDto) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                User user = userRepository.findByEmail(mail);
                if (user == null) {
                    return new ResponseEntity<>("Email not found in the database", HttpStatus.BAD_REQUEST);
                }
                mailService.sendMessageMail(mail, messageDto);
                return ResponseEntity.ok("Successfully sent the email!");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send the email.");
            }
        });
    }

    @PostMapping("/change-password")
    public CompletableFuture<ResponseEntity<?>> changePassword(@RequestBody @Valid ChangePasswordDto changePasswordDto) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                User user = userRepository.findByEmail(changePasswordDto.getEmail());
                if (user == null) {
                    throw new BadRequestException("Email not found");
                }
                else if (!JwtTokenUtil.comparePassword(changePasswordDto.getOldPassword(), user.getPassword())) {
                    throw new BadRequestException("Incorrect old password");
                }
                if (changePasswordDto.getNewPassword().equals(changePasswordDto.getOldPassword())) {
                    throw new BadRequestException("New password must be different from the old password");
                }
                user.setPassword(JwtTokenUtil.hashPassword(changePasswordDto.getNewPassword()));
                userRepository.save(user);
                return ResponseEntity.ok("Password changed successfully");
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        });
    }

    @PostMapping("/forgetpassword/sendotp/{mail}")
    public CompletableFuture<ResponseEntity<String>> sendOtpEmailForPassword(@PathVariable String mail) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                User user = userRepository.findByEmail(mail);
                if (user == null) {
                    return new ResponseEntity<>("Email not found in the database", HttpStatus.BAD_REQUEST);
                }
                String confirmationCode = generateConfirmationCode();
                confirmationCodes.put(mail, confirmationCode);
                mailService.sendOtpEmailForPassword(mail, confirmationCode);
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        confirmationCodes.remove(mail);
                    }
                }, 5 * 60 * 1000);  // 5 minutes
                confirmationTimers.put(mail, timer);
                return ResponseEntity.ok("Successfully sent the confirmation email!");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send the confirmation email.");
            }
        });
    }


    private String generateConfirmationCode() {
        return String.valueOf(new Random().nextInt(899999) + 100000);
    }

    @PostMapping("/confirm-reset-password/{mail}")
    public CompletableFuture<ResponseEntity<String>> confirmResetPassword(@PathVariable String mail, @RequestBody ConfirmResetDto confirmResetDto) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String storedCode = confirmationCodes.get(mail);
                if (storedCode == null || !storedCode.equals(confirmResetDto.getConfirmationCode())) {
                    return new ResponseEntity<>("Invalid confirmation code", HttpStatus.BAD_REQUEST);
                }
                User user = userRepository.findByEmail(mail);
                user.setPassword(JwtTokenUtil.hashPassword(confirmResetDto.getNewPassword()));
                userRepository.save(user);

                confirmationCodes.remove(mail);
                Timer timer = confirmationTimers.get(mail);
                if (timer != null) {
                    timer.cancel();
                }
                return ResponseEntity.ok("Password reset successfully!");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to reset the password.");
            }
        });
    }
}