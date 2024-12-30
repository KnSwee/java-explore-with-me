
CREATE TABLE IF NOT EXISTS category(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    category_name VARCHAR(50) UNIQUE NOT NULL,
    CONSTRAINT pk_category PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS users(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    username VARCHAR(250) NOT NULL,
    email VARCHAR(254) NOT NULL,
    CONSTRAINT UQ_USER_NAME UNIQUE(username),
    CONSTRAINT UQ_USER_EMAIL UNIQUE(email),
    CONSTRAINT pk_users PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS event(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    annotation VARCHAR(2000),
    category_id BIGINT NOT NULL REFERENCES category(id) ON DELETE RESTRICT,
    confirmed_requests BIGINT,
    created_on TIMESTAMP WITHOUT TIME ZONE,
    description VARCHAR(7000),
    event_date TIMESTAMP WITHOUT TIME ZONE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    paid BOOLEAN,
    participant_limit BIGINT,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN,
    status VARCHAR(20),
    title VARCHAR(120),
    views BIGINT,
    longitude FLOAT,
    latitude FLOAT,
    CONSTRAINT pk_event PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS event_requests(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    event_id BIGINT NOT NULL REFERENCES event(id) ON DELETE CASCADE,
    created TIMESTAMP WITHOUT TIME ZONE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(20),
    CONSTRAINT pk_event_requests PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS compilation(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    pinned BOOLEAN NOT NULL,
    title VARCHAR(255) NOT NULL,
    CONSTRAINT pk_compilation PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS event_compilation(
    event_id BIGINT REFERENCES event(id) ON DELETE CASCADE,
    compilation_id BIGINT REFERENCES compilation(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text VARCHAR(512) NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    event_id BIGINT NOT NULL REFERENCES event(id) ON DELETE CASCADE,
    updated TIMESTAMP WITHOUT TIME ZONE
)