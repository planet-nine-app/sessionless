export const saveUser = async (uuid, pubKey) => {
  
 /**
  * This is a contrived example for this example, which should be run locally.
  * In an actual implementation your user store should be a database.
  */
  
  try {
    const usersString = await readFile('./users.json');
    const users = JSON.parse(usersString);
    users[uuid] = {
      pubKey,
      associatedKeys: {}
    };
    fs.writeFileSync('./users.json', JSON.stringify(users));
  } catch(err) {
    let users = {};
    users[uuid] = {
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
  user[associatedKeys][uuid] = pubKey;
  const users = JSON.parse(fs.readFileSync('./users.json'));
  fs.writeFileSync('./users.json', JSON.stringify(users));
};

export const saveValue = (uuid, value) => {
  const user = getUser(uuid);
  user.value = value;
  const users = JSON.parse(fs.readFileSync('./users.json'));
  fs.writeFileSync('./users.json', JSON.stringify(users));
};

export const getValue = (uuid) => {
  const user = getUser(uuid);
  return user.value;
};
