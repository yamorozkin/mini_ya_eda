package delivery.external;

public record UserProfileResponse(
        Long id,
        String name,
        String email,
        String role
) {
}
