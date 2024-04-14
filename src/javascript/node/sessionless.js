import { v4 as uuidv4 } from 'uuid';
import { secp256k1 } from 'ethereum-cryptography/secp256k1';
import { keccak256 } from "ethereum-cryptography/keccak.js";
import { bytesToHex } from "ethereum-cryptography/utils.js";
import { utf8ToBytes } from "ethereum-cryptography/utils.js";

let getKeysFromDisk;

const AsyncFunction = (async () => {}).constructor;

const generateKeys = async (saveKeys, getKeys) => {
  if(!saveKeys || !getKeys) {
    throw new Error(`Since this can be run on any machine with node, there is no default secure storage. You will need to provide a saveKeys and getKeys function`);
  }
  const privateKey = bytesToHex(secp256k1.utils.randomPrivateKey());
  const pubKey = bytesToHex(secp256k1.getPublicKey(privateKey));
  saveKeys && (saveKeys instanceof AsyncFunction ? await saveKeys({
    privateKey,
    pubKey
  }) : saveKeys({
    privateKey,
    pubKey
  }));
  getKeysFromDisk = getKeys;
  return {
    privateKey,
    pubKey
  };
};

const getKeys = async () => {
  if(!getKeysFromDisk) {
    throw new Error(`Since this can be run on any machine with node, there is no default secure storage. You will need to have your own getKeys function.`);
  } else {
    return getKeysFromDisk instanceof AsyncFunction ? await getKeysFromDisk() : getKeysFromDisk();
  }
};

const sign = async (message) => {
  const { privateKey } = await getKeys();
  const messageHash = keccak256(utf8ToBytes(message)).slice(32);
  const signatureAsBigInts = secp256k1.sign(messageHash, privateKey);
  const signature = signatureAsBigInts.r.toString(16) + signatureAsBigInts.s.toString(16);
  return signature;
};

const verifySignature = (sig, message, pubKey) => {
  const messageHash = keccak256(utf8ToBytes(message)).slice(32);
  
  let signature = {
    r: sig.substring(0, 64),
    s: sig.substring(64)
  };

  let hex = signature.r;
  if (hex.length % 2) { 
    hex = '0' + hex; 
  }

  const bn = BigInt('0x' + hex);

  signature.r = bn;

  let hex2 = signature.s;
  if (hex2.length % 2) { 
    hex2 = '0' + hex2; 
  }

  const bn2 = BigInt('0x' + hex2);

  signature.r = bn;
  signature.s = bn2;

  const res = secp256k1.verify(signature, messageHash, pubKey);
  return res;
};

const generateUUID = () => {
  return  uuidv4();
};

const associate = (primarySignature, primaryMessage, primaryPublicKey, secondarySignature, secondaryMessage, secondaryPublicKey) => {
  return (verifySignature(primarySignature, primaryMessage, primaryPublicKey) && verifySignature(secondarySignature, secondaryMessage, secondaryPublicKey)); 
};

const sessionless = {
  generateKeys,
  getKeys,
  sign,
  verifySignature,
  generateUUID,
  associate
};

export default sessionless;
