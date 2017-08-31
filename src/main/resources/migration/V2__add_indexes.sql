CREATE UNIQUE INDEX "users_login_lowercase" ON "users"("login_lowercase");
CREATE UNIQUE INDEX "users_email" ON "users"("email");

CREATE UNIQUE INDEX "consumer_ask_plan_id_and_consumer_id" ON consumer_ask("plan_id", "consumer_id");
CREATE UNIQUE INDEX "provider_ask_plan_id_and_consumer_id" ON provider_ask("plan_id", "provider_id");

CREATE UNIQUE INDEX "uk_consumer_bid_provider_ask_id_and_bidder_id" ON consumer_bid("provider_ask_id", "bidder_id");
CREATE UNIQUE INDEX "uk_provider_bid_consumer_ask_id_and_bidder_id" ON provider_bid("consumer_ask_id", "bidder_id");