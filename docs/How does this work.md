For the purposes of this doc, I ask that you put your personal feelings about crypto and blockchains aside. As with all software projects, there're always things to learn both good and bad. 

Here's how the excerpt from Satoshi's original Bitcoin paper begins:

> A purely peer-to-peer version of electronic cash would allow online payments to be sent directly from one party to another without going through a financial institution. _Digital signatures provide part of the solution_ [emphasis added], but the main benefits are lost if a trusted third party is still required to prevent double-spending.

I'd been thinking about cryptographic identity systems for a while when I first read this, and was excited to learn more about these digital signatures. While they are mentioned again in the paper as creating an auditable trail of coin ownership, it doesn't tell the whole story about what these digital signatures are, and why they're so important. 

Assuming positive intent, Bitcoin needed a way for users to join its network without submitting personal information. Why without? Because the only database Bitcoin has is the blockchain, and the blockchain is public. You certainly don't want to be storing people's emails and passwords in a public database. 

Here it might help to look at some code. This is from [Ethereum's JavaScript implementation](https://github.com/ethereumjs/ethereumjs-monorepo/blob/master/packages/util/src/signature.ts):

``` js
/**
 * Returns the ECDSA signature of a message hash.
 *
 * If `chainId` is provided assume an EIP-155-style signature and calculate the `v` value
 * accordingly, otherwise return a "static" `v` just derived from the `recovery` bit
 */
export function ecsign(
  msgHash: Uint8Array,
  privateKey: Uint8Array,
  chainId?: bigint
): ECDSASignature {
  const sig = secp256k1.sign(msgHash, privateKey) // create a signature just like Sessionless
  const buf = sig.toCompactRawBytes()
  const r = buf.slice(0, 32)
  const s = buf.slice(32, 64) // r and s are two parts of the signature

  const v =
    chainId === undefined
      ? BigInt(sig.recovery! + 27)
      : BigInt(sig.recovery! + 35) + BigInt(chainId) * BIGINT_2 // v is specific to blockchains see https://bitcoin.stackexchange.com/questions/38351/ecdsa-v-r-s-what-is-v/38909#38909

  return { r, s, v }
}
```

Nothing wild here. You take a message and you sign it, just like Sessionless. And then to check it:

``` js
/**
 * ECDSA public key recovery from signature.
 * NOTE: Accepts `v === 0 | v === 1` for EIP1559 transactions
 * @returns Recovered public key
 */
export const ecrecover = function (
  msgHash: Uint8Array,
  v: bigint,
  r: Uint8Array,
  s: Uint8Array,
  chainId?: bigint
): Uint8Array {
  const signature = concatBytes(setLengthLeft(r, 32), setLengthLeft(s, 32))
  const recovery = calculateSigRecovery(v, chainId)
  if (!isValidSigRecovery(recovery)) {
    throw new Error('Invalid signature v value')
  }

  const sig = secp256k1.Signature.fromCompact(signature).addRecoveryBit(Number(recovery))
  const senderPubKey = sig.recoverPublicKey(msgHash) // Ethereum and Bitcoin recover the pubKey from the sig,
                                                     // and then check it against the address of the transaction
                                                     // (addresses are calculated from pubKeys which is why that 
                                                     // works).
  return senderPubKey.toRawBytes(false).slice(1)
```

We use a slightly different method to verify the signature, because it's faster, and comes from the underlying crypto libraries that the blockchains use [see more here](https://crypto.stackexchange.com/questions/57718/verification-using-recovered-public-key-from-ecdsa-signature-and-normal-verifica#57725).
