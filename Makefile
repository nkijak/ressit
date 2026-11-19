database?=ressit
dbuser?=postgres
dbpass?=password
dbhost?=localhost:5432

dbconnect=jdbc:postgresql://${dbhost}/${database}?user=${dbuser}&password=${dbpass}

migrate: docker-start create-migration
	@DATABASE_URL="${dbconnect}" \
							 lein migrations migrate

create-migration: docker-start resources/db/models.edn
	@DATABASE_URL="${dbconnect}" \
							lein migrations make

docker-start:
	docker compose up -d

dev: docker-start
	RESSIT_DB_PASSWORD=${dbpass} \
										 lein ring server-headless

repl:
	RESSIT_DB_PASSWORD=${dbpass} \
										 lein repl
