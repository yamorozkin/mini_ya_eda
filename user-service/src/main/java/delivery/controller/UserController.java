package delivery.controller;

import delivery.mapper.UserEntityMapper;
import delivery.model.entity.UserRequestDto;
import delivery.model.entity.UserResponseDto;
import delivery.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/log-in")
public class UserController {

    private final UserService service;
    private final UserEntityMapper mapper;
    private final Logger log =  LoggerFactory.getLogger(UserController.class);

    @PostMapping("/registration")
    public UserResponseDto create(
            @RequestBody UserRequestDto request
            ){
        var entity = service.create(request);
        log.info("Created new user ={}", entity);
        return mapper.toUserResponseDto(entity);
    }

}
