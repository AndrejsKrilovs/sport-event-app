package krilovs.andrejs.enlabs.interview.event;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class SportEventRepository {
  private final ConcurrentHashMap<UUID, SportEvent> storage = new ConcurrentHashMap<>();

  public SportEvent save(SportEvent event) {
    storage.put(event.getId(), event);
    return event;
  }

  public Optional<SportEvent> findById(UUID id) {
    return Optional.ofNullable(storage.get(id));
  }

  public List<SportEvent> findItems() {
    return new CopyOnWriteArrayList<>(storage.values());
  }
}
