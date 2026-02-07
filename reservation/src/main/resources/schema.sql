DROP INDEX IF EXISTS uk_session_id_seat_id;

CREATE UNIQUE INDEX uk_session_id_seat_id
    ON reserve (session_id, seat_id)
    WHERE status NOT IN ('EXPIRED', 'CANCELED');