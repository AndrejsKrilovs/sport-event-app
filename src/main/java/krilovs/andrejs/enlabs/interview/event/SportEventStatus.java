package krilovs.andrejs.enlabs.interview.event;

public enum SportEventStatus {
  INACTIVE,
  ACTIVE,
  FINISHED;

  public boolean canChangeEventStatusTo(SportEventStatus target) {
    return switch (this) {
      case INACTIVE -> target == ACTIVE;
      case ACTIVE -> target == FINISHED;
      case FINISHED -> false;
    };
  }
}
