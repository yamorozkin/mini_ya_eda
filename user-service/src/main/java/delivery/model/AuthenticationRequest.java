package delivery.model;

public record AuthenticationRequest(
        String email,
        String password
) {
}