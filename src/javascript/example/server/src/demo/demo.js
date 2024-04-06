import sessionless from 'sessionless-node';
import chalk from 'chalk';
import { associateKey, getUser, saveUser, getValue, saveValue } from '../persistence/user.js';

export const addRoutes = (app) => {
  app.post('/associate', async (req, res) => {
    const payload = req.body;
    const signature1 = payload.signature1;
    const signature2 = payload.signature2;

    const user = getUser(payload.uuid1);
console.log(user);

    if(!signature1 || !signature2) {
      res.send(new Error('Need two signatures'));
    }
    
    const message1 = JSON.stringify({
      uuid: payload.uuid1,
      timestamp: payload.timestamp1
    });
console.log(message1);
    
    const message2 = JSON.stringify({
      uuid: payload.uuid2,
      timestamp: payload.timestamp2,
      pubKey: payload.pubKey
    });
console.log(message2);

    if(!sessionless.verifySignature(signature1, message1, user.pubKey) || 
       !sessionless.verifySignature(signature2, message2, payload.pubKey)) {
      res.send(new Error('Auth error'));
    }

    associateKey(user, payload.uuid2, payload.pubKey2);

    console.log(chalk.blue('\n\nKeys associated'));

    res.send({success: true});
  });

  app.post('/value', async (req, res) => {
    const payload = req.body;
    const signature = payload.signature;
    const pubKey  = getUser(payload.uuid).pubKey;

    const message = JSON.stringify({
      timestamp: payload.timestamp,
      uuid: payload.uuid,
      value: payload.value
    });

    if(!sessionless.verifySignature(signature, message, pubKey)) {
      console.log(chalk.red('\n\nAuth error'));
      return Response(401, 'Auth error');
    }

    await saveValue(payload.uuid, payload.value);

    console.log(chalk.blue('\n\nvalue set'));

    res.send({success: true});
  });

  app.get('/value', async (req, res) => {
    const payload = {
      timestamp: req.query.timestamp,
      uuid: req.query.uuid,
      signature: req.query.signature
    };
    const signature = payload.signature;
    const pubKey  = getUser(payload.uuid).pubKey;

    const message = JSON.stringify({
      timestamp: paylaod.timestamp,
      uuid: payload.uuid
    });

    if(!sessionless.verifySignature(signature, message, pubKey)) {
      return Response(401, 'Auth error');
    }

    await getValue(payload.uuid);

    console.log(chalk.blue('\n\nreturning value'));

    res.send({ value });
  });
};


