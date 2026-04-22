package delivery.model;

public record UserResponseDto(
        Long id,
        String name,
        String email,
        UserRole role
) {
}
