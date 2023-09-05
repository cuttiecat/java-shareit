DROP ALL OBJECTS;

CREATE TABLE IF NOT EXISTS users (
        user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
        user_name VARCHAR(48) NOT NULL,
        user_email VARCHAR(48) NOT NULL,
        CONSTRAINT unique_user_email UNIQUE (user_email)
        );

CREATE TABLE IF NOT EXISTS requests (
        request_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
        request_description VARCHAR(1024) NOT NULL,
        requestor_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
        request_created TIMESTAMP WITHOUT TIME ZONE NOT NULL
        );

CREATE TABLE IF NOT EXISTS items (
        item_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
        item_name VARCHAR(48) NOT NULL,
        item_description VARCHAR(200) NOT NULL,
        item_available BOOLEAN NOT NULL,
        owner_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
        request_id BIGINT REFERENCES requests(request_id) ON DELETE SET NULL
        );

CREATE TABLE IF NOT EXISTS comments (
        comment_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
        comment_text VARCHAR(1024) NOT NULL,
        item_id BIGINT NOT NULL REFERENCES items(item_id) ON DELETE CASCADE,
        author_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
        comment_created TIMESTAMP WITHOUT TIME ZONE NOT NULL
        );

CREATE TABLE IF NOT EXISTS bookings (
        booking_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
        booking_start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
        booking_end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
        item_id BIGINT NOT NULL REFERENCES items(item_id) ON DELETE CASCADE,
        booker_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
        booking_status VARCHAR NOT NULL
        );