-- USER ROLES
CREATE TABLE "USER_ROLES" (
  "id"              INT4 PRIMARY KEY,
  "role"            VARCHAR NOT NULL UNIQUE
);
ALTER TABLE "USER_ROLES"
  OWNER TO jc_acct;

-- USER STATUS
CREATE TABLE "USER_STATUSES" (
  "id"              INT4 PRIMARY KEY,
  "status"          VARCHAR NOT NULL UNIQUE
);
ALTER TABLE "USER_STATUSES"
    OWNER TO jc_acct;

-- USERS
CREATE TABLE "USERS" (
  "id"              UUID      NOT NULL PRIMARY KEY,
  "login"           VARCHAR   NOT NULL,
  "login_lowercase" VARCHAR   NOT NULL,
  "email"           VARCHAR   NOT NULL NOT NULL,
  "password"        VARCHAR   NOT NULL,
  "salt"            VARCHAR   NOT NULL,
  "user_status_id"  INT4      NOT NULL REFERENCES "USER_STATUSES"(id),
  "created_on"      TIMESTAMP NOT NULL
);
ALTER TABLE "USERS"
  OWNER TO jc_acct;

CREATE TABLE "USER_ROLE_MAPPINGS" (
  "user_id"         UUID      NOT NULL REFERENCES "USERS"(id) ON DELETE CASCADE ON UPDATE CASCADE,
  "role_id"         INT4      NOT NULL REFERENCES "USER_ROLES"(id)
);
ALTER TABLE "USER_ROLE_MAPPINGS"
  OWNER TO jc_acct;

-- PASSWORD RESET CODES
CREATE TABLE "PASSWORD_RESET_CODES" (
  "id"       UUID      NOT NULL PRIMARY KEY,
  "code"     VARCHAR   NOT NULL,
  "user_id"  UUID      NOT NULL REFERENCES "USERS"(id) ON DELETE CASCADE ON UPDATE CASCADE,
  "valid_to" TIMESTAMP NOT NULL
);
ALTER TABLE "PASSWORD_RESET_CODES"
  OWNER TO jc_acct;

-- REMEMBER ME TOKENS
CREATE TABLE "REMEMBER_ME_TOKENS" (
  "id"         UUID      NOT NULL PRIMARY KEY,
  "selector"   VARCHAR   NOT NULL UNIQUE,
  "token_hash" VARCHAR   NOT NULL,
  "user_id"    UUID      NOT NULL REFERENCES "USERS"(id) ON DELETE CASCADE ON UPDATE CASCADE,
  "valid_to"   TIMESTAMP NOT NULL
);
ALTER TABLE "REMEMBER_ME_TOKENS"
  OWNER TO jc_acct;

-- Extra information about user with pilot role
CREATE TABLE "PILOTS" (
  "id"      SERIAL4 PRIMARY KEY,
  "user_id" UUID NOT NULL REFERENCES "USERS"(id),
  "license_num" VARCHAR NOT NULL
);
ALTER TABLE "PILOTS"
    OWNER TO jc_acct;

-- Extra information about user with plane provider role
CREATE TABLE "PLANE_PROVIDERS" (
  "id"      SERIAL4 PRIMARY KEY,
  "user_id" UUID NOT NULL REFERENCES "USERS"(id),
  "lincence_num" VARCHAR NOT NULL
);
ALTER TABLE "PLANE_PROVIDERS"
    OWNER TO jc_acct;

CREATE TABLE "PLANES" (
  "id"                SERIAL4  PRIMARY KEY,
  "n_no"              VARCHAR                        NOT NULL UNIQUE,
  "manufacturer_name" VARCHAR                        NOT NULL,
  "serial_no"         VARCHAR                        NOT NULL UNIQUE,
  "model"             VARCHAR                        NOT NULL,
  "owner_id"          UUID REFERENCES "USERS" ("id") NOT NULL,
  "pilot_seats"       INT                            NOT NULL,
  "min_pilot"         INT                            NOT NULL,
  "customer_seats"    INT                            NOT NULL
);
ALTER TABLE "PLANES"
  OWNER TO jc_acct;

-- A plane provider may provide multiple planes
CREATE TABLE "PLANE_PROVIDER_MAPPINGS" (
  "provider_id" INTEGER NOT NULL REFERENCES "PLANE_PROVIDERS"(id) ON DELETE CASCADE ON UPDATE CASCADE,
  "plane_id"    INTEGER NOT NULL REFERENCES "PLANES" (id)
);
ALTER TABLE "PLANE_PROVIDER_MAPPINGS"
    OWNER TO jc_acct;

CREATE TABLE "LOCATIONS" (
  "id"      SERIAL4  PRIMARY KEY,
  "code"    VARCHAR          NOT NULL UNIQUE,
  "name"    VARCHAR          NOT NULL,
  "geo_lat" DOUBLE PRECISION NOT NULL,
  "geo_lon" DOUBLE PRECISION NOT NULL
);
ALTER TABLE "LOCATIONS"
    OWNER TO jc_acct;

