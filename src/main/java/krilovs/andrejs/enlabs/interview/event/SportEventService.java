package krilovs.andrejs.enlabs.interview.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SportEventService {
  private final SportEventRepository repository;
  private final ApplicationEventPublisher publisher;

  public SportEvent create(SportEvent event) {
    event.setId(UUID.randomUUID());
    event.setStatus(SportEventStatus.INACTIVE);
    log.info(
      "Creating event: name={}, sportType={}, startTime={}",
      event.getName(), event.getSportType(), event.getStartTime()
    );
    return repository.save(event);
  }

  public SportEvent getById(UUID id) {
    log.debug("Fetching event by id={}", id);
    return repository.findById(id).orElseThrow(() -> {
      log.warn("Event with id {} not found", id);
      return new IllegalArgumentException("Event with id %s not found".formatted(id));
    });
  }

  public List<SportEvent> getItems(String sportType, SportEventStatus status, int page, int size, String sortBy, String direction) {
    log.debug(
      "Fetching events with filters: sportType={}, status={}, page={}, size={}, sortBy={}, direction={}",
      sportType, status, page, size, sortBy, direction
    );

    var comparator = switch (sortBy) {
      case "name" -> Comparator.comparing(SportEvent::getName, String.CASE_INSENSITIVE_ORDER);
      case "startTime" -> Comparator.comparing(SportEvent::getStartTime);
      case null, default -> Comparator.comparing(SportEvent::getId);
    };

    if ("desc".equalsIgnoreCase(direction)) {
      comparator = comparator.reversed();
    }

    var result = repository.findItems()
                     .stream()
                     .filter(e -> sportType == null || e.getSportType().equalsIgnoreCase(sportType))
                     .filter(e -> status == null || e.getStatus() == status)
                     .sorted(comparator)
                     .skip((long) page * size)
                     .limit(size)
                     .toList();

    log.debug("Filtered events count={}", result.size());
    if (result.isEmpty()) {
      log.info("No events found for given filters: sportType={}, status={}", sportType, status);
    }

    return result;
  }

  public SportEvent changeStatus(UUID id, SportEventStatus newStatus) {
    log.debug("Changing status for event id={} to {}", id, newStatus);
    var event = getById(id);
    validate(event, newStatus);
    event.setStatus(newStatus);
    repository.save(event);
    publisher.publishEvent(new StatusChangedEvent(event));
    log.info("Status for event with id={} is changed to {}", id, newStatus);
    return event;
  }

  private void validate(SportEvent event, SportEventStatus newStatus) {
    var current = event.getStatus();
    log.debug("Validating status change: id={}, from={}, to={}", event.getId(), event.getStatus(), newStatus);

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
