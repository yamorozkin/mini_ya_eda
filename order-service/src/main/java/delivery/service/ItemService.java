package delivery.service;

import delivery.model.entity.ItemEntity;
import delivery.repository.ItemJpaRepository;
import http.order.model.dto.CreateItemRequestDto;
import http.order.model.dto.ItemResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemJpaRepository itemRepository;

    public ItemResponseDto createItem(CreateItemRequestDto request) {
        ItemEntity item = new ItemEntity();
        item.setName(request.name());
        item.setPrice(request.price());
        item.setImageUrl(request.imageUrl());

        ItemEntity saved = itemRepository.save(item);

        return new ItemResponseDto(
                saved.getId(),
                saved.getName(),
                saved.getPrice(),
                saved.getImageUrl()
        );
    }

    public List<ItemResponseDto> getAllItems() {
        return itemRepository.findAll().stream()
                .map(item -> new ItemResponseDto(
                        item.getId(),
                        item.getName(),
                        item.getPrice(),
                        item.getImageUrl()
                ))
                .toList();
    }
}