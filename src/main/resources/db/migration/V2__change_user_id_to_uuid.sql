-- user_id was originally BIGINT. Switched to UUID to avoid exposing a sequential,
-- enumerable identifier as the shared cross-module user identity (auth <-> users),
-- and to keep that identity independent of any single table's ID generator.
--
-- Tables are empty at this point in the project (no real data yet), so a straight
-- drop-and-recreate of the column is safe. This would be a data-migrating ALTER
-- (backfill a new column, cut over, drop the old one) if there were existing rows.

ALTER TABLE auth_credentials
    DROP COLUMN user_id,
    ADD COLUMN user_id UUID NOT NULL UNIQUE;

ALTER TABLE auth_user_roles
    DROP COLUMN user_id,
    ADD COLUMN user_id UUID NOT NULL;

ALTER TABLE auth_refresh_tokens
    DROP COLUMN user_id,
    ADD COLUMN user_id UUID NOT NULL;