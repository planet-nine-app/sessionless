# Sessionless

*Sessionless* is an authentication protocol that obviates the need for a personal identifier like email, and a known secret like a password. 

## Overview

Since the very beginning of computing, there has been a need to represent oneself to a machine, and secure the data and user state associated with oneself.
The only real innovation in this that has happened since the beginning was the introduction of email to replace username as the personal identifier of choice.
This particular method of representing and securing a user, while tried and true, has led to some unfortunate user experiences. 

One reason for this, is that we've conflated three separate user concerns into one convoluted process.
First is account creation, where we ask for a personal identifier and password largely before a user has even touched our product.
Second is account continuity, a useful feature which, because of the difficulty in the next step has turned into perpetual sessions.
Third is account recovery, a constant source of friction and consternation for anyone working the support lines.

Sessionless sets out to change the way we think about steps one and three while enhancing what we can do in step two.

## The problem

We've all been there, you check out a new site, download a new app, install a new game, and the first thing you're met with is entering in an email and password. 
That is a bad user experience, which exists because that site is trying to provide account continuity from the onset. 
But that's a huge source of friction to adoption. 
Sessionless gives you the opportunity to provide account continuity without bombarding the user with forms at the onset.

Only about a third of people use password managers, which means that 65.9% of users will have forgotten their password by the time they have to enter it again. 
If you're a designer reading this, I'm guessing you've designed your fair share of forgot password flows.
With Sessionless we can get more creative with how we get accounts back due to the next section.

## The cooler problem

Sessions can't be shared.
If I login to Amazon, I can't use that login to do anything with Twitter.
Because Sessionless moves the secret part of authorization to clients, servers can share the keys that verify them (the mechanics of this are covered in [README-DEV.md](). 
This means if I login to Sessionless app 1, I can associate my key with Sessionless app 2.
Then I can do Sessionless app 2 things in Sessionless app 1 and vice versa.

This also means we can hop on to any platform that has authentication and an API.
We can login with Slack for example, or Discord, or Mastadon, or Pixelfed, or etc. etc.
Which means we get multi-platform SSO basically for free for account recovery.

## Origin and motivation

Technically, Sessionless is the auth mechanism of Bitcoin and Ethereum where blockchains have been replaced by traditional databases, and a unique user identifier has been added.
I had the idea for this seven or eight years ago while working on transit apps. 
There was an ACM conference, and one of the questions was about digital identity for smart cities.
I wrote [this paper][smart-cities].

In the paper I describe a system whereby a user can move through a city authenticating with different systems with a singular identity.
After that I set about building the first iterations of Sessionless.
As I used it, I began to realize it was way bigger than just smart cities, and deserving of a much broader audience.

## What does success look like?

So if I had my way everyone would stop using email tomorrow, and never share it as part of authentication again. 
Obviously that's not going to happen. 
But these three use cases make sense to try and attack:

* Asking for email account creation before a user really engages with your content is one of the leading causes of bounce rates on web sites. 
With Sessionless you can provide [account continuity] without asking for email up front, leaving the credential entering for after a user engages with your site.
Consider an image creation site.
Use Sessionless to create an account behind the scenes. 
Save the image to that account, and ask for an email to send it to.
Now there's no friction to using your site, and users see its value before they have to sign up.

* Any time you want account based behavior on a single device like games on consoles. 
Remove the need to type out emails and passwords, but still maintain [account continuity].

* Devices that aren't usually connected to the internet like bluetooth devices (controllers, keyboards, headphones, etc).

## How do we get there?

So the purpose of this repo is to define the protocol, implement it in as many languages as possible, and provide examples in as many frameworks as possible.
We want to create [this oauth page][oauth-page], but make it look like it wasn't made by a bunch of devs in 2004.
Based on what Sessionless can do, and what we're trying to do here, my thoughts on where we could use UX are:

* since Sessionless obviates the need for email and password, SSO, passkeys, etc, what could account creation look like?

* what should account recovery look like (can we do better than forgot password)

* Sessionless works everywhere so how does this work on web, mobile, game consoles, desktop, hardware, refrigerators…

* how should associating keys work

* how could revoking keys work?

* is Sessionless worth branding in some way

* need web design for Sessionless.org

* need small designs for all of the examples (great if you’re new to that platform and want to learn)

But any and all help is welcome. Also if you'd rather think of an implementation use case for Sessionless, those are welcome too!

## How to contribute

You are of course welcome to submit PRs, and issues, but you can also come chat with us over at 
<a href="https://discord.gg/W4mQqNnfSq">
<img src="https://discordapp.com/api/guilds/913584348937207839/widget.png?style=shield"/></a>

I'm planetnineisaspaceship there.

## Further reading

* [What makes cryptography hard, and how does Sessionless make it easier?](./docs/Cryptography.md)
* [What makes this an authentication/identity system?](./docs/Authentication-and-identity.md)
* [How does this work, and why should I trust it?](./docs/How-does-this-work.md)
* [What's the primary/secondary thing (read this for how this relates to OAuth2.0)?](./docs/Primary-and-secondary.md)
* [Is this secure?](./docs/Is-Sessionless-secure.md)
* [How does this compare to Web 3.0?](./docs/Web-3.md)

[smart-cities]: https://static1.squarespace.com/static/5bede41d365f02ab5120b40f/t/65d305f9682e3158ed9386cf/1708328441775/ACM+Identity+Paper.pdf
[account continuity]: ./docs/Authentication-and-identity.md
[oauth-page]: https://oauth.net/code/
