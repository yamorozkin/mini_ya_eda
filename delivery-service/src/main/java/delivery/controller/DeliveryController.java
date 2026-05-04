package delivery.controller;

import delivery.repository.DeliveryEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryEntityRepository repository;

    @GetMapping("/streets")
    public List<String> getAvailableStreets() {
        return repository.findAll()
                .stream()
                .map(delivery -> delivery.getStreet())
                .distinct()
                .sorted()
                .toList();
    }
}
