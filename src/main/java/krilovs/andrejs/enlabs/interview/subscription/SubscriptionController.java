package krilovs.andrejs.enlabs.interview.subscription;

import krilovs.andrejs.enlabs.interview.event.StatusChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {
  final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

  @GetMapping
  public SseEmitter addSubscription() {
    var emitter = new SseEmitter(Long.MAX_VALUE);
    emitters.add(emitter);
    log.info("New SSE subscription added. Total subscribers={}", emitters.size());
    emitter.onCompletion(() -> {
      emitters.remove(emitter);
      log.info("SSE subscription completed. Total subscribers={}", emitters.size());
    });
    emitter.onTimeout(() -> {
      emitters.remove(emitter);
      log.warn("SSE subscription timeout. Total subscribers={}", emitters.size());
    });
    emitter.onError(e -> {
      emitters.remove(emitter);
      log.error("SSE subscription error: {}", e.getMessage());
    });
    return emitter;
  }

  @EventListener
  public void onEvent(StatusChangedEvent event) {
    log.debug("Sending sport event {} notification to subscribers", event.event());
    emitters.forEach(emitter -> {
      try {
        emitter.send(SseEmitter.event().data(event.event()));
      }
      catch (IOException e) {
        log.warn("Failed to send event to subscriber. Removing emitter. Error={}", e.getMessage());
        emitter.complete();
        emitters.remove(emitter);
      }
    });
    log.debug("Event delivery completed");
  }
}
