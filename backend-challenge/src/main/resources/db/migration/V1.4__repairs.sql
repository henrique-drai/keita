CREATE TABLE repair_types
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT NOT NULL
);

CREATE TABLE repairs
(
    id              UUID PRIMARY KEY,
    status          VARCHAR(255) NOT NULL,
    user_id         UUID REFERENCES users (id) ON DELETE CASCADE,
    repair_type_id  UUID REFERENCES repair_types (id) ON DELETE CASCADE
);
