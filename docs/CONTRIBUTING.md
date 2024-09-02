# How to contribute

I'm of the opinion that contributing should be as easy and inviting as possible. 
This guide is written with that premise in mind.

The discussion about Sessionless takes place on Discord in the [Open Source Force][osf] server. 
Follow that link to get an invite. 

Before contributing, I recommend you read through the READMEs and supporting documentation thoroughly.

## Testing

New language implementations should include an example server, which is added to the color tests of the language CLIs (of which only javascript and Rust are currently setup for new languages).

New client implementations should be testable against any of the example servers, and should include an example with some simple UI for doing that test.

Unit tests for implementations are encouraged, but not required since testing an implementation against itself can give false positives.[^1]

## Submitting changes

Please send a with a clear list of what you've done (read more about [pull requests](http://help.github.com/pull-requests/)). 
Feel free to give us a heads up on Discord of your impressive contribution.

## Coding conventions

The Sessionless repo is multi-language, and multi-platform so there are no global conventions. 
Still some concepts are common between implementations.

* Sessionless is small (only 6-8 functions), and should be as flat as possible.
Try to avoid indirection, and unecessary abstraction.

* Your implementation should match the words of the names of functions, but adopt other conventions of your language, i.e. snake_casing vs camelCasing.

* If your implementation errors, it should log a clear message about why it is erroring.

Thank you for your interest in Sessionless!
We look forward to seeing what you come up with.

Planet Nine & Open Source Force

[osf]: https://opensourceforce.net
