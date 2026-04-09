package krilovs.andrejs.enlabs.interview.event;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.StringJoiner;
import java.util.UUID;

@Getter
@Setter
public class SportEvent {
  @Null(message = "Identifier generated automatically")
  private UUID id;
  @NotBlank(message = "Event name Should be defined")
  private String name;
  @NotBlank(message = "Sport type should be defined")
  private String sportType;
  @Null(message = "Status is set automatically")
  private SportEventStatus status;
  @Future(message = "Start time cannot be in past")
  private LocalDateTime startTime;

  @Override
  public String toString() {
    return new StringJoiner(
      ", ",
      SportEvent.class.getSimpleName() + "[", "]"
    ).add("id=" + id)
     .add("name='" + name + "'")
     .add("sportType='" + sportType + "'")
     .add("status=" + status)
     .add("startTime=" + startTime)
     .toString();
  }
}
