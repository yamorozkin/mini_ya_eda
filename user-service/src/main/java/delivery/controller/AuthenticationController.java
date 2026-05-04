package delivery.controller;

import delivery.model.AuthenticationRequest;
import delivery.model.AuthenticationResponse;
import delivery.model.UserRequestDto;
import delivery.model.entity.UserEntity;
import delivery.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody UserRequestDto request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/generate-backup-codes")
    public ResponseEntity<List<String>> generateBackupCodes(
            @AuthenticationPrincipal UserEntity user
    ) {
        return ResponseEntity.ok(service.generateBackupCodes(user.getEmail()));
    }
}