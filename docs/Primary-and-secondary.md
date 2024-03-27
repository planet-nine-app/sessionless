# Primary and secondary?

Often times a platform will need to integrate with third parties to provide some service or another for their users.
 Sometimes this is done by servers just talking with each other, but sometimes this requires a user to communicate with the third party for some resource.
 In this latter instance there is the problem of how to grant the user credentials to the third party since (a) the third party is untrusted, and (b) the user's credentials are stored in the first party server.
 

To answer this problem, the OAuth 2.
0 protocol was developed, which you can [read here](https://datatracker.
ietf.
org/doc/html/rfc6749).
 Here's what the solution looks like:

```
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
```

All those grants and tokens are just different digital representations of a user credentials, and they're necessary because nobody other than the client and the authorization server trust each other.


With Sessionless, however, the client can send an authenticated request directly to a resource server who can in turn pass that to the authorization server, which can then authorize the request.
 The picture looks like this:

```
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
```

Not only is this simpler, and more direct, but you get it basically for free using Sessionless.
 

But it doesn't stop there.
 Sessionless allows user to associate third party public keys with their public key, which enables the third party to become a secondary user on their behalf.
 Here's the OAuth 2.
0 spec's list of reasons for why this is impractical:

- Third-party applications are required to store the resource owner's credentials for future use, typically a password in clear-text.

- Servers are required to support password authentication, despite the security weaknesses inherent in passwords.

- Third-party applications gain overly broad access to the resource owner's protected resources, leaving resource owners without any ability to restrict duration or access to a limited subset of resources.

- Resource owners cannot revoke access to an individual third party without revoking access to all third parties, and must do so by changing the third party's password.

- Compromise of any third-party application results in compromise of the end-user's password and all of the data protected by that password.


As you can see, pretty much everything revolves around the problems inherent in sharing user credentials and sessions with an untrusted third party.
 With Sessionless there are no shared user credentials and no sessions, and the access granted to the third party is revokable.
 
### Primary and secondary long-term

So I'm going to risk getting a little preachy here. 
I'd like for us, just for a second, to remember what identity was like before the internet. 
If we think about going out and running some errands, say picking up some dry cleaning, picking up a package from the post office, and buying some groceries. 
The dry cleaners, post office, and grocery store need not know who you are, or that those three things are connected. 
Everything operates just fine without any more information being exchanged. 

Then the internet came along, and in order to find anything we needed a way to sort through the thousands of links people were churning out. 
Some clever groups came up with search engines, and one of those groups, Google took off, and found they had to figure out how to pay for the servers that people were hitting all day. 
To do that they built the largest advertising platform to ever exist. 
In doing so they've churned out a series of products designed to capture exactly when and where you do anything online. 
An overly simplified picture of this is the following:

erDiagram
    GOOGLE ||--|| GMAIL : has
    GOOGLE ||--o{ APP1 : SSO
    GMAIL ||--o{ APP1 : login
    GOOGLE ||--o{ ADSENSE : uses
    ADSENSE ||--|{ WEBSITE1 : monetizes
    GOOGLE ||--|{ WEBSITE1 : tracks
    AUTH2 ||--|{ APP2 : login
    GOOGLEANALYTICS ||--o{ APP2 : fingerprints
    GOOGLE ||--|{ GOOGLEANALYTICS : tracking
    IDENTITY ||--|{ GOOGLE : monetized

Back in 2001 we all signed up for gmails, used them to login to every new thing to come along for two decades, and now when I talk about my son having a cold, children's tylenol ads pop up. 

As a response to this, a lot has been spun up around so-called distributed identities, or dids. The picture for distributed identities looks like this:

erDiagram
    GOOGLE ||--|| GMAIL : has
    DID ||--o{ APP1 : login
    GMAIL ||--o{ DID : gmail
    GOOGLE ||--o{ ADSENSE : uses
    ADSENSE ||--|{ WEBSITE1 : monetizes
    DID ||--|{ WEBSITE1 : login
    DID ||--|{ APP2 : login
    GOOGLEANALYTICS ||--o{ APP2 : fingerprints
    GOOGLE ||--|{ GOOGLEANALYTICS : tracking
    IDENTITY ||--|{ DID : monetized

Here it's a little better, but at the end of the day, Google doesn't see any difference between you being johndoe@gmail.com or RANDOMHEXSTRING@distributed.server. 

But here's the thing all of these distributed efforts are missing: identity is contextual _not_ canonical. 
At Starbucks I had my own drink I made up, and we called it a green lantern, so I was the green lantern guy. 
A bunch of people in Chicago used to call me Lazer. 
I don't want to lose that personalization when it's an enhancement to my experience, and more importantly for anyone designing software, there's no chance in hell that any business is going to give that up just over some privacy. 
I firmly believe that the correct approach to online identity is one that allows for privacy, while also allowing for personalization of online experiences.

Sessionless allows for this because rather than having a single canonical identity, which is compromised as soon as anyone in the web knows who you are, your primary and secondary identities are all separate from each other. 
Primaries are there to associate keys, and allow for account recovery, but there need not be just one of them. My hope is that the protocol grows to where there are several so that they can compete and keep each other honest. 
But most importantly each primary and secondary private key is used in a single bounded context, i.e. one app and/or website. 
All that's shared between those contexts are anonymized uuids and public keys. 
Here's what the Sessionless picture looks like:

erDiagram
    GOOGLE ||--|| GMAIL : has
    GMAIL ||--|| DID : gmail
    GOOGLE ||--|| ADSENSE : uses
    ADSENSE ||--|| WEBSITE1 : monetizes
    WEBSITE1 ||--|| PRIMARY : login
    APP1 ||--|| SECONDARY1 : login
    APP2 ||--|| SECONDARY2 : login
    GOOGLEANALYTICS ||--|| APP2 : fingerprints
    GOOGLE ||--|| GOOGLEANALYTICS : tracking
    SECONDARY1 ||--|| IDENTITY1 : anonymized
    SECONDARY2 ||--|| IDENTITY2 : anonymized
    PRIMARY ||--|| IDENTITY3 : anonymized
    PRIMARY ||--|| SECONDARY1 : associated
    PRIMARY ||--|| SECONDARY2 : associated

Now we've got identity on the right side of the diagram. Next we build up our defenses against the adver(ti)sement. But one thing at a time.
