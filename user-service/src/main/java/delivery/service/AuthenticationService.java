package delivery.service;

import delivery.model.AuthenticationRequest;
import delivery.model.AuthenticationResponse;
import delivery.model.UserRequestDto;
import delivery.model.entity.UserEntity;
import delivery.model.UserRole;
import delivery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public AuthenticationResponse register(UserRequestDto request) {
        var user = UserEntity.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password())) // Шифруем!
                .role(UserRole.USER) // Даем роль по умолчанию
                .build();

        repository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
        } catch (AuthenticationException e) {
            // Check backup codes
            var user = repository.findByEmail(request.email())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            for (String hashedCode : new HashSet<>(user.getBackupCodes())) {
                if (passwordEncoder.matches(request.password(), hashedCode)) {
                    // Remove used code
                    user.getBackupCodes().remove(hashedCode);
                    repository.save(user);

                    // Generate token
                    var jwtToken = jwtService.generateToken(user);
                    return AuthenticationResponse.builder()
                            .token(jwtToken)
                            .build();
                }
            }
            throw e; // Rethrow if no backup code matches
        }

        var user = repository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found after auth"));

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public List<String> generateBackupCodes(String email) {
        var user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> plainCodes = new ArrayList<>();
        Set<String> hashedCodes = new HashSet<>();

        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 10; i++) {
            String code = generateRandomCode(random);
            plainCodes.add(code);
            hashedCodes.add(passwordEncoder.encode(code));
        }

        user.setBackupCodes(hashedCodes);
        repository.save(user);

        return plainCodes;
    }

    private String generateRandomCode(SecureRandom random) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}