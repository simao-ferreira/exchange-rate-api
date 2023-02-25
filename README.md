# Gradle Template App

Simple project template for Gradle projects.

Template was built with intellij IDE in mind.

Contents:

* Gradle
* Groovy
* Kotlin
* Spock
* Spring
* Postgres
* Swagger-Springdoc

## Postgresql Docker

To run the PostgreSQL docker image:

```shell
$ docker-compose up
```

To manually access the db:

```shell
$ psql --host=localhost --port 5432 --dbname=default_db --username=username
```

or

```shell
$ psql -h localhost -p 5432 -d default_db -U username
```

### Warning

For this implementation we use a volume, if settings are changed, delete `pdb-data` folder.
