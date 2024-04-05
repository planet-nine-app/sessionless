import sessionless from "npm:sessionless-node";
import { getUser, saveUser } from "src/persistence/user";
import { associate, getValue, saveValue } from "src/demo/demo";

const ResponseError = (code, error) => {
  return new Response(error, {
    status: code,
    headers: {
      "content-type": "application/json; charset=utf-8",
    },
  });
};

const dispatch = async (request: Request): Response | Error => {
  if(request.url.indexOf('register') > -1) {
    return await register(request);
  }
  if(request.url.indexOf('cool-stuff') > -1) {
    return await doCoolStuff(request);
  }
  if(request.url.indexOf('value') > -1) {
    if(request.method === 'GET') {
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

  if(!signature || !sessionless.verifySignature(signature, message, payload.pubKey)) {
    return ResponseError(401, 'Auth error');
  }

  const uuid = sessionless.generateUUID();
  await saveUser(uuid, payload.pubKey);
  return {uuid, welcomeMessage: "Welcome to Sessionless"};
};

const doCoolStuff = async (request: Request): Response | Error => {
  const payload = await request.json();
  const signature = payload.signature;

  const user = await getUser(payload.uuid);

  const message = JSON.stringify({
    uuid: payload.uuid,
    coolness: payload.coolness,
    timestamp: payload.timestamp
  });

  if(!signature || !sessionless.verifySignature(signature, message, user.value)) {
    return ResponseError(401, 'Auth error');
  }

  return {doubleCool: 'double cool'};
};

Deno.serve({port: 3000}, async (request: Request) => {
console.log(request);
  if((request.method !== "POST" && request.method !== "PUT") || !request.body) {
    return ResponseError(404, "Method not supported");
  }
  const res = await dispatch(request);
console.log(res);
  return new Response(JSON.stringify(res), {
    headers: {
      "content-type": "application/json; charset=utf-8",
    }
  });
});
