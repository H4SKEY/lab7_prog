CREATE TYPE color AS ENUM ('GREEN', 'RED', 'BLUE', 'YELLOW', 'ORANGE');

CREATE TYPE ticket_type AS ENUM ('VIP', 'BUDGETARY', 'CHEAP');

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    login TEXT NOT NULL,
    password TEXT NOT NULL
);

CREATE TABLE coordinates (
    id SERIAL PRIMARY KEY,
    x BIGINT NOT NULL,
    y BIGINT NOT NULL CHECK(y <= 166),
    user_id INTEGER REFERENCES users(id)
);

CREATE TABLE persons (
    id SERIAL PRIMARY KEY,
    passport_id VARCHAR(49) CHECK (char_length(passport_id) >= 4),
    eye_color color,
    hair_color color,
    user_id INTEGER REFERENCES users(id)
);

CREATE TABLE tickets (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    coordinates_id INTEGER REFERENCES coordinates(id),
    creation_date TIMESTAMP NOT NULL,
    price INTEGER CHECK (price > 0),
    discount INTEGER CHECK (discount >= 0 AND discount <= 100),
    type ticket_type,
    person_id INTEGER REFERENCES persons(id),
    user_id INTEGER REFERENCES users(id)
);
