import { v4 as uuidv4 } from 'uuid';
import { secp256k1 } from 'ethereum-cryptography/secp256k1';
import { keccak256 } from "ethereum-cryptography/keccak.js";
import { bytesToHex } from "ethereum-cryptography/utils.js";
import * as crypto from "expo-crypto";
import * as SecureStore from "expo-secure-store";

let getKeysFromDisk;

const keysToSaveString = 'EE305432-6349-44E8-AE9D-193071152FE7';

const AsyncFunction = (async () => {}).constructor;

const utf8ToBytes = (str) => {
    return Uint8Array.from(Array.from(str).map(letter => letter.charCodeAt(0)));
};

const generateKeys = async (saveKeys, getKeysOverride) => {
  if(saveKeys || getKeysOverride) {
    console.warn('It is not recommended to supply saveKeys and/or getKeys in Expo');
  }
  const byteArray = new Uint8Array(32);
  const privateKey = bytesToHex(crypto.getRandomValues(byteArray)); 
  const pubKey = bytesToHex(secp256k1.getPublicKey(privateKey));
  saveKeys && (saveKeys instanceof AsyncFunction ? await saveKeys({
    privateKey,
    pubKey
  }) : saveKeys({
    privateKey,
    pubKey
  }));
  await SecureStore.setItemAsync(keysToSaveString, JSON.stringify({
    privateKey,
    pubKey
  }));
  getKeysFromDisk = getKeysOverride;
};

const getKeys = async () => {
  if(!getKeysFromDisk) {
    const keyString = await SecureStore.getItemAsync(keysToSaveString);
    return await JSON.parse(keyString);
  } else {
    return getKeysFromDisk instanceof AsyncFunction ? await getKeysFromDisk() : getKeysFromDisk();
  }
};

const sign = async (message) => {
  const { privateKey } = await getKeys();
  const messageHash = keccak256(utf8ToBytes(message));
  const signatureAsBigInts = secp256k1.sign(messageHash, privateKey);
  const signature = signatureAsBigInts.r.toString(16) + signatureAsBigInts.s.toString(16);
  return signature;
};

const verifySignature = (sig, message, pubKey) => {
  const messageHash = keccak256(utf8ToBytes(message));

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

