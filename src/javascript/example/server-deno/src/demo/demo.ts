import sessionless from 'npm:sessionless-node';
import chalk from 'npm:chalk';
import { associateKey, getUser, saveUser } from '../persistence/user.ts';

export const associate = async (request: Request): Response | Error => {
  const payload = await request.json();
  const signature1 = payload.signature1;
  const signature2 = payload.signature2;

  const user = await getUser(payload.uuid1);

  if(!signature1 || !signature2) {
    res.send(new Error('Need two signatures'));
  }

  const message1 = JSON.stringify({
    uuid: payload.uuid1,
    timestamp: payload.timestamp1
  });

  const message2 = JSON.stringify({
    uuid: payload.uuid2,
    timestamp: payload.timestamp2,
    pubKey: payload.pubKey
  });

  if(!sessionless.verifySignature(signature1, message1, user.pubKey) ||
     !sessionless.verifySignature(signature2, message2, payload.pubKey)) {
    res.send(new Error('Auth error'));
  }
    
  associateKey(user, payload.uuid2, payload.pubKey2);

  console.log(chalk.green('\n\nKeys associated'));

  return new Response(200, {success: true});
};

export const saveValue = async (request: Request): Response | Error => {
  const payload = await request.json();
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

  console.log(chalk.green('\n\nvalue set'));

  return Response(200, {success: true});
};

export const getValue = async (request: Request): Response | Error => {
  const payload = {
    timestamp: request.searchParams.get('timestamp'),
    uuid: request.searchParams.get('uuid'),
    signature: request.searchParams.get('signature'),
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

  console.log(chalk.green('\n\nreturning value'));

  return Response(200, { value });  
};

