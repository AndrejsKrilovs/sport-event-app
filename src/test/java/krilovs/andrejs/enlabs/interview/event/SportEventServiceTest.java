package krilovs.andrejs.enlabs.interview.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SportEventServiceTest {
  @Mock
  private SportEventRepository repository;

  @Mock
  private ApplicationEventPublisher publisher;

  @InjectMocks
  private SportEventService service;

  private SportEvent event;

  @BeforeEach
  void setUp() {
    event = new SportEvent();
    event.setId(UUID.randomUUID());
    event.setName("Test");
    event.setSportType("football");
    event.setStatus(SportEventStatus.INACTIVE);
    event.setStartTime(LocalDateTime.now().plusHours(1));
  }

  @Test
  void shouldCreateEventWithGeneratedIdAndInactiveStatus() {
    when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    var result = service.create(new SportEvent());
    assertNotNull(result.getId());
    assertEquals(SportEventStatus.INACTIVE, result.getStatus());
    verify(repository).save(result);
  }

  @Test
  void shouldReturnEventById() {
    when(repository.findById(event.getId())).thenReturn(Optional.of(event));
    assertEquals(event, service.getById(event.getId()));
  }

  @Test
  void shouldThrowExceptionWhenEventNotFound() {
    var id = UUID.randomUUID();
    when(repository.findById(id)).thenReturn(Optional.empty());
    assertThrows(IllegalArgumentException.class, () -> service.getById(id));
  }

  @Test
  void shouldFilterBySportType() {
    var another = new SportEvent();
    another.setSportType("hockey");

    when(repository.findItems()).thenReturn(List.of(event, another));
    var result = service.getItems("football", null, 0, 10, "id", "asc");
    assertFalse(result.isEmpty());
    assertEquals("football", result.getFirst().getSportType());
  }

  @Test
  void shouldPaginateResults() {
    var e1 = new SportEvent();
    e1.setId(UUID.randomUUID());
    var e2 = new SportEvent();
    e2.setId(UUID.randomUUID());

    when(repository.findItems()).thenReturn(List.of(e1, e2));
    var result = service.getItems(null, null, 1, 1, "id", "asc");
    assertFalse(result.isEmpty());
  }

  @Test
  void shouldChangeStatusAndPublishEvent() {
    when(repository.findById(event.getId())).thenReturn(Optional.of(event));
    when(repository.save(any())).thenReturn(event);

    var result = service.changeStatus(event.getId(), SportEventStatus.ACTIVE);
    assertEquals(SportEventStatus.ACTIVE, result.getStatus());
    verify(publisher).publishEvent(any(StatusChangedEvent.class));
  }

  @Test
  void shouldNotAllowChangeFromFinished() {
    event.setStatus(SportEventStatus.FINISHED);
    when(repository.findById(event.getId())).thenReturn(Optional.of(event));
    assertThrows(IllegalStateException.class, () -> service.changeStatus(event.getId(), SportEventStatus.ACTIVE));
  }

  @Test
  void shouldNotAllowInvalidTransition() {
    event.setStatus(SportEventStatus.INACTIVE);
    when(repository.findById(event.getId())).thenReturn(Optional.of(event));
    assertThrows(IllegalStateException.class, () -> service.changeStatus(event.getId(), SportEventStatus.FINISHED));
  }

  @Test
  void shouldNotActivatePastEvent() {
    event.setStartTime(LocalDateTime.now().minusHours(1));
    when(repository.findById(event.getId())).thenReturn(Optional.of(event));
    assertThrows(IllegalStateException.class, () -> service.changeStatus(event.getId(), SportEventStatus.ACTIVE));
  }
}