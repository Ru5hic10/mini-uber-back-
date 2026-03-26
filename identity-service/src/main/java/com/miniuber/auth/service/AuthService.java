package com.miniuber.auth.service;

import com.miniuber.auth.config.JwtConfig;
import com.miniuber.auth.dto.*;
import com.miniuber.auth.entity.RefreshToken;
import com.miniuber.auth.event.UserRegistrationEventPublisher;
import com.miniuber.auth.exception.InvalidCredentialsException;
import com.miniuber.auth.repository.RefreshTokenRepository;
import com.miniuber.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import com.miniuber.user.service.UserService;
import com.miniuber.driver.service.DriverService;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.miniuber.auth.controller.LoggingController;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final JwtUtil jwtUtil;
    private final JwtConfig jwtConfig;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRegistrationEventPublisher eventPublisher;

    private final UserService userService;
    private final DriverService driverService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        logger.info("User registration attempt - Email: {}, UserType: {}", request.getEmail(), request.getUserType());
        LoggingController.addLog("User registration attempt - Email: " + request.getEmail() + ", UserType: " + request.getUserType());

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        Long userId;
        String email;
        String name = "";
        String userType = request.getUserType() != null ? request.getUserType().toUpperCase() : "RIDER";

        if ("DRIVER".equalsIgnoreCase(userType)) {
            var driverReq = new com.miniuber.driver.dto.DriverRegistrationRequest();
            driverReq.setName(request.getName());
            driverReq.setEmail(request.getEmail());
            driverReq.setPassword(hashedPassword);
            driverReq.setPhone(request.getPhone());
            driverReq.setLicenseNumber(request.getLicenseNumber());
            driverReq.setVehicleType(request.getVehicleType());
            driverReq.setVehicleNumber(request.getVehicleNumber());
            driverReq.setVehicleModel(request.getVehicleModel());
            var driverResp = driverService.registerDriver(driverReq);
            userId = driverResp.getId();
            email = driverResp.getEmail();
            name = driverResp.getName();
        } else {
            var userReq = new com.miniuber.user.dto.UserRegistrationRequest();
            userReq.setName(request.getName());
            userReq.setEmail(request.getEmail());
            userReq.setPassword(hashedPassword);
            userReq.setPhone(request.getPhone());
            var userResp = userService.registerUser(userReq);
            userId = userResp.getId();
            email = userResp.getEmail();
            name = userResp.getName();
        }

        LoggingController.addLog("User registered successfully - Email: " + email + ", UserId: " + userId);

        if ("DRIVER".equalsIgnoreCase(userType)) {
            eventPublisher.publishDriverRegistration(userId, email, name);
        }

        return generateAuthResponse(userId, email, userType, name, null);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        logger.info("User login attempt - Email: {}, UserType: {}", request.getEmail(), request.getUserType());
        LoggingController.addLog("User login attempt - Email: " + request.getEmail());

        String userType = request.getUserType() != null ? request.getUserType().toUpperCase() : "RIDER";

        Object userObj;
        if ("DRIVER".equals(userType)) {
            userObj = driverService.getDriverByEmail(request.getEmail());
        } else {
            userObj = userService.getUserByEmail(request.getEmail());
        }

        if (userObj == null) {
            logger.error("User not found - Email: {}", request.getEmail());
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String storedPassword;
        Long userId;
        String email;
        String name = "";
        String phone = "";

        if ("DRIVER".equals(userType)) {
            var driver = (com.miniuber.driver.entity.Driver) userObj;
            storedPassword = driver.getPassword();
            userId = driver.getId();
            email = driver.getEmail();
            name = driver.getName();
            phone = driver.getPhone();
        } else {
            var user = (com.miniuber.user.entity.User) userObj;
            storedPassword = user.getPassword();
            userId = user.getId();
            email = user.getEmail();
            name = user.getName();
            phone = user.getPhone();
        }

        if (storedPassword == null) {
            logger.error("Stored password is null for user - Email: {}", request.getEmail());
            throw new InvalidCredentialsException("Invalid email or password");
        }

        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), storedPassword);
        if (!passwordMatches) {
            logger.error("Invalid password for user - Email: {}", request.getEmail());
            throw new InvalidCredentialsException("Invalid email or password");
        }

        logger.info("User login successful - Email: {}, UserType: {}, UserId: {}", email, userType, userId);
        LoggingController.addLog("User login successful - Email: " + email + ", UserType: " + userType);

        return generateAuthResponse(userId, email, userType, name, phone);
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.getRevoked()) {
            throw new RuntimeException("Refresh token has been revoked");
        }

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token has expired");
        }

        if (!jwtUtil.validateToken(refreshToken.getToken())) {
            throw new RuntimeException("Invalid refresh token");
        }

        String email = jwtUtil.extractEmail(refreshToken.getToken());

        return generateAuthResponse(
                refreshToken.getUserId(),
                email,
                refreshToken.getUserType(),
                null,
                null
        );
    }

    public ValidateTokenResponse validateToken(String token) {
        try {
            if (jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.extractUserId(token);
                String email = jwtUtil.extractEmail(token);
                String userType = jwtUtil.extractUserType(token);

                return ValidateTokenResponse.builder()
                        .valid(true)
                        .userId(userId)
                        .email(email)
                        .userType(userType)
                        .message("Token is valid")
                        .build();
            }
        } catch (Exception e) {
            // Token is invalid
        }

        return ValidateTokenResponse.builder()
                .valid(false)
                .message("Token is invalid or expired")
                .build();
    }

    @Transactional
    public void logout(String token) {
        Long userId = jwtUtil.extractUserId(token);
        String userType = jwtUtil.extractUserType(token);

        refreshTokenRepository.findByUserIdAndUserType(userId, userType)
                .ifPresent(refreshToken -> {
                    refreshToken.setRevoked(true);
                    refreshTokenRepository.save(refreshToken);
                });
    }

    private AuthResponse generateAuthResponse(Long userId, String email, String userType, String name, String phone) {
        String accessToken = jwtUtil.generateToken(userId, email, userType);
        String refreshTokenStr = jwtUtil.generateRefreshToken(userId, email, userType);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenStr);
        refreshToken.setUserId(userId);
        refreshToken.setUserType(userType);
        refreshToken.setExpiryDate(
                LocalDateTime.now().plusSeconds(jwtConfig.getRefreshExpiration() / 1000)
        );
        refreshToken.setRevoked(false);

        refreshTokenRepository.deleteByUserId(userId);
        refreshTokenRepository.save(refreshToken);

        // Fill user info if missing by querying local services
        if ((name == null || name.isEmpty()) || (phone == null || phone.isEmpty())) {
            try {
                if ("DRIVER".equalsIgnoreCase(userType)) {
                    var driver = driverService.getDriverById(userId);
                    if (driver != null) {
                        if (name == null || name.isEmpty()) name = driver.getName();
                        if (phone == null || phone.isEmpty()) phone = driver.getPhone();
                    }
                } else {
                    var user = userService.getUserById(userId);
                    if (user != null) {
                        if (name == null || name.isEmpty()) name = user.getName();
                        if (phone == null || phone.isEmpty()) phone = user.getPhone();
                    }
                }
            } catch (Exception e) {
                // ignore and use available fields
            }
        }

        UserInfo userInfo = new UserInfo(userId, name != null ? name : "", email, phone != null ? phone : "", userType);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .tokenType("Bearer")
                .expiresIn(jwtConfig.getExpiration() / 1000)
                .user(userInfo)
                .build();
    }
}