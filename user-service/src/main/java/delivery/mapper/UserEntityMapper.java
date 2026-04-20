package delivery.mapper;

import delivery.model.entity.UserEntity;
import delivery.model.entity.UserRequestDto;
import delivery.model.entity.UserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

//@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
//        componentModel = MappingConstants.ComponentModel.SPRING)
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserEntityMapper {
    UserEntity toUserEntity(UserRequestDto requestDto);
    UserResponseDto toUserResponseDto(UserEntity entity);

}
