import sessionless from 'sessionless-node';
import { associateKey, getUser, saveUser, getValue, saveValue } from '../persistence/user.js';

export const addRoutes = (app) => {
  app.post('/associate', async (req, res) => {
    const payload = req.body;
    const signature1 = payload.signature1;
    const signature2 = payload.signature2;

    const user = getUser(payload.uuid1);

    if(!signature1 || !signature2) {
      res.send(new Error('Need two signatures'));
    }
    
    const message1 = JSON.stringify({
      uuid1: payload.uuid1,
      timestamp: payload.timestamp
    });
    
    const message2 = JSON.stringify({
      uuid2: payload.uuid2,
      timestamp: payload.timestamp,
      pubKey2: payload.pubKey2
    });

    if(!sessionless.verifySignature(signature1, message1, user.pubKey1) || 
       !sessionless.verifySignature(signature2, message2, payload.pubKey2)) {
      res.send(new Error('Auth error'));
    }

    associateKey(user, payload.uuid2, payload.pubKey2);

    res.send({success: true});
  });

  app.post('/value', async (req, res) => {
    const payload = req.body;
    const signature = payload.signature;
    const pubKey  = getUser(payload.uuid).pubKey;

    const message = JSON.stringify({
      timestamp: paylaod.timestamp,
      uuid: payload.uuid,
      value: payload.value
    });

    if(!sessionless.verifySignature(signature, message, pubKey)) {
      return Response(401, 'Auth error');
    }

    await saveValue(payload.uuid, value);

    res.send({success: true});
  });

  app.get('/value', async (req, res) => {
    const payload = req.body;
    const signature = payload.signature;
    const pubKey  = getUser(payload.uuid).pubKey;

    const message = JSON.stringify({
      timestamp: paylaod.timestamp,
      uuid: payload.uuid,
      value: payload.value
    });

    if(!sessionless.verifySignature(signature, message, pubKey)) {
      return Response(401, 'Auth error');
    }

    await getValue(payload.uuid);

    res.send({ value });
  });
};


