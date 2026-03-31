package krilovs.andrejs.enlabs.interview.event;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class SportEvent {
  private UUID id;
  private String name;
  private String sport;
  private SportEventStatus status;
  private LocalDateTime startTime;
}
