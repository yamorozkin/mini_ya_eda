package delivery.model;

public record UserResponseDto(
        String name,
        String email,
        UserRole role
) {
}
