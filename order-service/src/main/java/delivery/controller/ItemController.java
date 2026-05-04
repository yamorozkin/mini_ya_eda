package delivery.controller;

import delivery.external.UserHttpClient;
import delivery.service.ItemService;
import http.order.model.dto.CreateItemRequestDto;
import http.order.model.dto.ItemResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final UserHttpClient userHttpClient;

    @GetMapping
    public List<ItemResponseDto> getAllItems() {
        return itemService.getAllItems();
    }

    @PostMapping
    public ItemResponseDto createItem(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody CreateItemRequestDto request
    ) {
        // Проверяем роль пользователя
        var user = userHttpClient.getMyProfile(authorizationHeader);
        if (!"ADMIN".equals(user.role())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can create items");
        }

        return itemService.createItem(request);
    }
}