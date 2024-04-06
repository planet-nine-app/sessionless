const kv = await Deno.openKv();

export const saveUser = async (uuid, pubKey) => {
  await kv.set([uuid], { 
    pubKey,
    associatedKeys: {}
  });
  await kv.set([pubKey], uuid);
};

export const getUser = async (uuid) => {
  const user = await kv.get([uuid]);
console.log(user);
  return user.value;
};

export const saveValue = async (uuid, value) => {
  await kv.set([uuid, 'value'], value);
};

export const getValue = async (uuid)  => {
  return await kv.get([uuid, 'value']);
};

export const associateKey = async (user, uuid, publicKey) => {
  await kv.set([uuid], {
    publicKey: user.publicKey,
    associatedKeys: {
      uuid: publicKey
    }
  });
};
