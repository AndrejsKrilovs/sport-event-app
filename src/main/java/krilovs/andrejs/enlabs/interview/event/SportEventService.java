package krilovs.andrejs.enlabs.interview.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SportEventService {
  private final SportEventRepository repository;
  private final ApplicationEventPublisher publisher;

  public SportEvent create(SportEvent event) {
    event.setId(UUID.randomUUID());
    event.setStatus(SportEventStatus.INACTIVE);
    return repository.save(event);
  }

  public SportEvent getById(UUID id) {
    return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Event with id %s not found".formatted(id)));
  }

  public List<SportEvent> getItems(String sportType, SportEventStatus status, int page, int size, String sortBy, String direction) {
    var comparator = switch (sortBy) {
      case "name" -> Comparator.comparing(SportEvent::getName, String.CASE_INSENSITIVE_ORDER);
      case "startTime" -> Comparator.comparing(SportEvent::getStartTime);
      case null, default -> Comparator.comparing(SportEvent::getId);
    };

    if ("desc".equalsIgnoreCase(direction)) {
      comparator = comparator.reversed();
    }

    return repository.findItems()
                     .stream()
                     .filter(e -> sportType == null || e.getSportType().equalsIgnoreCase(sportType))
                     .filter(e -> status == null || e.getStatus() == status)
                     .sorted(comparator)
                     .skip((long) page * size)
                     .limit(size)
                     .toList();
  }

  public SportEvent changeStatus(UUID id, SportEventStatus newStatus) {
    var event = getById(id);
    validate(event, newStatus);
    event.setStatus(newStatus);
    repository.save(event);
    publisher.publishEvent(new StatusChangedEvent(event));
    return event;
  }

  private void validate(SportEvent event, SportEventStatus newStatus) {
    var current = event.getStatus();

    if (current == SportEventStatus.FINISHED) {
      throw new IllegalStateException("Event which was finished cannot be changed");
    }
    if (!current.canChangeEventStatusTo(newStatus)) {
      throw new IllegalStateException("Event cannot be changed from %s to %s".formatted(current, newStatus));
    }
    if (newStatus == SportEventStatus.ACTIVE && event.getStartTime().isBefore(LocalDateTime.now())) {
      throw new IllegalStateException("Cannot activate past event");
    }
  }
}
