CREATE TABLE users
(
    id         BIGSERIAL PRIMARY KEY,
    first_name TEXT    NOT NULL,
    last_name  TEXT    NOT NULL,
    age        INTEGER NOT NULL,
    email      TEXT UNIQUE,
    password   TEXT    NOT NULL
);