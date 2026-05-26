.PHONY: dev db-up db-down db-reset test

# Load .env if present
ifneq (,$(wildcard ./.env))
  include .env
  export
endif

## Start DB containers and run the API with the dev profile
dev: db-up
	./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

## Start MySQL + PhpMyAdmin containers
db-up:
	docker compose up -d
	@echo "MySQL:      localhost:3306"
	@echo "PhpMyAdmin: http://localhost:8090"

## Stop containers (data is preserved in the named volume)
db-down:
	docker compose down

## Destroy data volume and restart with a clean database
## The dev seeder will re-populate on next `make dev`
db-reset:
	docker compose down -v
	docker compose up -d
	@echo "Database reset. Run 'make dev' to reseed."

## Run the test suite
test:
	./mvnw test
