import sessionless from "npm:sessionless-node";
console.log(sessionless);

const kv = await Deno.openKv();

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
};

const saveUser = async (userUUID, publicKey) => {
  await kv.set([userUUID], publicKey);
  await kv.set([publicKey], userUUID);
};

const getUser = async (userUUID) => {
  return await kv.get([userUUID]);
};

const register = async (request: Request): Response | Error => {
  const payload = await request.json();
  const signature = payload.signature;

  const message = JSON.stringify({
    publicKey: payload.publicKey,
    enteredText: payload.enteredText,
    timestamp: payload.timestamp
  });

  if(!signature || !sessionless.verifySignature(signature, message, payload.publicKey)) {
    return ResponseError(401, 'Auth error');
  }

  const userUUID = sessionless.generateUUID();
  await saveUser(userUUID, payload.publicKey);
  return {userUUID, welcomeMessage: "Welcome to Sessionless"};
};

const doCoolStuff = async (request: Request): Response | Error => {
  const payload = await request.json();
  const signature = payload.signature;

  const user = await getUser(payload.userUUID);

  const message = JSON.stringify({
    userUUID: payload.userUUID,
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
