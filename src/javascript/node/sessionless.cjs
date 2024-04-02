'use strict';

var uuid = require('uuid');
var secp256k1 = require('ethereum-cryptography/secp256k1');
var keccak_js = require('ethereum-cryptography/keccak.js');
var utils_js = require('ethereum-cryptography/utils.js');

let getKeysFromDisk;

const AsyncFunction = (async () => {}).constructor;

const generateKeys = async (saveKeys, getKeys) => {
  if(!saveKeys || !getKeys) {
    throw new Error(`Since this can be run on any machine with node, there is no default secure storage. You will need to provide a saveKeys and getKeys function`);
  }
  const privateKey = utils_js.bytesToHex(secp256k1.secp256k1.utils.randomPrivateKey());
  const publicKey = utils_js.bytesToHex(secp256k1.secp256k1.getPublicKey(privateKey));
  saveKeys && (saveKeys instanceof AsyncFunction ? await saveKeys({
    privateKey,
    publicKey
  }) : saveKeys({
    privateKey,
    publicKey
  }));
  getKeysFromDisk = getKeys;
  return {
    privateKey,
    publicKey
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
  const messageHash = keccak_js.keccak256(utils_js.utf8ToBytes(message.slice(0, 32)));
  const signatureAsBigInts = secp256k1.secp256k1.sign(messageHash, privateKey);
  const signature = {
    r: signatureAsBigInts.r.toString(16),
    s: signatureAsBigInts.s.toString(16),
    recovery: signatureAsBigInts.recovery
  };
  return signature;
};

const verifySignature = (signature, message, publicKey) => {
  const messageHash = keccak_js.keccak256(utils_js.utf8ToBytes(message.slice(0,32)));
  
  let hex = signature.r;
  if (hex.length % 2) { 
    hex = '0' + hex; 
  }

  const bn = BigInt('0x' + hex);

  let hex2 = signature.s;
  if (hex2.length % 2) { 
    hex2 = '0' + hex2; 
  }

  const bn2 = BigInt('0x' + hex2);

  const signature2 = {
    r: bn,
    s: bn2
  };
  
  const res = secp256k1.secp256k1.verify(signature2, messageHash, publicKey);
  return res;
};

const generateUUID = () => {
  return  uuid.v4();
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

module.exports = sessionless;
