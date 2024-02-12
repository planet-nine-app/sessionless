Often times a platform will need to integrate with third parties to provide some service or another for their users. Sometimes this is done by servers just talking with each other, but sometimes this requires a user to communicate with the third party for some resource. In this latter instance there is the problem of how to grant the user credentials to the third party since a) the third party is untrusted, and b) the user's credentials are stored in the first party server. 

To answer this problem, the OAuth 2.0 protocol was developed, which you can [read here](https://datatracker.ietf.org/doc/html/rfc6749). Here's what the solution looks like:

  +--------+                                           +---------------+
  |        |--(A)------- Authorization Grant --------->|               |
  |        |                                           |               |
  |        |<-(B)----------- Access Token -------------|               |
  |        |               & Refresh Token             |               |
  |        |                                           |               |
  |        |                            +----------+   |               |
  |        |--(C)---- Access Token ---->|          |   |               |
  |        |                            |          |   |               |
  |        |<-(D)- Protected Resource --| Resource |   | Authorization |
  | Client |                            |  Server  |   |     Server    |
  |        |--(E)---- Access Token ---->|          |   |               |
  |        |                            |          |   |               |
  |        |<-(F)- Invalid Token Error -|          |   |               |
  |        |                            +----------+   |               |
  |        |                                           |               |
  |        |--(G)----------- Refresh Token ----------->|               |
  |        |                                           |               |
  |        |<-(H)----------- Access Token -------------|               |
  +--------+           & Optional Refresh Token        +---------------+

All those grants and tokens are just different digital representations of a user credentials, and they're necessary because nobody other than the client and the authorization server trust each other.

With Sessionless, however, the client can send an authenticated request directly to a resource server who can in turn pass that to the authorization server, which can then authorize the request. The picture looks like this:

  +--------+                                                          +---------------+
  |        |                            +----------+                  |               |
  |        |--(A)---- Signed Request--->|          |-(B)-Request Fwd->|               |
  |        |                            |          |                  |               |
  |        |<-(D)- Protected Resource --| Resource |<Request Authed(C)| Authorization |
  | Client |                            |  Server  |                  |     Server    |
  |        |                            |(Secondary|                  |   (Primary)   |
  |        |                            |          |                  |               |
  |        |                            |          |                  |               |
  |        |                            +----------+                  |               |
  +--------+                                                          +---------------+

Not only is this simpler, and more direct, but you get it basically for free using Sessionless. 

But it doesn't stop there. Sessionless allows user to associate third party public keys with their public key, which enables the third party to become a secondary user on their behalf. Here's the OAuth 2.0 spec's list of reasons for why this is impractical:

   o  Third-party applications are required to store the resource
      owner's credentials for future use, typically a password in
      clear-text.

   o  Servers are required to support password authentication, despite
      the security weaknesses inherent in passwords.

   o  Third-party applications gain overly broad access to the resource
      owner's protected resources, leaving resource owners without any
      ability to restrict duration or access to a limited subset of
      resources.

   o  Resource owners cannot revoke access to an individual third party
      without revoking access to all third parties, and must do so by
      changing the third party's password.

   o  Compromise of any third-party application results in compromise of
      the end-user's password and all of the data protected by that
      password.

  As you can see pretty much everything revolves around the problems inherent in sharing user credentials and sessions with an untrusted third party. With Sessionless there are no shared user credentials and no sessions, and the access granted to the third party is revokable. 
