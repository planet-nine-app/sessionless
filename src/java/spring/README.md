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
    "publicKey": "03173db82e5b709bc0dde804f717c05a3e94d83f4e024d6360ca8ce396f38a7de7"
}
```
### Response:
HTTP/1.1 202 Accepted

Content-Type: application/json
```
{
    "userUuid": "1db684ea-76bd-467e-afc6-ec06322c1f82"
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

### /do-cool-stuff
### Parameters:
Content-Type: application/json
```
{
    "userId": "1db684ea-76bd-467e-afc6-ec06322c1f82",
    "content": "My message",
    "signature": 
        [
            "7aa8e3512ea528bec690dbddb118425f9f1997bf87f87ada6b96cffe11730f03",
            "226397edd371fd7e8398651c956552f11c1d710a3988e42c02eb24b413a6f8f4"
        ]
}
```
### Response:
HTTP/1.1 202 Accepted

Content-Type: application/json
```
{
    "The message content was verified successfully"
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


