
-- USERS
CREATE TABLE "users" (
  "id"              UUID      NOT NULL PRIMARY KEY,
  "login"           VARCHAR   NOT NULL,
  "login_lowercase" VARCHAR   NOT NULL,
  "email"           VARCHAR   NOT NULL NOT NULL,
  "password"        VARCHAR   NOT NULL,
  "salt"            VARCHAR   NOT NULL,
  "created_on"      TIMESTAMP NOT NULL
);
ALTER TABLE "users"
  OWNER TO jc_acct;

-- USER ROLES
CREATE TABLE "user_roles" (
  "id"              BIGSERIAL PRIMARY KEY,
  "role"            VARCHAR NOT NULL UNIQUE
);
ALTER TABLE "user_roles"
  OWNER TO jc_acct;


CREATE TABLE "user_role_mappings" (
  "user_id"         UUID  NOT NULL REFERENCES "users"(id),
  "role_id"         INTEGER NOT NULL REFERENCES "user_roles"(id)
);
ALTER TABLE "user_role_mappings"
  OWNER TO jc_acct;

-- PASSWORD RESET CODES
CREATE TABLE "password_reset_codes" (
  "id"       UUID      NOT NULL PRIMARY KEY,
  "code"     VARCHAR   NOT NULL,
  "user_id"  UUID      NOT NULL REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
  "valid_to" TIMESTAMP NOT NULL
);
ALTER TABLE "password_reset_codes"
  OWNER TO jc_acct;

-- REMEMBER ME TOKENS
CREATE TABLE "remember_me_tokens" (
  "id"         UUID      NOT NULL PRIMARY KEY,
  "selector"   VARCHAR   NOT NULL UNIQUE,
  "token_hash" VARCHAR   NOT NULL,
  "user_id"    UUID      NOT NULL REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
  "valid_to"   TIMESTAMP NOT NULL
);
ALTER TABLE "remember_me_tokens"
  OWNER TO jc_acct;

-- FLIGHT ORDERS
CREATE TABLE "flight_orders" (
  "id"                BIGSERIAL PRIMARY KEY,
  "created_on"        TIMESTAMP NOT NULL,
  "confirmed_on"      TIMESTAMP DEFAULT NULL,
  "rejected_on"       TIMESTAMP DEFAULT NULL
);
CREATE INDEX "request_user_id_idx"
  ON "flight_orders" ("request_user_id");
CREATE INDEX "accept_user_id_idx"
  ON "flight_orders" ("accept_user_id");
ALTER TABLE "flight_orders"
  OWNER TO jc_acct;


CREATE TABLE "locations" (
  "id"      BIGSERIAL  PRIMARY KEY,
  "code"    VARCHAR          NOT NULL UNIQUE,
  "name"    VARCHAR          NOT NULL,
  "geo_lat" DOUBLE PRECISION NOT NULL,
  "geo_lon" DOUBLE PRECISION NOT NULL
);
CREATE INDEX "location_name_idx"
  ON "locations" ("name");
ALTER TABLE "locations"
    OWNER TO jc_acct;

CREATE TABLE "planes" (
  "id"                BIGSERIAL  PRIMARY KEY,
  "n_no"              VARCHAR                        NOT NULL UNIQUE,
  "manufacturer_name" VARCHAR                        NOT NULL,
  "serial_no"         VARCHAR                        NOT NULL UNIQUE,
  "model"             VARCHAR                        NOT NULL,
  "owner_id"          UUID REFERENCES "users" ("id") NOT NULL,
  "pilot_seats"       INT                            NOT NULL,
  "min_pilot"         INT                            NOT NULL,
  "customer_seats"    INT                            NOT NULL
);
CREATE INDEX "planes_manufacturer_name_idx"
  ON "planes" ("manufacturer_name");
ALTER TABLE "planes"
  OWNER TO jc_acct;

-- FLIGHT PLANS
CREATE TABLE "flight_plans" (
  "id"              BIGSERIAL  PRIMARY KEY,
  "order_id"        BIGINT    REFERENCES flight_orders(id) DEFAULT NULL,
  "provide_user_id"   UUID    NOT NULL REFERENCES users(id),
  "consume_user_id"   UUID    NOT NULL REFERENCES users(id),
  "initiate_user_id"  UUID    NOT NULL REFERENCES users(id),
  "passenger_count" INTEGER NOT NULL,
  "start_time"      TIMESTAMP,
  "end_time"        TIMESTAMP,
  "created_on"      TIMESTAMP NOT NULL,
  "modified_on"     TIMESTAMP NOT NULL
);
ALTER TABLE "flight_plans"
  OWNER TO jc_acct;

-- FLIGHT STEPS
CREATE TABLE "flight_steps" (
  "id"                BIGSERIAL PRIMARY KEY,
  "plan_id"           INTEGER NOT NULL REFERENCES flight_plans(id)
                      ON DELETE CASCADE ON UPDATE CASCADE,
  "from_location_id"  INTEGER NOT NULL REFERENCES locations(id)
                      ON DELETE CASCADE ON UPDATE CASCADE,
  "to_location_id"    INTEGER NOT NULL REFERENCES locations(id)
                      ON DELETE CASCADE ON UPDATE CASCADE,
  "from_time"         TIMESTAMP NOT NULL,
  "to_time"           TIMESTAMP NOT NULL
);
ALTER TABLE "flight_steps"
  OWNER TO jc_acct;
