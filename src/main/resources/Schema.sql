CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email varchar(255) NOT NULL,
    name varchar(127) NOT NULL,
    CONSTRAINT unique_email UNIQUE(email)
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text VARCHAR(1023) NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_requests_to_users FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name varchar(127) NOT NULL,
    description varchar(1023) NOT NULL,
    available boolean  NOT NULL,
    request_id BIGINT,
    CONSTRAINT fk_items_to_users FOREIGN KEY(user_id) REFERENCES users(id),
    CONSTRAINT fk_items_to_requests FOREIGN KEY(request_id) REFERENCES requests(id)
);

CREATE TABLE IF NOT EXISTs bookings (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  user_id BIGINT NOT NULL,
  item_id BIGINT NOT NULL,
  start_date TIMESTAMP NOT NULL,
  end_date TIMESTAMP NOT NULL,
  status VARCHAR(255) NOT NULL,
  CONSTRAINT fk_booking_to_users FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_booking_to_items FOREIGN KEY (item_id) REFERENCES items(id)
);

CREATE TABLE IF NOT EXISTs comments (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  text VARCHAR(2047) NOT NULL,
  user_id BIGINT NOT NULL,
  item_id BIGINT NOT NULL,
  created TIMESTAMP,
  CONSTRAINT fk_comment_to_users FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_comment_to_items FOREIGN KEY (item_id) REFERENCES items(id)
);
