CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name  VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL UNIQUE
);


CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description  VARCHAR NOT NULL,
    requester_id BIGINT REFERENCES users (id),
    created      TIMESTAMP WITHOUT TIME ZONE
);


CREATE TABLE IF NOT EXISTS items
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name         VARCHAR(50) NOT NULL,
    description  VARCHAR NOT NULL,
    available    BOOLEAN NOT NULL,
    user_id      BIGINT REFERENCES users (id),
    request_id   BIGINT REFERENCES requests (id)
);


CREATE TABLE IF NOT EXISTS bookings
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    start_date   TIMESTAMP WITHOUT TIME ZONE,
    end_date     TIMESTAMP WITHOUT TIME ZONE,
    item_id      BIGINT REFERENCES items (id),
    user_id      BIGINT REFERENCES users (id),
    status       VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text      VARCHAR NOT NULL,
    item_id   BIGINT REFERENCES items (id),
    user_id   BIGINT REFERENCES users (id),
    created   TIMESTAMP WITHOUT TIME ZONE
);