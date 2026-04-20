package delivery.service;

import delivery.mapper.UserEntityMapper;
import delivery.model.entity.UserEntity;
import delivery.model.entity.UserRequestDto;
import delivery.model.entity.UserResponseDto;
import delivery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository repository;
    private final UserEntityMapper mapper;

    public UserEntity create(UserRequestDto request){
        UserEntity entity = mapper.toUserEntity(request);
        return repository.save(entity);
    }
}
