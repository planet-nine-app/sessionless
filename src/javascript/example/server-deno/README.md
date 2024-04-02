### Overview

This server runs on the Deno runtime.
If you're unfamiliar with Deno, its key features are native typescript support, built in fetch server, built in key/value data store (in beta), and free dev deployment to serverless containers.
This makes it ideal for rapidly prototyping backends. Since it also supports all npm packages, it's got a lot going for it. So I've chosen it as the runtime for our hosted example.

This server is hosted at https://rare-robin-97.deno.dev.
It has the following endpoints:

<details>
 <summary><code>POST | PUT</code> <code><b>/register</b></code> <code>Registers a new user</code></summary>

##### Parameters

> | name         |  required     | data type               | description                                                           |
> |--------------|-----------|-------------------------|-----------------------------------------------------------------------|
> | publicKey    |  true     | string (hex)            | the publicKey of the user's keypair  |
> | enteredText  |  true     | string                  | example of user supplied data sent to the server  |
> | timestamp    |  true     | string                  | in a production system timestamps prevent replay attacks  |
> | signature    |  true     | string (signature)      | the stringified signature from sessionless for the message  |


##### Responses

> | http code     | content-type                      | response                                                            |
> |---------------|-----------------------------------|---------------------------------------------------------------------|
> | `200`         | `application/json`                | `{"userUUID": <uuid>, "welcomeMessage": "Welcome to Sessionless"}`   |
> | `400`         | `application/json`                | `{"code":"400","message":"Bad Request"}`                            |

##### Example cURL

> ```javascript
>  curl -X POST -H "Content-Type: application/json" -d '{"publicKey": "key", "enteredText": "foo", "timestamp": "now", "signature": "sig"}' https://rare-robin-97.deno.dev/register
> ```

</details>

<details>
 <summary><code>POST | PUT</code> <code><b>/cool-stuff</b></code> <code>Sends back a cool message.</code></summary>

##### Parameters

> | name         |  required     | data type               | description                                                           |
> |--------------|-----------|-------------------------|-----------------------------------------------------------------------|
> | userUUID     |  true     | string (uuid)           | the user's uuid  |
> | coolness     |  true     | string                  | the server needs to know how cool you are |
> | timestamp    |  true     | string                  | in a production system timestamps prevent replay attacks  |
> | signature    |  true     | string (signature)      | the stringified signature from sessionless for the message  |


##### Responses

> | http code     | content-type                      | response                                                            |
> |---------------|-----------------------------------|---------------------------------------------------------------------|
> | `200`         | `application/json`                | `{"coolness": "doubleCool"}`   |
> | `400`         | `application/json`                | `{"code":"400","message":"Bad Request"}`                            |

##### Example cURL

> ```javascript
>  curl -X POST -H "Content-Type: application/json" -d '{"publicKey": "key", "enteredText": "foo", "timestamp": "now", "signature": "sig"}' https://rare-robin-97.deno.dev/cool-stuff
> ```

</details>

