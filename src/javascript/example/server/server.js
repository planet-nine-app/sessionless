import express from 'express';
import bodyParser from 'body-parser';
import chalk from 'chalk';
import sessionless from '@zachbabb/sessionless-node';
import fs from 'fs';

const app = express();
app.use(bodyParser.json());

const saveUser = (uuid, publicKey) => {
  fs.writeFileSync('./users.json', JSON.stringify({
    uuid,
    publicKey
  }));
};

const getUserPublicKey = (uuid) => {
  const usersString = fs.readFileSync('./users.json');
  const users = JSON.parse(usersString);
  return users.publicKey;
};

app.put('/register', (req, res) => {
  const payload = req.body;
  const signature = payload.signature;
  
  const publicKey = payload.publicKey; 
  
  const message = JSON.stringify({ 
    publicKey, 
    enteredText: payload.enteredText, 
    timestamp: payload.timestamp 
  });

console.log(message);

  if(sessionless.verifySignature(signature, message, publicKey)) {
    const uuid = sessionless.generateUUID();
    saveUser(uuid, publicKey);
    const user = {
      uuid,
      welcomeMessage: "Welcome to this example!"
    };
    console.log(chalk.green(`user registered with uuid: ${uuid}`));
    res.send(user);
  } else {
    console.log(chalk.red('unverified!'));
  }
});

app.put('/cool-stuff', (req, res) => {
  const payload = req.body;
  const signature = payload.signature;

  const publicKey = getUserPublicKey(payload.uuid); 

  const message = JSON.stringify({ coolness: payload.coolness, timestamp: timestamp });

  if(sessionless.verifySignature(message, signature, publicKey)) {
    res.send({
      doubleCool: true
    });
  }
});

app.listen(3000);
