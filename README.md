# Sport Events API

REST API for managing sport events with real-time updates.

## 🚀 Tech Stack

- Java 21
- Spring Boot 3
- Lombok
- In-memory storage (ConcurrentHashMap)

---

## 📦 Event Model

Each event contains:

- `id` (UUID)
- `name` (String)
- `sport` (String, e.g. football, hockey)
- `status` (INACTIVE, ACTIVE, FINISHED)
- `startTime` (LocalDateTime)

---
