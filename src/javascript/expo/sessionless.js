import { secp256k1 } from 'ethereum-cryptography/secp256k1';
import { keccak256 } from "ethereum-cryptography/keccak.js";
import { bytesToHex } from "ethereum-cryptography/utils.js";
import * as crypto from "expo-crypto";
import * as SecureStore from "expo-secure-store";

let getKeysFromDisk;

const keysToSaveString = 'EE305432-6349-44E8-AE9D-193071152FE7';

const AsyncFunction = (async () => {}).constructor;

const utf8ToBytes = (stri) => {
    const str = stri.slice(0, 32);
    return Uint8Array.from(Array.from(str).map(letter => letter.charCodeAt(0)));
};

const decimalToHex = (str) => {
    let decimal = str.toString().split('');
    let sum = [];
    let hex = [];
    let i;
    let s;
    while(decimal.length){
        s = 1 * decimal.shift()
        for(i = 0; s || i < sum.length; i++){
            s += (sum[i] || 0) * 10;
            sum[i] = s % 16;
            s = (s - sum[i]) / 16;
        }
    }
    while(sum.length){
        hex.push(sum.pop().toString(16));
    }
    return hex.join('');
};

const generateKeys = async (saveKeys, getKeysOverride) => {
  if(saveKeys || getKeysOverride) {
    console.warn('It is not recommended to supply saveKeys and/or getKeys in Expo');
  }
  const byteArray = new Uint8Array(32);
  const privateKey = bytesToHex(crypto.getRandomValues(byteArray)); 
  const publicKey = bytesToHex(secp256k1.getPublicKey(privateKey));
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
  const messageHash = keccak256(utf8ToBytes(message));
  const signatureAsBigInts = secp256k1.sign(messageHash, privateKey);
  const signature = {
    r: decimalToHex(signatureAsBigInts.r),
    s: decimalToHex(signatureAsBigInts.s),
    recovery: signatureAsBigInts.recovery
  };
  return signature;
};

const verifySignature = async (signature, message, publicKey) => {
  const messageHash = keccak256(utf8ToBytes(message));
  return secp256k1.verify(signature, messageHash, publicKey);
};

const sessionless = {
  generateKeys,
  getKeys,
  sign,
  verifySignature,
  generateUUID
};

export default sessionless;

