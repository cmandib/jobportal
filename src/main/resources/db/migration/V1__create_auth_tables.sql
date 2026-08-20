CREATE TABLE auth_credentials (
                                  id            BIGSERIAL PRIMARY KEY,
                                  user_id       BIGINT NOT NULL UNIQUE,
                                  email         VARCHAR(255) NOT NULL UNIQUE,
                                  password_hash VARCHAR(255) NOT NULL,
                                  created_at    TIMESTAMP NOT NULL DEFAULT now(),
                                  updated_at    TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE auth_roles (
                            id   BIGSERIAL PRIMARY KEY,
                            name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE auth_user_roles (
                                 user_id BIGINT NOT NULL,
                                 role_id BIGINT NOT NULL REFERENCES auth_roles(id),
                                 PRIMARY KEY (user_id, role_id)
);

CREATE TABLE auth_refresh_tokens (
                                     id         BIGSERIAL PRIMARY KEY,
                                     user_id    BIGINT NOT NULL,
                                     token_hash VARCHAR(255) NOT NULL,
                                     expires_at TIMESTAMP NOT NULL,
                                     revoked    BOOLEAN NOT NULL DEFAULT false,
                                     created_at TIMESTAMP NOT NULL DEFAULT now()
);

INSERT INTO auth_roles (name) VALUES ('CANDIDATE'), ('RECRUITER'), ('ADMIN');