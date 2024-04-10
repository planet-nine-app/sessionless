# Web Server Example

This web server example is based on the [JS server example](https://github.com/planet-nine-app/sessionless/blob/main/src/javascript/example/server/server.js).

Requests, responses and underlying features might differ from the original, but the whole logic design is the same.

___

## Endpoint list
- **[ANY]** `/client`
- **[ANY]** `/register`
- **[ANY]** `/cool-stuff`
- **[ANY]** `/associate`
- **[POST, GET]** `/value`
- _­+ resources_

To easily "register" and access "cool-stuff" visit `/client`.

___

## API Docs

### ­> `/register`
payload:
```json
{
  "pub_key": <PUBLIC_KEY>,
  "timestamp": "now",
  "entered_text": "any"
}
```
headers:
- signature - Payload signature.

### ­> `/cool-stuff`
payload:
```json
{
    "timestamp": "now",
    "coolness": "any",
    "uuid": <UUID>,
}
```
headers:
- signature - Payload signature.

### ­> `/associate`
payload:
```json
{
    "message1": <BASE64_ENCODED_MESSAGE>,
    "signature1": <SGINATURE_OF_MESSAGE1>,
    "message2": <BASE64_ENCODED_MESSAGE>,
    "signature2": <SGINATURE_OF_MESSAGE2>,
    "pub_key": <PUBLIC_KEY>,
}
```

### ­> `/value`

**[POST]:** 

payload:
```json
{
    "uuid": <UUID>,
    "timestamp": "now",
    "value": <VALUE>,
}
```
headers:
- signature - Payload signature.

**[GET]:** 

query:
```
?message=<BASE64_ENCODED_MESSAGE>
```
headers:
- signature - Message signature.

___

## Ref

Message structure:
```json
{
    "uuid": <UUID>,
    "timestamp": "now",
}
```
