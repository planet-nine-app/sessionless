import expressSession from 'express-session';
import express from 'express';
import cors from 'cors';
import bodyParser from 'body-parser';
import chalk from 'chalk';
import sessionless from '@zachbabb/sessionless-node';
import fs from 'fs';
import path from 'path';
import url from 'url';

const __filename = url.fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const app = express();

app.use(
  cors({
    origin: "http://localhost:3000",
    methods: ["POST", "PUT", "GET", "OPTIONS", "HEAD"],
    credentials: true,
  })
);

const saveUser = (userUUID, publicKey) => {
  const usersString = fs.readFileSync('./users.json');
  const users = JSON.parse(usersString);
  users[userUUID] = publicKey;
  fs.writeFileSync('./users.json', JSON.stringify(users));
};

const getUserPublicKey = (userUUID) => {
  const usersString = fs.readFileSync('./users.json');
  const users = JSON.parse(usersString);
  return users[userUUID];
};

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
let keys = {};
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

  const userUUID = sessionless.generateUUID();
  users[userUUID] = keys.publicKey;

  saveUser(userUUID, keys.publicKey);

  res.send({
    userUUID,
    welcomeMessage: "Welcome to Sessionless!"
  });
};

app.use(bodyParser.json());

app.put('/register', (req, res) => {
  const payload = req.body;
  const signature = payload.signature;

  if(!signature) {
    return handleWebRegistration(req, res);
  }
  
  const publicKey = payload.publicKey; 
  
  const message = JSON.stringify({ 
    publicKey, 
    enteredText: payload.enteredText, 
    timestamp: payload.timestamp 
  });

  if(sessionless.verifySignature(signature, message, publicKey)) {
    const userUUID = sessionless.generateUUID();
    saveUser(userUUID, publicKey);
    let user = {
      userUUID,
      welcomeMessage: "Welcome to this example!"
    };
    console.log(chalk.green(`user registered with userUUID: ${userUUID}`));
    res.send(user);
  } else {
    console.log(chalk.red('unverified!'));
  }
});

app.put('/cool-stuff', async (req, res) => {
  const payload = req.body;
  const message = JSON.stringify({ coolness: payload.coolness, timestamp: payload.timestamp });
  let signature = payload.signature;
  if(!signature) {
    signature = await webSignature(req, message);
  }

  const publicKey = getUserPublicKey(payload.userUUID); 

  if(sessionless.verifySignature(signature, message, publicKey)) {
    return res.send({
      doubleCool: 'double cool'
    });
  }
  return res.send({error: 'auth error'});
});

app.use(express.static('../web'));

app.listen(3000);
