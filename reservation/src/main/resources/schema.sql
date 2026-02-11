DROP INDEX IF EXISTS uk_session_id_seat_id;

CREATE UNIQUE INDEX uk_session_id_seat_id
    ON reserve (session_id)
    WHERE status NOT IN ('EXPIRED', 'CANCELED');

CREATE UNIQUE INDEX IF NOT EXISTS uk_seats_session_seat
    ON seats (session_id, seat_id);