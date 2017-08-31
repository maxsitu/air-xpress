
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

CREATE TABLE "PILOT" (
  "id"      BIGSERIAL PRIMARY KEY,
  "user_id" UUID NOT NULL REFERENCES users(id),
  "lincence_num" VARCHAR NOT NULL
);

CREATE TABLE "PLANE_PROVIDER" (
  "id"      BIGSERIAL PRIMARY KEY,
  "user_id" UUID NOT NULL REFERENCES users(id),
  "name"    VARCHAR NOT NULL,
  "lincence_num" VARCHAR NOT NULL
);

CREATE TABLE "PLANE_PROVIDER_MAPPING" (
  "provider_id" INTEGER NOT NULL,
  "plane_id"    INTEGER NOT NULL REFERENCES planes(id)
);


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
  "owner_id"        UUID     REFERENCES users(id),
  "description"     VARCHAR,
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

-- Ask from Consumer
CREATE TABLE consumer_ask (
  "id"            BIGSERIAL PRIMARY KEY,
  "plan_id"       INTEGER REFERENCES flight_plans(id),
  "consumer_id"   UUID NOT NULL REFERENCES users(id),
  "passengers"    INT NOT NULL,
  "active"        BOOLEAN DEFAULT TRUE,
  "asked_on"      TIMESTAMP NOT NULL,
  "modified_on"   TIMESTAMP NOT NULL
);
ALTER TABLE "consumer_ask"
    OWNER TO jc_acct;

-- Ask from Provider
CREATE TABLE provider_ask (
  "id"            BIGSERIAL PRIMARY KEY,
  "plan_id"       INTEGER REFERENCES flight_plans(id),
  "provider_id"   UUID NOT NULL REFERENCES users(id),
  "seats"         INT NOT NULL,
  "price"         FLOAT NOT NULL,
  "active"        BOOLEAN DEFAULT TRUE,
  "created_on"    TIMESTAMP NOT NULL,
  "modified_on"   TIMESTAMP NOT NULL
);
ALTER TABLE "provider_ask"
    OWNER TO jc_acct;

-- Bid for provider ask
CREATE TABLE consumer_bid (
  "provider_ask_id" INTEGER NOT NULL REFERENCES provider_ask(id),
  "bidder_id"       UUID NOT NULL REFERENCES users(id),
  "active"          BOOLEAN DEFAULT TRUE,
  "confirmed"       BOOLEAN DEFAULT FALSE,
  "created_on"      TIMESTAMP NOT NULL,
  "modified_on"     TIMESTAMP NOT NULL
);
ALTER TABLE consumer_bid
    OWNER TO jc_acct;

-- Bid for consumer ask
CREATE TABLE provider_bid (
  "consumer_ask_id" INTEGER NOT NULL REFERENCES provider_ask(id),
  "bidder_id"       UUID NOT NULL REFERENCES users(id),
  "active"          BOOLEAN DEFAULT TRUE,
  "confirmed"       BOOLEAN DEFAULT FALSE,
  "created_on"      TIMESTAMP NOT NULL,
  "modified_on"     TIMESTAMP NOT NULL
);
ALTER TABLE provider_ask
    OWNER TO jc_acct;