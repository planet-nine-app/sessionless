const kv = await Deno.openKv();

export const saveUser = async (uuid, pubKey) => {
  await kv.set([uuid], { 
    uuid,
    pubKey,
    associatedKeys: {}
  });
  await kv.set([pubKey], uuid);
};

export const getUser = async (uuid) => {
  const user = await kv.get([uuid]);
  return user.value;
};

export const setValue = async (uuid, value) => {
  await kv.set([uuid, 'value'], value);
};

export const getValue = async (uuid)  => {
  return await kv.get([uuid, 'value']);
};

export const associateKey = async (user, uuid, pubKey) => {
  await kv.set([user.uuid], {
    pubKey: user.pubKey,
    associatedKeys: {
      associatedUser: {
        uuid,
        pubKey
      }
    }
  });
  await kv.set([uuid], {
    uuid,
    pubKey,
    associatedUUID: user.uuid
  });
};

export const getUserByAssociatedKey = async (uuid) => {
  const associatedUser = await kv.get([uuid]);
  return associatedUser.value;
};
