-- Garante que o índice antigo (se existir) seja apagado
DROP INDEX IF EXISTS uk_movie_title_active;

-- Cria o índice parcial que permite nomes repetidos APENAS se active=false
CREATE UNIQUE INDEX uk_movie_title_active
    ON movie (title)
    WHERE active = true;