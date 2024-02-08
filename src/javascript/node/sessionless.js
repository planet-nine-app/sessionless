import { secp256k1 } from 'ethereum-cryptography/secp256k1';
import { keccak256 } from "ethereum-cryptography/keccak.js";
import { utf8ToBytes } from "ethereum-cryptography/utils.js";
import { bytesToHex } from "ethereum-cryptography/utils.js";
import { getRandomBytesSync } from "ethereum-cryptography/random.js";

let getKeysFromDisk;

const generateKeys = (saveKeys, getKeys) => {
  if(!saveKeys || !getKeys) {
    return console.error(`Since this can be run on any machine with node, there is no default secure storage. You will need to provide a saveKeys and getKeys function`);
  }
  const privateKey = secp256k1.utils.randomPrivateKey();
  const publicKey = secp256k1.getPublicKey(privateKey);
  saveKeys({
    privateKey,
    publicKey
  });
  getKeysFromDisk = getKeys;
};

const getKeys = () => {
  if(!getKeysFromDisk) {
    return console.error(`Since this can be run on any machine with node, there is no default secure storage. You will need to have your own getKeys function.`);
  } else {
    return getKeysFromDisk();
  }
};

const sign = (message) => {
  const { privateKey } = getKeys();
  const messageHash = keccak256(utf8ToBytes(message));
  return secp256k1.sign(messageHash, privateKey);
};

const verifySignature = (signature, message) => {
  const { publicKey } = getKeys();
  const messageHash = keccak256(utf8ToBytes(message));
  return secp256k1.verify(signature, messageHash, publicKey);
};

const createUUID = () => {
  return  bytesToHex(getRandomBytesSync(32));
};

const sessionless = {
  generateKeys,
  getKeys,
  sign,
  verifySignature,
  createUUID
};

export default sessionless;
