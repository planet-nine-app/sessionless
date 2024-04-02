# Sessionless Nest.JS Implementation

This is a simple implementation of sessionless in a Nest.JS server.

---

## Endpoints

### POST

---

### api/register

### Parameters:

Content-Type: application/json

```
{
  "publicKey": "609a015a18105c226ef80ed728e3439baf8acdaee7b720307a7eb9ac80f2fe5102239dd9feab7fa7fc81f76d85c86d2c8d3c683d9dcffb0ca393be62041fac839a296cffee915606d42f316ac3c56c83",
  "content": "This is content.",
  "timestamp": "2024-04-01T12:00:00Z",
  "signature": {
    "r": "304402202675248de9d52a8c86850bc906b8dfc4ad7308a520b1c120231f1124bb6b5b4d",
    "s": "2206106e3734e4383af67fc8da8703e3f82a3a6d15ef2e8a883845b60a83f3a1d2"
  }
}

```

### Response:

HTTP/1.1 201 Created

Content-Type: application/json

```
{
    "uuid": "1db684ea-76bd-467e-afc6-ec06322c1f82",
    "welcomeMessage": "Welcome to this example!"
}
```

HTTP/1.1 400 Bad Request

Content-Type: application/json

```
{
    "Error":"Something went wrong. Please try again later."
}
```

---

### api/message/verify

### Parameters:

Content-Type: application/json

```
{
    "uuid": "1db684ea-76bd-467e-afc6-ec06322c1f82",
    "content": "This is content.",
    "timestamp": "2024-04-01T12:00:00Z",
    "signature": {
        "r": "304402202675248de9d52a8c86850bc906b8dfc4ad7308a520b1c120231f1124bb6b5b4d",
        "s": "2206106e3734e4383af67fc8da8703e3f82a3a6d15ef2e8a883845b60a83f3a1d2"
    }
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

