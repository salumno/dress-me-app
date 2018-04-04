INSERT INTO "user"(name)
  SELECT 'Administrator'
  WHERE NOT EXISTS(
    SELECT id
    FROM "user"
    WHERE id = 1
  );

INSERT INTO "user"(name)
  SELECT 'Shimon'
  WHERE NOT EXISTS(
      SELECT id
      FROM "user"
      WHERE id = 2
  );

INSERT INTO user_data(login, hash_password, role, user_id)
  SELECT 'admin', '$2a$10$1jcIqkeEGVX.Lqz5zMLZFeuGQkxiwxUciBjzBleGXcnplIOn4A/4m', 'ADMIN', 1
  WHERE NOT EXISTS(
      SELECT id
      FROM user_data
      WHERE id = 1
  );

INSERT INTO user_data(login, hash_password, role, user_id)
  SELECT 'shimon', '$2a$10$1jcIqkeEGVX.Lqz5zMLZFeuGQkxiwxUciBjzBleGXcnplIOn4A/4m', 'USER', 2
  WHERE NOT EXISTS(
      SELECT id
      FROM user_data
      WHERE id = 2
  );

CREATE TABLE IF NOT EXISTS persistent_logins (
  username VARCHAR(64) NOT NULL,
  series VARCHAR(64) NOT NULL,
  token VARCHAR(64) NOT NULL,
  last_used TIMESTAMP NOT NULL,
  PRIMARY KEY (series)
);