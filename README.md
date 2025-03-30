
## Swagger UI
- http://localhost:8080/swagger-ui/index.html

## Application Architecture Overview

```mermaid
graph TD;
    subgraph Client
        C1[API Consumer: Postman, Curl, etc.]
    end

    subgraph Server[Spring Boot Application]
        S1[REST Controller]
        S2[Service Layer]
        S3[Repository Layer]
    end

    subgraph Database[Message Queue Database]
        D1[Topics Table]
        D2[Queues Table]
        D3[Messages Table]

    end

    C1 -->|HTTP Request| S1
    S1 -->|Publish/Consume Requests| S2
    S2 -->|Store/Retrieve| S3
    S3 -->|Persist Data| D1
    S3 -->|Persist Data| D2
    S3 -->|Persist Data| D3
    S2 -->|Return Response| S1
    S1 -->|HTTP Response| C1
```

## Database Design
```mermaid
erDiagram
    TOPIC {
        int topic_id PK
        varchar name
        varchar description
    }
    QUEUE {
        int queue_id PK
        varchar name
        varchar description
    }
    PERSON {
        int person_id PK
        varchar username
    }
    MESSAGE {
        int msg_id PK
        text msg_content
        int person_id FK
        int queue_id FK
        datetime created_at
    }
    TOPIC_MESSAGE {
        int topic_id FK
        int msg_id FK
    }

    TOPIC ||--o{ TOPIC_MESSAGE : "One to Many"
    MESSAGE ||--o{ TOPIC_MESSAGE : "One to Many"
    QUEUE ||--o{ MESSAGE : "One to Many"
    PERSON ||--o{ MESSAGE : "One to Many"

```

```
- Séance du 03/01
- JSON acyclic
- Cascade (Persistance)
- Queries
- Queues : impossible de supprimer un message non lu
- Gestion des Topics
- Relation N-N avec Message
- Numérotation interne des messages dans un topic
- GET : récupération d'une liste de massage à partir d'un numéro donnée
- Recherche de message par contenu partiel
- Suppression d'un message d'un Topic :  suppression du message uniquement si plus dans aucun Topic
```