# Sessionless Product Details

**Introduction & Base Definitions**


Authentication protocols (auth protocols) are gatekeepers that verify the identity of a person to give them access to specific programs or data. They include a set of rules or procedures that govern how data is transmitted across electronic devices. The most common uses of auth protocols are passwords, but others include one-time-passwords, biometrics, and magic links. 

- An Application Programming Interface (API) is  a software intermediary that allows two  applications to share data with each other
- An API Key is  a unique code that identifies an application or user
- An API Token is a piece of code that contains information about a user and is used to secure access to an API
- Encryption is the process of turning data into code to prevent unauthorized access

**Problem Statement**

Many auth protocols are client-server, where the client supplies some secret information (usually name and email address with a password) to authenticate requests.

There are multiple problems with this set up:
- Requires users to enter in personally identifiable information which can be hacked presenting legal risks 
- Requires encryption to prevent sensitive data leaks using https which slows and limits authentication opportunities (can’t connect to anything other than standard computers)
- Ignores other authentication needs such as between processes on one machine, or server-server relationships. 

	For example, setting up a payment processor like Stripe requires obtaining an API token and Key and connecting it to your product’s server. These keys can be compromised, creating security risks, and obtaining your key if it is lost is often impossible, which means starting the process from scratch.
- Storage of secret information is platform dependent
- API Key creation is also platform dependent for randomness
- Language support for a given auth approach might be limited, locking companies into a particular tech stack
- Requires users to be connected to the internet

**Solution**

Enter Sessionless, an authentication protocol (Sessionless Authentication Protocol, or SLAP), allowing for simplicity and flexibility by opening opportunities for indirect authentication. 

With Sessionless, Services (websites, applications, and other digital services) can:
- Authenticate users without gathering personal information which will
- Reduce bounce rates
- Remove security risks
- Eliminate need for platform dependency to store secret information
- Simplify authentication by removing key creation process 
- Authenticate users across devices such as computers, smart Tvs, Game Consoles, whether connected to the internet or connected via bluetooth devices

And Users can:
- Decrease usage of password managers
- Gain security 

**Use Cases**
- Decrease website bounce rates - 

	The average bounce rate for B2C websites is [45%](https://www.fullstory.com/blog/what-is-a-good-bounce-rate/). “On certain pages, you may be asking visitors to do more than just read the content—like signing up for an account or filling out a form. If the page is asking for too much information or requires too many steps, then visitors may leave before taking any action; they'll have perceived too much cost before perceiving value.” With Sessionless, you can provide [account continuity](https://github.com/planet-nine-app/sessionless/blob/main/docs/Authentication-and-identity.md) without asking for an email upfront, leaving the credential entering for after a user engages with your site.
- Connect user accounts to single devices such as game consoles and maintain account continuity
- Connect users to bluetooth devices such as controllers, keyboards, headphones, etc. 

