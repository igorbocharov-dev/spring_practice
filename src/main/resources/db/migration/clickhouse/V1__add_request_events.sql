CREATE TABLE request_events
(
    event_time    DateTime64(3, 'UTC'),
    doc_id        Nullable(UUID),
    event_type    LowCardinality(String),
    status        LowCardinality(String),
    processing_ms Nullable(UInt32),
    http_status   Nullable(UInt16),
    error_type    Nullable(String)
)
    ENGINE = MergeTree
        ORDER BY event_time