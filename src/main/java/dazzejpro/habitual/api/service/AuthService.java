package dazzejpro.habitual.api.service;

import dazzejpro.habitual.api.dtos.LoginDtos.LoginRequest;
import dazzejpro.habitual.api.dtos.LoginDtos.LoginResponse;
import dazzejpro.habitual.api.dtos.RegisterDtos.RegisterRequest;
import dazzejpro.habitual.api.dtos.RegisterDtos.RegisterResponse;
import dazzejpro.habitual.api.dtos.ResetPasswordDtos.ResetPasswordRequest;
import dazzejpro.habitual.api.dtos.ResetPasswordDtos.ResetPasswordResponse;
import dazzejpro.habitual.api.entities.User;
import dazzejpro.habitual.api.exception.ApiException;
import dazzejpro.habitual.api.repositories.UserRepository;
import dazzejpro.habitual.api.security.PasswordHasher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public AuthService(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    // Registration business logic
    public RegisterResponse register(RegisterRequest request) {
        validateRegisterRequest(request);

        if (userRepository.existsByUsernameIgnoreCase(request.getUsername())) {
            throw new ApiException(HttpStatus.CONFLICT, "duplicate username");
        }

        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "duplicate email");
        }

        // The id is left empty on purpose: the database generates it when the user is saved
        User user = new User();
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordHasher.hash(request.getPassword()));

        user = userRepository.save(user);

        return new RegisterResponse(
                user.getUserId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );
    }

    // Login business logic
    public LoginResponse login(LoginRequest request) {

        if (isBlank(request.getUsername()) || isBlank(request.getPassword())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "username and password are required");
        }

        User user = userRepository.findByUsernameIgnoreCase(request.getUsername()).orElse(null);

        if (user == null || !passwordHasher.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "invalid username or password");
        }

        String token = Base64.getUrlEncoder()
                .withoutPadding().encodeToString(UUID.randomUUID().toString().getBytes());

        return new LoginResponse(user.getUserId(), user.getUsername(), token);
    }


    //Reset password business logic
    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
        if (isBlank(request.getEmail()) || isBlank(request.getNewPassword())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "email and password are required");
        }
        User user = userRepository.findByEmailIgnoreCase(request.getEmail()).orElse(null);

        if (user == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "user not found");
        }

        user.setPasswordHash(passwordHasher.hash(request.getNewPassword()));
        userRepository.save(user);

        return new ResetPasswordResponse("Password reset successful");
    }

    // Validates registration request
    private void validateRegisterRequest(RegisterRequest request) {

        if (isBlank(request.getUsername())
                || isBlank(request.getFirstName())
                || isBlank(request.getLastName())
                || isBlank(request.getEmail())
                || isBlank(request.getPassword())) {

            throw new ApiException(HttpStatus.BAD_REQUEST, "invalid/missing field");
        }

        if (!request.getEmail().contains("@")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "email must be valid");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
