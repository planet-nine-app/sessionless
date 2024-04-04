const kv = await Deno.openKv();

export const saveUser = async (uuid, publicKey) => {
  await kv.set([uuid], publicKey);
  await kv.set([publicKey], uuid);
};

export const getUser = async (uuid) => {
  return await kv.get([uuid]);
};

export const saveValue = async (uuid, value) => {
  await kv.set([uuid, 'value'], value);
};

export const getValue = async (uuid)  => {
  return await kv.get([uuid, 'value']);
};
