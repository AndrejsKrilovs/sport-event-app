# Sport Events API

REST API for managing sport events with real-time updates via SSE.

---

## 🚀 Tech Stack

- Java 21
- Spring Boot 3
- Lombok
- In-memory storage (ConcurrentHashMap)
- SSE (Server-Sent Events)

---

## 📦 Event Model

Each event contains:

- `id` (UUID)
- `name` (String)
- `sportType` (String, e.g. football, hockey)
- `status` (INACTIVE, ACTIVE, FINISHED)
- `startTime` (LocalDateTime)

---

## 📡 API Endpoints

### 1. Create Event

POST /events

Request body:
{
  "name": "Champions League Final",
  "sportType": "football",
  "startTime": "2026-04-01T18:00:00"
}

---

### 2. Get Events

GET /events

Query params:
- sportType
- status
- page
- size
- sortBy
- direction

---

### 3. Get Event by ID

GET /events/{id}

---

### 4. Change Event Status

PATCH /events/{id}/status?status=ACTIVE

---

## 🔄 Event Status Rules

- INACTIVE → ACTIVE
- ACTIVE → FINISHED
- INACTIVE → FINISHED ❌
- FINISHED → ANY ❌

---

## 📡 Subscriptions (SSE)

GET /subscriptions

---

## 🧪 Testing

- Unit tests
- Integration tests

---

## ▶️ Run as usual

./gradlew bootRun

## 🐳 Run with Docker

```bash
docker build -t sport-events .
docker run -p 8080:8080 sport-events