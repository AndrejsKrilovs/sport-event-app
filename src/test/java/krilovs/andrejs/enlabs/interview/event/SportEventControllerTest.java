package krilovs.andrejs.enlabs.interview.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.reactive.server.WebTestClient.bindToServer;

@TestMethodOrder(MethodOrderer.Random.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SportEventControllerTest {
  @LocalServerPort
  private int port;

  private WebTestClient client;

  @Autowired
  private SportEventRepository repository;

  @BeforeEach
  void setUp() {
    client = bindToServer()
      .baseUrl("http://localhost:" + port)
      .build();

    repository.clearStorage();
  }

  @Test
  void shouldCreateEvent() {
    var created = createEvent("Test", "football");
    assertNotNull(created.getId());
    assertEquals(SportEventStatus.INACTIVE, created.getStatus());
  }

  @Test
  void shouldReturnAllEvents() {
    createEvent("Event1", "football");
    createEvent("Event2", "hockey");

    var events = getEvents("");
    assertFalse(events.isEmpty());
  }

  @Test
  void shouldFilterBySportType() {
    createEvent("Football", "football");
    createEvent("Hockey", "hockey");

    var events = getEvents("?sportType=football");
    assertEquals(1, events.size());
    assertEquals("football", events.getFirst().getSportType());
  }

  @Test
  void shouldPaginateResults() {
    createEvent("A", "football");
    createEvent("B", "football");
    createEvent("C", "football");

    var page1 = getEvents("?page=0&size=2&sortBy=name&direction=asc");
    var page2 = getEvents("?page=1&size=2&sortBy=name&direction=asc");

    assertEquals(2, page1.size());
    assertEquals(1, page2.size());
    assertNotEquals(page1.getFirst().getId(), page2.getFirst().getId());
  }

  @Test
  void shouldGetEventById() {
    var created = createEvent("Test", "football");
    var event = client.get()
                      .uri("/events/{id}", created.getId())
                      .exchange()
                      .expectStatus().isOk()
                      .expectBody(SportEvent.class)
                      .returnResult()
                      .getResponseBody();

    assertNotNull(event);
    assertEquals(created.getId(), event.getId());
  }

  @Test
  void shouldChangeStatus() {
    var created = createEvent("Test", "football");

    client.patch()
          .uri("/events/{id}/status?status=ACTIVE", created.getId())
          .exchange()
          .expectStatus().isOk();

    var updated = client.get()
                        .uri("/events/{id}", created.getId())
                        .exchange()
                        .expectBody(SportEvent.class)
                        .returnResult()
                        .getResponseBody();

    assertEquals(SportEventStatus.ACTIVE, updated.getStatus());
  }

  private SportEvent createEvent(String name, String sport) {
    var request = new SportEvent();
    request.setName(name);
    request.setSportType(sport);
    request.setStartTime(LocalDateTime.now().plusHours(1));

    return client.post()
                 .uri("/events")
                 .bodyValue(request)
                 .exchange()
                 .expectStatus().isOk()
                 .expectBody(SportEvent.class)
                 .returnResult()
                 .getResponseBody();
  }

  private List<SportEvent> getEvents(String query) {
    return client.get()
                 .uri("/events" + query)
                 .exchange()
                 .expectStatus().isOk()
                 .expectBody(new ParameterizedTypeReference<List<SportEvent>>() {})
                 .returnResult()
                 .getResponseBody();
  }
}