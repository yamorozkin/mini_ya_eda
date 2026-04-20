package delivery.model.entity;

import jakarta.persistence.Column;

public record UserRequestDto (
        String name,
        String email,
        String password
){
}
