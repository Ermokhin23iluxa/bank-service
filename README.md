# Bank Service — README

> Коротко: это небольшая демонстрационная реализация микросервиса (bank-service) для работы с картами и операциями
> с использованием Spring Boot, PostgreSQL, Kafka и Redis.
> В проекте реализован Outbox pattern для надёжной отправки событий в Kafka,
> идемпотентность при приёме событий, базовые сущности (Card / Transaction / Outbox / ProcessedEvent) и примитивный consumer для отладки.
---

## Содержание

* [Что реализовано](#что-реализовано)
* [Архитектура и паттерны](#архитектура-и-паттерны)
* [Технологии](#технологии)
* [Схема БД — таблицы и поля (кратко)](#схема-бд---таблицы-и-поля-кратко)
* [Публичные API (кратко)](#публичные-api-кратко)
* [Поток событий (Outbox → Kafka → Consumer)](#поток-событий-outbox--kafka--consumer)
* [Идемпотентность и обработка дубликатов](#идемпотентность-и-обработка-дубликатов)
* [Транзакции и блокировки при денежных операциях](#транзакции-и-блокировки-при-денежных-операциях)

---

## Что реализовано

* Сущности: `Card`, `Transaction`, `OutboxEvent`, `ProcessedEvent`, (планируется `User`).
* REST API: создание/удаление/просмотр карт; endpoint для создания транзакций (top-up/withdraw).
* Outbox pattern: запись событий в таблицу `outbox`, фоновый `OutboxPublisher` отправляет сообщения в Kafka и помечает их как `processed`.
* Producer: `KafkaMessageSender` с retry/@Recover (spring-retry), отправка `eventId` как header и `aggregateId` как key.
* Consumer: `CardEventsKafkaConsumer` (минимальный) — читает сообщения и логирует / применяет бизнес-логику.
* Механизм idempotency: таблица `processed_events` (или processed flag) для отбрасывания повторных событий.
* Примитивные подходы к безопасности PAN/CVV: PAN шифруется (panCiphertext),
  дополнительно сохраняется `panHash` (sha256) для быстрого поиска, maskedPan доступен в API (без CVV).
  CVV хранится в зашифрованном виде `cvvCiphertext` / `cvvHash`.

---

## Архитектура и паттерны

* **Outbox pattern** — жёсткая гарантия отправки: сначала запись в DB (business transaction), потом фоновой процесс публикует в Kafka и помечает outbox как processed.
* **Producer-side sync + DB mark** — `kafkaTemplate.send(...).get()` для простоты подтверждения и последующая пометка `processed = true`.
* **Idempotency on consumer** — consumer сохраняет processed event id (или использует уникальный индекс idempotency_key), чтобы избежать duplicate processing.
* **Пессимистическая блокировка** для single-card операций — `@Lock(PESSIMISTIC_WRITE)` / `SELECT ... FOR UPDATE` (используется в `CardRepository.findByIdForUpdate`)
* при пополнении/списании, чтобы избежать race-conditions на баланс.
* **UUID** идентификаторы для глобальной уникальности (удобнее для outbox / event id).
* **Шифрование + хеширование**: PAN шифруется для защиты, параллельно сохраняется SHA-256 (panHash) для быстрого поиска и проверки уникальности без хранения plaintext.
* **Retry + Recover** для producer: spring-retry + @Recover (сохранение в DLQ при исчерпании попыток).

---

## Технологии

* Java 17, Spring Boot (Web, Data JPA, Kafka, Retry, Validation).
* PostgreSQL (в Docker).
* Kafka + Zookeeper (в Docker).
* Redis (в Docker) — зарезервирован (кеш/rate-limit/locks).
* Docker / docker-compose — поднятие инфраструктуры.
* Kafdrop (UI) для отладки Kafka сообщений (опционально).
* Maven, Lombok, MapStruct (mapper).
* (Планы) Liquibase / Flyway для миграций; Testcontainers для integration tests.

---
## Схема БД — кратко

(В проекте есть JPA-entity; миграции Liquibase пока опциональны)

* `users` — (планируется) id (UUID/Long), email, name, создано/обновлено.
* `cards` — id (UUID), user_id (FK), pan_ciphertext, pan_hash, masked_pan, expire_date, cvv_ciphertext, balance (decimal), currency, status, created_at, version.
* `transactions` — id (UUID), card_id, type (CREDIT/DEBIT), amount, currency, idempotency_key, created_at.
* `outbox` — id (UUID), aggregate_type, aggregate_id (UUID), event_type, payload (jsonb), processed (boolean), created_at.
* `processed_events` — event_id (UUID PK), processed_at.
* `outbox_dead_letter` — id, outbox_id, payload, error, created_at (опционально).

---

## Публичные API (коротко)

> Примеры — подставь правильные пути/контроллеры в коде

**Cards**

* `POST /api/v1/cards` — создать карту (возвращает `CardDto` с maskedPan).
* `GET /api/v1/cards` — список карт пользователя.
* `GET /api/v1/cards/{id}` — детали карты.
* `DELETE /api/v1/cards/{id}` — удалить карту.

**Transactions**

* `POST /cards/{id}/topup` — пополнение карты.
* `POST /cards/{id}/withdraw` — списание с карты.

  * Заголовок `Idempotency-Key` (опционально) — для защиты от повторных запросов.
* Параметры: `amount`, и т.д.


---

## Поток событий (Outbox → Kafka → Consumer) — шаги

1. Business transaction создаёт запись `Transaction` и пишет `OutboxEvent` в той же БД-транзакции.
2. `OutboxPublisher` (фоновый @Scheduled) находит необработанные `outbox` записи.
3. Для каждой записи:

   * `KafkaMessageSender.send(topic, payload, eventId, aggregateId)` отправляет сообщение в Kafka (sync `.get()` в текущей реализации).
   * После успеха выполняется `outbox.markProcessed(id)` (лучше в отдельной короткой транзакции `REQUIRES_NEW`).
4. Consumer получает сообщение, проверяет `processed_events` (или `processed` в outbox), выполняет бизнес-логику и, при успехе,
сохраняет запись в `processed_events` (или меняет состояние агрегата).

---

## Идемпотентность и обработка дубликатов

* Producer-side: `Idempotency-Key` для API (client) предотвращает повторную обработку одинаковых запросов (внутри `Transaction` проверяется наличие записи с тем же `idempotencyKey`).
* Consumer-side: `processed_events` таблица (insert). При попытке повторного вставления — уникальный индекс делает вставку невозможной → duplicate skip.
* DB-constraint: рекомендовано иметь уникальный индекс на `outbox.idempotency_key` (partial unique index WHERE not null) и на `processed_events.event_id`.

---

## Транзакции и блокировки при денежных операциях

* Для single-card top-up/withdraw используется **pessimistic write lock** (SELECT FOR UPDATE) через `@Lock(LockModeType.PESSIMISTIC_WRITE)` в репозитории:
это предотвращает «lost update» при параллельных запросах.
* `@Version` (оптимистическая блокировка) — также можно использовать, но для money-transfer лучше пессимистическая блокировка или атомарные записи в ledger.
* При переводах между картами: стоит использовать **ledger** (журнал операций) и применить транзакцию, сохраняющую debit + credit в одной
атомарной транзакции, чтобы обеспечить консистентный порядок и суммарную целостность.


---

## Заключение

В этой репозитории реализован рабочий каркас микросервиса банковских карт с корректной (базовой и безопасной) 
обработкой отправки событий в Kafka через Outbox pattern, базовыми механизмами идемпотентности и protection для PAN/CVV.


Если хочешь — я могу:

* Сгенерировать готовый `README.md` в репозиторий (с тем же содержимым).
* Добавить секцию «Примеры запросов Postman» (json + curl).
* Написать шаблон `docker-compose` с Kafdrop/akhq включённым и корректной конфигурацией advertised listeners для локального dev.
