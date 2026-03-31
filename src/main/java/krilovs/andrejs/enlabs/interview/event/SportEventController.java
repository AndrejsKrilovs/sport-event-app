package krilovs.andrejs.enlabs.interview.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class SportEventController {
  private final SportEventService service;

  @PostMapping
  public SportEvent create(@RequestBody @Valid SportEvent event) {
    return service.create(event);
  }

  @GetMapping
  public List<SportEvent> getItems(
    @RequestParam(required = false) String sportType,
    @RequestParam(required = false) SportEventStatus status,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(defaultValue = "startTime") String sortBy,
    @RequestParam(defaultValue = "asc") String direction
  ) {
    return service.getItems(sportType, status, page, size, sortBy, direction);
  }

  @GetMapping("/{id}")
  public SportEvent getById(@PathVariable UUID id) {
    return service.getById(id);
  }

  @PatchMapping("/{id}/status")
  public SportEvent changeStatus(
    @PathVariable UUID id,
    @RequestParam SportEventStatus status
  ) {
    return service.changeStatus(id, status);
  }
}
