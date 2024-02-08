import sessionless from 'sessionless';
import * as SecureStore from 'expo-secure-store';

const sless = sessionless;
console.log(JSON.stringify(sless));

const generateKeys = () => {
  sless.generateKeys(async (keys) => {
    console.log(keys)
    await SecureStore.setItemAsync(keysToSaveString, JSON.stringify(keys));
  }, () => {});
};

generateKeys();

const getKeys = async () => {
  const keyString = await SecureStore.getItemAsync(keysToSaveString);
  return JSON.parse(keyString);
};

const sign = sless.sign;

const verifySignature = sless.verifySignature;

const generateUUID = sless.generateUUID;
