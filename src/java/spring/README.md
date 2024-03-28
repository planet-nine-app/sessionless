# Sessionless Spring Server Implementation
This project a deployable spring server for the sessionless protocol

---

## Endpoints
### POST

---

### api/register
###  Parameters:
Content-Type: application/json
```
{ 
    "publicKey": "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAE1MtHIxlGP5TARqBccrddNm1FnYH1Fp+o
    nETz5KbXPSeG5FGwKMUXGfAmSZJq2gENULFewwymt+9bTXkjBZhh8A=="
}
```
### Response:
HTTP/1.1 201 Created

Content-Type: application/json
```
{
    "userId": "1db684ea-76bd-467e-afc6-ec06322c1f82"
}
```
HTTP/1.1 400 bad request

Content-Type: application/json
```
{
    "status: 400,
    "message": "Invalid request parameters provided",
    "errorDetails": [
        "invalid key format"
    ]
}
```

---

### api/verify
### Parameters:
Content-Type: application/json
```
{
    "userId": "1db684ea-76bd-467e-afc6-ec06322c1f82",
    "message": "string",
    "signature": 
        [
            r=0xb83380f6e1d09411ebf49afd1a95c738686bfb2b0fe2391134f4ae3d6d77b78a,
            s=0x6c305afcac930a3ea1721c04d8a1a979016baae011319746323a756fbaee1811
        ]
}
```
### Response:
HTTP/1.1 200 OK

Content-Type: application/json
```
{
    "status": 200,
    "message": "The message was verified successfully"
}
```

HTTP/1.1 400 bad request

Content-Type: application/json
```
{
    "status: 400,
    "message": "Invalid request parameters provided",
    "errorDetails": [
        "userId not found",
        "invalid signature"
    ]
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

With docker & docker-compose installed run the following command from the root project folder: `docker compose up -d`


