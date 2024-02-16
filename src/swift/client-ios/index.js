//import sessionless from '@zachbabb/sessionless-node';
let window = {};

window.sessionless = require('@zachbabb/sessionless-node');

window.generateKeys = sessionless.generateKeys;

const getKeys = sessionless.getKeys;

const sign = sessionless.sign;

const verifySignature = sessionless.verifySignature;

const generateUUID = sessionless.generateUUID;


