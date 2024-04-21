import sessionless from "npm:sessionless-node@^0.10.0";
import chalk from "npm:chalk";
import { getUser, saveUser } from "./src/persistence/user.ts";
import { associate, getValue, saveValue } from "./src/demo/demo.ts";

const ResponseError = (code, error) => {
  return new Response(error, {
    status: code,
    headers: {
      "content-type": "application/json; charset=utf-8",
    },
  });
};

const dispatch = async (request: Request): Response | Error => {
console.log('in dispatch');
  if(request.url.indexOf('register') > -1) {
    return await register(request);
  }
  if(request.url.indexOf('cool-stuff') > -1) {
    return await doCoolStuff(request);
  }
  if(request.url.indexOf('value') > -1) {
    if(request.method === 'GET') {
console.log("routing to get value");
      return await getValue(request);
    }
    return await saveValue(request);
  }
  if(request.url.indexOf('associate') > -1) {
    return await associate(request);
  }
};

const register = async (request: Request): Response | Error => {
  const payload = await request.json();
  const signature = payload.signature;

  const message = JSON.stringify({
    pubKey: payload.pubKey,
    enteredText: payload.enteredText,
    timestamp: payload.timestamp
  });

console.log('about to check signature');
console.log(JSON.stringify(payload));
console.log('message: ' + message);

  if(!signature || !sessionless.verifySignature(signature, message, payload.pubKey)) {
console.error('auth error');
    return ResponseError(403, 'Auth error');
<<<<<<< HEAD
   }
=======
  }
>>>>>>> 1d7f9ef6 (Fixed js bug, and added lots of users test)

console.log('verified');

  const uuid = sessionless.generateUUID();
  await saveUser(uuid, payload.pubKey);

  console.log(chalk.green(`\n\nuser registered with uuid: ${uuid}`));

  return {uuid, welcomeMessage: "Welcome to Sessionless"};
};

const doCoolStuff = async (request: Request): Response | Error => {
  const payload = await request.json();
  const signature = payload.signature;

  const user = await getUser(payload.uuid);

console.log(user);

  const message = JSON.stringify({
    uuid: payload.uuid,
    coolness: payload.coolness,
    timestamp: payload.timestamp
  });

console.log(user.pubKey);

  if(!signature || !sessionless.verifySignature(signature, message, user.pubKey)) {
    return ResponseError(401, 'Auth error');
  }

  return {doubleCool: 'double cool'};
};

Deno.serve({port: 3002}, async (request: Request) => {
console.log('got request');
console.log(request);
  const res = await dispatch(request);
console.log('got res');
console.log(res);
  return new Response(JSON.stringify(res), {
    headers: {
      "content-type": "application/json; charset=utf-8",
    }
  });
});
