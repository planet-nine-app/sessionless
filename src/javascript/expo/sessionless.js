import { secp256k1 } from 'ethereum-cryptography/secp256k1';
import { keccak256 } from "ethereum-cryptography/keccak.js";
import { bytesToHex } from "ethereum-cryptography/utils.js";
import * as crypto from "expo-crypto";
import * as SecureStore from "expo-secure-store";

let getKeysFromDisk;

const keysToSaveString = 'EE305432-6349-44E8-AE9D-193071152FE7';

const AsyncFunction = (async () => {}).constructor;

const generateKeys = async (saveKeys, getKeysOverride) => {
  if(saveKeys || getKeysOverride) {
    console.warn('It is not recommended to supply saveKeys and/or getKeys in Expo');
  }
  const byteArray = new Uint8Array(32);
  const privateKey = crypto.getRandomValues(byteArray); 
  const publicKey = secp256k1.getPublicKey(privateKey);
  saveKeys && (saveKeys instanceof AsyncFunction ? await saveKeys({
    privateKey,
    publicKey
  }) : saveKeys({
    privateKey,
    publicKey
  }));
  await SecureStore.setItemAsync(keysToSaveString, JSON.stringify({
    privateKey,
    publicKey
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
  const messageInBytes = new TextEncoder().encode(message);
console.log('messageInBytes: ' + messageInBytes);
//  const messageHash = keccak256(utf8ToBytes(message));
  return secp256k1.sign(messageHash, privateKey);
};

const verifySignature = async (signature, message) => {
  const { publicKey } = await getKeys();
  const messageHash = keccak256(utf8ToBytes(message));
  return secp256k1.verify(signature, messageHash, publicKey);
};

const generateUUID = () => {
  return  crypto.getRandomBytes(32);
};

const sessionless = {
  generateKeys,
  getKeys,
  sign,
  verifySignature,
  generateUUID
};

export default sessionless;