-- FLIGHT PLANS
CREATE TABLE "FLIGHT_PLANS" (
  "id"              SERIAL8  PRIMARY KEY,
  "passenger_num"   INT4,
  "start_time"      TIMESTAMP,
  "end_time"        TIMESTAMP,
  "created_on"      TIMESTAMP NOT NULL,
  "modified_on"     TIMESTAMP NOT NULL
);
ALTER TABLE "FLIGHT_PLANS"
  OWNER TO jc_acct;

-- FLIGHT STEPS
CREATE TABLE "FLIGHT_STEPS" (
  "id"                SERIAL8 PRIMARY KEY,
  "plan_id"           INT8 NOT NULL REFERENCES "FLIGHT_PLANS" (id)
                      ON DELETE CASCADE ON UPDATE CASCADE,
  "from_location_id"  INT4 NOT NULL REFERENCES "LOCATIONS" (id)
                      ON DELETE CASCADE ON UPDATE CASCADE,
  "to_location_id"    INT4 NOT NULL REFERENCES "LOCATIONS" (id)
                      ON DELETE CASCADE ON UPDATE CASCADE,
  "from_time"         TIMESTAMP NOT NULL,
  "to_time"           TIMESTAMP NOT NULL
);
ALTER TABLE "FLIGHT_STEPS"
  OWNER TO jc_acct;

-- Ask from Consumer
CREATE TABLE "CONSUMER_ASKS" (
  "id"            SERIAL8 PRIMARY KEY,
  "plan_id"       INT8 REFERENCES "FLIGHT_PLANS" (id),
  "consumer_id"   UUID NOT NULL REFERENCES "USERS" (id),
  "passengers"    INT8 NOT NULL,
  "active"        BOOLEAN DEFAULT TRUE,
  "modified_on"   TIMESTAMP NOT NULL
);
ALTER TABLE "CONSUMER_ASKS"
    OWNER TO jc_acct;

-- Ask from Provider
CREATE TABLE "PROVIDER_ASKS" (
  "id"            SERIAL8 PRIMARY KEY,
  "plan_id"       INT8 REFERENCES "FLIGHT_PLANS" (id),
  "provider_id"   UUID NOT NULL REFERENCES "USERS" (id),
  "seats"         INT NOT NULL,
  "price"         FLOAT NOT NULL,
  "active"        BOOLEAN DEFAULT TRUE,
  "created_on"    TIMESTAMP NOT NULL,
  "modified_on"   TIMESTAMP NOT NULL
);
ALTER TABLE "PROVIDER_ASKS"
    OWNER TO jc_acct;

-- Bid for provider ask
CREATE TABLE "CONSUMER_BIDS" (
  "id"              SERIAL8 PRIMARY KEY,
  "provider_ask_id" INT8 NOT NULL REFERENCES "PROVIDER_ASKS" (id),
  "bidder_id"       UUID NOT NULL REFERENCES "USERS" (id),
  "passengers"      INT8 NOT NULL,
  "active"          BOOLEAN DEFAULT TRUE,
  "confirmed"       BOOLEAN NOT NULL DEFAULT FALSE,
  "charged"         BOOLEAN NOT NULL DEFAULT FALSE,
  "refunded"        BOOLEAN NOT NULL DEFAULT FALSE,
  "created_on"      TIMESTAMP NOT NULL,
  "modified_on"     TIMESTAMP NOT NULL
);
ALTER TABLE "CONSUMER_BIDS"
    OWNER TO jc_acct;

-- Bid for consumer ask
CREATE TABLE "PROVIDER_BIDS" (
  "id"              SERIAL8 PRIMARY KEY,
  "consumer_ask_id" INT8 NOT NULL REFERENCES "CONSUMER_ASKS" (id),
  "bidder_id"       UUID NOT NULL REFERENCES "USERS" (id),
  "active"          BOOLEAN DEFAULT TRUE,
  "confirmed"       BOOLEAN DEFAULT FALSE,
  "charged"         BOOLEAN NOT NULL DEFAULT FALSE,
  "refunded"        BOOLEAN NOT NULL DEFAULT FALSE,
  "created_on"      TIMESTAMP NOT NULL,
  "modified_on"     TIMESTAMP NOT NULL
);
ALTER TABLE "PROVIDER_BIDS"
    OWNER TO jc_acct;

CREATE TABLE "PROVIDE_ORDERS" (
  "provider_ask_id" INT8 NOT NULL REFERENCES "PROVIDER_ASKS" (id),
  "consumer_bid_id" INT8 NOT NULL REFERENCES "CONSUMER_BIDS" (id),
  "confirmed"       BOOLEAN DEFAULT FALSE,
  "created_on"      TIMESTAMP NOT NULL,
  "modified_on"     TIMESTAMP NOT NULL
);
ALTER TABLE "PROVIDE_ORDERS"
    OWNER TO jc_acct;

CREATE TABLE "CONSUME_ORDERS" (
  "consumer_ask_id" INT8 NOT NULL REFERENCES "CONSUMER_ASKS" (id),
  "provider_bid_id" INT8 NOT NULL REFERENCES "PROVIDER_BIDS" (id),
  "confirmed"       BOOLEAN DEFAULT FALSE,
  "created_on"      TIMESTAMP NOT NULL,
  "modified_on"     TIMESTAMP NOT NULL
);
ALTER TABLE "CONSUME_ORDERS"
  OWNER TO jc_acct;