CREATE UNIQUE INDEX "USERS_login_lowercase_uk" ON "USERS"("login_lowercase");
CREATE UNIQUE INDEX "USERS_email_uk" ON "USERS"("email");

CREATE UNIQUE INDEX "USER_ROLE_MAPPING_user_id_and_role_id_uk" ON "USER_ROLE_MAPPINGS"("user_id", "role_id");

CREATE INDEX "PLANES_manufacturer_name_idx" ON "PLANES" ("manufacturer_name");
CREATE INDEX "LOCATIONS_name_idx" ON "LOCATIONS" ("name");

CREATE UNIQUE INDEX "CONSUMER_ASKS_plan_id_and_consumer_id_uk" ON "CONSUMER_ASKS"("plan_id", "consumer_id");
CREATE UNIQUE INDEX "PROVIDER_ASKS_plan_id_and_consumer_id_uk" ON "PROVIDER_ASKS"("plan_id", "provider_id");

CREATE UNIQUE INDEX "CONSUMER_BIDS_provider_ask_id_and_bidder_id_uk" ON "CONSUMER_BIDS"("provider_ask_id", "bidder_id");
CREATE UNIQUE INDEX "PROVIDER_BIDS_consumer_ask_id_and_bidder_id_uk" ON "PROVIDER_BIDS"("consumer_ask_id", "bidder_id");

CREATE UNIQUE INDEX "PILOTS_license_num_uk" ON "PILOTS"("license_num");