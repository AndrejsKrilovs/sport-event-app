package krilovs.andrejs.enlabs.interview.subscription;

import krilovs.andrejs.enlabs.interview.event.SportEvent;
import krilovs.andrejs.enlabs.interview.event.StatusChangedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class SubscriptionControllerTest {
  private SubscriptionController controller;

  @BeforeEach
  void setUp() {
    controller = new SubscriptionController();
  }

  @Test
  void shouldAddEmitter() {
    var emitter = controller.addSubscription();
    assertNotNull(emitter);
    assertDoesNotThrow(() -> controller.onEvent(new StatusChangedEvent(new SportEvent())));
  }

  @Test
  void shouldSendEventToEmitter() throws IOException {
    var emitter = spy(new SseEmitter());
    controller = new SubscriptionController();
    controller.addSubscription();
    controller = new SubscriptionController() {{
      emitters.add(emitter);
    }};

    var event = new StatusChangedEvent(new SportEvent());
    controller.onEvent(event);
    verify(emitter).send(any(SseEmitter.SseEventBuilder.class));
  }
}