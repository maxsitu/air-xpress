-- USERS
CREATE TABLE "users"(
    "id"          UUID NOT NULL,
    "login"       VARCHAR NOT NULL,
    "login_lowercase" VARCHAR NOT NULL,
    "email"       VARCHAR NOT NULL NOT NULL,
    "password"    VARCHAR NOT NULL,
    "salt"        VARCHAR NOT NULL,
    "created_on"  TIMESTAMP NOT NULL
);
ALTER TABLE "users" ADD CONSTRAINT "users_id" PRIMARY KEY("id");

-- PASSWORD RESET CODES
CREATE TABLE "password_reset_codes"(
  "id"        UUID NOT NULL,
  "code"      VARCHAR NOT NULL,
  "user_id"   UUID NOT NULL,
  "valid_to"  TIMESTAMP NOT NULL
);
ALTER TABLE "password_reset_codes" ADD CONSTRAINT "password_reset_codes_id" PRIMARY KEY("id");
ALTER TABLE "password_reset_codes" ADD CONSTRAINT "password_reset_codes_user_fk"
  FOREIGN KEY("user_id") REFERENCES "users"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- REMEMBER ME TOKENS
CREATE TABLE "remember_me_tokens"(
  "id"          UUID NOT NULL,
  "selector"    VARCHAR NOT NULL,
  "token_hash"  VARCHAR NOT NULL,
  "user_id"     UUID NOT NULL,
  "valid_to"    TIMESTAMP NOT NULL
);
ALTER TABLE "remember_me_tokens" ADD CONSTRAINT "remember_me_tokens_id" PRIMARY KEY("id");
ALTER TABLE "remember_me_tokens" ADD CONSTRAINT "remember_me_tokens_user_fk"
  FOREIGN KEY("user_id") REFERENCES "users"("id") ON DELETE CASCADE ON UPDATE CASCADE;
CREATE UNIQUE INDEX "remember_me_tokens_selector" ON "remember_me_tokens"("selector");

CREATE TABLE "orders" (
  "id"    SERIAL,
  "request_user_id" UUID NOT NULL,
  "accept_user_id"  UUID NOT NULL,
  "event_starts_on" TIMESTAMP NOT NULL,
  "event_ends_on"   TIMESTAMP NOT NULL,
  "from_location_id"  INTEGER NOT NULL,
  "to_location_id"  INTEGER NOT NULL,
  "plan_id"         INTEGER,
  "created_on"      TIMESTAMP NOT NULL
);

ALTER TABLE "orders" ADD CONSTRAINT "orders_id" PRIMARY KEY ("id");
CREATE INDEX "request_user_id_idx" ON "orders"("request_user_id");
CREATE INDEX "accept_user_id_idx" ON "orders"("accept_user_id");


CREATE TABLE "locations" (
  "id"    SERIAL,
  "code"  VARCHAR NOT NULL,
  "name"  VARCHAR NOT NULL,
  "geo_lat"  DOUBLE PRECISION NOT NULL,
  "geo_lon"  DOUBLE PRECISION NOT NULL
);
ALTER TABLE "locations" ADD CONSTRAINT "locations_id" PRIMARY KEY ("id");
CREATE UNIQUE INDEX "location_code_idx" ON "locations"("code");
CREATE INDEX "location_name_idx" ON "locations"("name");

CREATE TABLE "planes" (
  "id"                SERIAL,
  "n_no"              VARCHAR NOT NULL,
  "manufacturer_name" VARCHAR NOT NULL,
  "serial_no"         VARCHAR NOT NULL,
  "model"             VARCHAR NOT NULL,
  "owner_id"          UUID REFERENCES "users" ("id") NOT NULL,
  "pilot_seats"       INT     NOT NULL,
  "min_pilot"         INT     NOT NULL,
  "customer_seats"    INT     NOT NULL
);
ALTER TABLE "planes" ADD CONSTRAINT "planes_id" PRIMARY KEY ("id");
CREATE UNIQUE INDEX "planes_n_no_idx" ON "planes" ("n_no");
CREATE UNIQUE INDEX "planes_serial_idx" ON "planes" ("serial_no");
CREATE INDEX "planes_manufacturer_name_idx" ON "planes" ("manufacturer_name");
