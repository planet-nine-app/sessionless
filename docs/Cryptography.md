# Cryptography

The entire realm of cryptography is pretty big, and out of scope for this little doc here. I'll focus on the asymmetric cryptography we use in Sessionless. Bare with me, we're gonna do just a little bit of math, but it'll be worth it.

You might remember from algebra an equation that looks like:

$x + y = 7$

If I tell you that $y = 4$, and we have:

$x + 4 = 7$

It's not that difficult to see that $x = 3$, but pedagogically let's write out the solve for $x$ step:

$x = 7 - 4$

This works because the addition and subtraction are inverse operations. 

Now imagine an operation that I'll represent withi $🔒$. Just like $+$, it takes two inputs on either side and gives one output:

$privateKey 🔒 messageHash = signature$

This is the basic equation of an asymmetric cryptography scheme where $🔒$ is an algorithm that, while cryptic (heh), isn't that crazy to [read](https://github.com/paulmillr/noble-curves/blob/main/src/secp256k1.ts). The key is, unlike addition, the $🔒$ operation doesn't have an inverse. This lack of reversibility is one of two key components to any cryptography because if the algorithm was reversible, an attacker could just apply the inverse to any signature and recover any private key just like solving for x once you know y. And this is why the private key is so important to keep hidden. It's the only part of the equation that is unknown. 

The second key component of cryptography is randomness. Since messageHashes and signatures get passed around, they can get intercepted. Once an attacker knows the messageHash and signature, they can just start trying private keys until they find one that matches. The only thing stopping computers from doing this is that for a key the size of secp256k1's keys, and thus Sessionless's keys, the number of possible keys is around the same order of magnitude as the number of atoms in the observable universe. 

That only matters though if the private key you pick is sufficiently random, and that's something that computers, being deterministic, cannot do. To combat that, and thus enable cryptographic functions, hardware manufacterers introduced hardware components, which gather "entropy" an overloaded term, which in a computer context refers to input that comes from outside of the computer itself to allow for randomness. This might come from things like jiggles in voltage amounts from the computer's power source, or mouse clicks and keyboard strokes (Darek if you're reading this we can debate whether humans are sufficiently non-deterministic for this to be truly random over beers some time). 

These two important elements of doing cryptography: private key storage, and randomness were half of the motivation for Sessionless. Luckily platforms have gotten on the cryptography train, and started making these things available. Unfortunately the implementations differ on every platform. Hence this repo, where we can all build out reference implementations, and then carry them into our work. 
