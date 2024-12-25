CREATE TABLE IF NOT EXISTS endpoint_hit
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    app
    VARCHAR
(
    50
) NOT NULL,
    uri VARCHAR
(
    100
) NOT NULL,
    ip VARCHAR
(
    20
) NOT NULL,
    timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL
    );