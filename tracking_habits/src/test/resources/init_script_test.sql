
CREATE SEQUENCE users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS users(
    id BIGINT PRIMARY KEY DEFAULT nextval('users_id_seq'),
    name VARCHAR(255),
    password VARCHAR(255),
    email VARCHAR(255),
    role VARCHAR(255),
    create_at TIMESTAMP(6) WITHOUT TIME ZONE,
    update_at TIMESTAMP(6) WITHOUT TIME ZONE,
    is_blocked BOOLEAN
);

CREATE SEQUENCE habits_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS habits (
    id BIGINT PRIMARY KEY DEFAULT nextval('habits_id_seq'),
    title VARCHAR(255) NOT NULL,
    text VARCHAR(255) NOT NULL,
    execution_rate VARCHAR(255),
    create_at TIMESTAMP(6) WITHOUT TIME ZONE,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE
);

CREATE SEQUENCE habit_execution_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS habit_execution (
    id BIGINT PRIMARY KEY DEFAULT nextval('habit_execution_id_seq'),
    date TIMESTAMP(6) WITHOUT TIME ZONE,
    habit_id BIGINT REFERENCES habits(id) ON DELETE CASCADE
);
