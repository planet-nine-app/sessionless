import sessionless from 'npm:sessionless-node@^0.10.0';
import chalk from 'npm:chalk';
import { associateKey, getUser, getValue as gv, getUserByAssociatedKey, saveUser, setValue } from '../persistence/user.ts';

export const associate = async (request: Request): Response | Error => {
  const payload = await request.json();
  const signature1 = payload.signature1;
  const signature2 = payload.signature2;

  const user = await getUser(payload.uuid1);

  if(!signature1 || !signature2) {
    return new Response(403, {error: 'Need two signatures'});
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
    return new Response(401, {error: 'Auth error'});
  }
    
  await associateKey(user, payload.uuid2, payload.pubKey);

  console.log(chalk.green('\n\nKeys associated'));

  return new Response(200, {success: true});
};

export const saveValue = async (request: Request): Response | Error => {
  const payload = await request.json();
  const signature = payload.signature;
  const user = await getUserByAssociatedKey(payload.uuid);
  const pubKey = user.pubKey;

  const message = JSON.stringify({
    timestamp: payload.timestamp,
    uuid: payload.uuid,
    value: payload.value
  });

  if(!sessionless.verifySignature(signature, message, pubKey)) {
    console.log(chalk.red('\n\nAuth error'));
    return new Response(401, {error: 'Auth error'});
  }

  await setValue(payload.uuid, payload.value);

  console.log(chalk.green('\n\nvalue set'));

  return new Response(200, {success: true});
};

export const getValue = async (request: Request): Response | Error => {
  const url = new URL(request.url);
  const payload = {
    timestamp: url.searchParams.get('timestamp'),
    uuid: url.searchParams.get('uuid'),
    signature: url.searchParams.get('signature'),
  };
  const signature = payload.signature;
  const user  = await getUser(payload.uuid);
  const pubKey = user.pubKey;

  const message = JSON.stringify({
    timestamp: payload.timestamp,
    uuid: payload.uuid
  });

  if(!sessionless.verifySignature(signature, message, pubKey)) {
    return new Response(401, {error: 'Auth error'});
  }

  const value = await gv(payload.uuid);

  if(!value) {
    return new Response(404, {error: "not found"});
  }

  console.log(chalk.green('\n\nreturning value'));

  return new Response(200, { value });  
};

