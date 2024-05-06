import expressSession from 'express-session';
import express from 'express';
import cors from 'cors';
import bodyParser from 'body-parser';
import chalk from 'chalk';
import sessionless from 'sessionless-node';
import fs from 'fs';
import { readFile } from 'node:fs/promises';
import path from 'path';
import url from 'url';
import { getUser, saveUser } from './src/persistence/user.js';
import { addRoutes } from './src/demo/demo.js';

const __filename = url.fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const app = express();

/**
 * Uncomment this when debugging what client implementations are sending
 */
//app.use(express.raw({ type: '*/*', limit: '10mb' }));
/*app.use((req) => {
  console.log(req.body.toString('utf-8'));
});*/

app.get('/', (req, res, next) => {
  console.log(req.socket.remoteAddress);
});

app.use(expressSession({
  secret: 'foo bar baz',
  cookie: {
    name: 'don\'t use this name',
    maxAge: 24 * 60 * 60 * 1000,
    httpOnly: true,
    signed: false
  }
}));

let users = {};
let currentPrivateKey = '';
let getKeys = () => {
  return (() => { return {privateKey: currentPrivateKey} })();
};

const webSignature = async (req, message) => {
  currentPrivateKey = req.session.user;
  return await sessionless.sign(message);
};

const handleWebRegistration = async (req, res) => {

  const keys = await sessionless.generateKeys((keys) => {
    keys[keys.publicKey] = keys.privateKey;
  }, getKeys);

  req.session.user = keys.privateKey;

  const uuid = sessionless.generateUUID();
  users[uuid] = keys.publicKey;

  await saveUser(uuid, keys.publicKey);

  res.send({
    uuid,
    welcomeMessage: "Welcome to Sessionless!"
  });
};

app.use(bodyParser.json());

app.post('/register', async (req, res) => {
console.log(req.body);
  const payload = req.body;
  const signature = payload.signature;

  if(!signature) {
    return handleWebRegistration(req, res);
  }
  
  const pubKey = payload.pubKey; 
  
  const message = JSON.stringify({ 
    pubKey, 
    enteredText: payload.enteredText, 
    timestamp: payload.timestamp 
  });
console.log(message);
console.log(signature);
console.log(pubKey);

const verified = await sessionless.verifySignature(signature, message, pubKey);
console.log('verified: ' + verified);

  if(await sessionless.verifySignature(signature, message, pubKey)) {
    const uuid = sessionless.generateUUID();
    await saveUser(uuid, pubKey);
    const user = {
      uuid,
      welcomeMessage: "Welcome to this example!"
    };
    console.log(chalk.blue(`\n\nuser registered with uuid: ${uuid}`));
    res.send(user);
  } else {
    console.log(chalk.red('unverified!'));
    res.send({error: 'auth error'});
  }
});

app.post('/cool-stuff', async (req, res) => {
console.log(req.body);
  const payload = req.body;
  const message = JSON.stringify({ uuid: payload.uuid, coolness: payload.coolness, timestamp: payload.timestamp });
console.log(req.body);
  const publicKey = getUser(payload.uuid).pubKey; 
  const signature = payload.signature || (await webSignature(req, message));

console.log(message);
console.log(signature);
console.log(publicKey);

  if(sessionless.verifySignature(signature, message, publicKey)) {
    return res.send({
      doubleCool: 'double cool'
    });
  }
  return res.send({error: 'auth error'});
});

addRoutes(app);

app.use(express.static('../web'));

app.listen(3001);






