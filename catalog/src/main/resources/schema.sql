-- Garante que o índice antigo (se existir) seja apagado
DROP INDEX IF EXISTS uk_movie_title_active;
DROP INDEX IF EXISTS uk_room_active_name;
DROP INDEX IF EXISTS uk_cinema_name_city;

-- Cria o índice parcial que permite nomes repetidos APENAS se active=false
CREATE UNIQUE INDEX uk_movie_title_active
    ON movie (title)
    WHERE active = true;

CREATE UNIQUE INDEX uk_room_active_name
    ON room (cinema_id, name)
    WHERE active = true;

CREATE UNIQUE INDEX uk_cinema_name_city
    ON cinema (name, city)
    WHERE active = TRUE;