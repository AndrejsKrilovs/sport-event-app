package krilovs.andrejs.enlabs.interview.subscription;

import krilovs.andrejs.enlabs.interview.event.StatusChangedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {
  final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

  @GetMapping
  public SseEmitter addSubscription() {
    var emitter = new SseEmitter(Long.MAX_VALUE);
    emitters.add(emitter);
    emitter.onCompletion(() -> emitters.remove(emitter));
    emitter.onTimeout(() -> emitters.remove(emitter));
    emitter.onError(e -> emitters.remove(emitter));
    return emitter;
  }

  @EventListener
  public void onEvent(StatusChangedEvent event) {
    emitters.forEach(emitter -> {
      try {
        emitter.send(SseEmitter.event().data(event.event()));
      }
      catch (IOException e) {
        emitter.complete();
        emitters.remove(emitter);
      }
    });
  }
}
