import fs from 'fs';

export const saveUser = async (uuid, pubKey) => {
  
 /**
  * This is a contrived example for this example, which should be run locally.
  * In an actual implementation your user store should be a database.
  */
  
  try {
    const usersString = await readFile('./users.json');
    const users = JSON.parse(usersString);
    users[uuid] = {
      uuid,
      pubKey,
      associatedKeys: {}
    };
    fs.writeFileSync('./users.json', JSON.stringify(users));
  } catch(err) {
    let users = {};
    users[uuid] = {
      uuid,
      pubKey,
      associatedKeys: {}
    };
    fs.writeFileSync('./users.json', JSON.stringify(users), { flag: 'w' });
  }
};

export const getUser = (uuid) => {
  const usersString = fs.readFileSync('./users.json');
  const users = JSON.parse(usersString);
  return users[uuid];
};

export const getUserPublicKey = (uuid) => {
  const usersString = fs.readFileSync('./users.json');
  const users = JSON.parse(usersString);
  return users[uuid].pubKey;
};

export const associateKey = (user, uuid, pubKey) => {
  user.associatedKeys[uuid] = {
    uuid,
    pubKey
  };
  let users = JSON.parse(fs.readFileSync('./users.json'));
  users[user.uuid] = user;
  fs.writeFileSync('./users.json', JSON.stringify(users));
};

export const getUserByAssociatedKey = (uuid) => {
  const usersString = fs.readFileSync('./users.json');
  const users = JSON.parse(usersString);
  let user;

 /**
  * In a real implementation you'll have a db for persistence, and this will be 
  * a query instead of this kind of janky map lookup
  */
console.log('Looking for key with uuid: ' + uuid);
  for(let userUUID in users) {
console.log(users[userUUID].associatedKeys);
    if(users[userUUID].associatedKeys[uuid]) {
      user = users[userUUID];
      user.pubKey = users[userUUID].associatedKeys[uuid].pubKey;
    }
  }
  return user;
};

export const saveValue = (uuid, value) => {
  const user = getUser(uuid);
  if(!user) {
    return;
  }
  user.value = value;
  let users = JSON.parse(fs.readFileSync('./users.json'));
  users[user.uuid] = user;
  fs.writeFileSync('./users.json', JSON.stringify(users));
};

export const getValue = (uuid) => {
  const user = getUser(uuid);
console.log(user);
console.log(user.value);
  return user.value;
};
