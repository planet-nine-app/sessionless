# Sessionless Spring Server Implementation
This project is a deployable spring server for the sessionless protocol

---

## Endpoints
### POST

---

### /register
###  Parameters:
Content-Type: application/json
```
{ 
    "pubKey": "035748bca3a8aa40fe11edb4d727512931ed14b5ca8688e512e7a866ec0124614b",
    enteredText: "Foo",
    timestamp: 1722220506519
    signature: "21c0317bbc204bc1bd132a688bace99a8e87999ad49c693393f368dce3a666ef50a3acd259d37dcbb653662305a6444f772487abb8539a32318dabe948bf7b9e"
}
```
### Response:
HTTP/1.1 202 Accepted

Content-Type: application/json
```
{
    "uuid": "1db684ea-76bd-467e-afc6-ec06322c1f82"
}
```
HTTP/1.1 400 bad request

Content-Type: application/json
```
{
    "Invalid request parameters provided",
}
```

---

### /cool-stuff
### Parameters:
Content-Type: application/json
```
{
    uuid: "1db684ea-76bd-467e-afc6-ec06322c1f82",
    coolness: "max",
    timestamp: 1722220506519
    signature: "21c0317bbc204bc1bd132a688bace99a8e87999ad49c693393f368dce3a666ef50a3acd259d37dcbb653662305a6444f772487abb8539a32318dabe948bf7b9e"
}
```
### Response:
HTTP/1.1 202 Accepted

Content-Type: application/json
```
{
    doubleCool: "double cool"
}
```

HTTP/1.1 400 bad request

Content-Type: application/json
```
{
    "Invalid request parameters provided",
}
```

---

## Technologies Used
This project is built using the following technologies:

- [Spring Boot](https://spring.io/projects/spring-boot): An open-source Java-based framework used to create stand-alone applications that are easy to deploy.
- [Docker](https://www.docker.com/): An open-source platform that automates the deployment, scaling, and management of applications by containerization.
- [Gradle](https://gradle.org/): An open-source build automation system used to define project configurations for Java.
- [Liquibase](https://www.liquibase.org/): An open-source library for tracking, managing, and applying database schema changes.
- [MyBatis](https://mybatis.org/): A Java-based open-source persistence framework that provides a simplified, minimalistic approach for integrating SQL databases.
- [Postgres](https://www.postgresql.org/): An open-source object-relational database management system (ORDBMS).

---
## Installation
To install this project:
1. Clone repository
2. Modify env/.env_api and .env_db to custom credentials for security
3. With docker & docker-compose installed run the following command from the root project folder: `docker compose up -d`


