import sessionless from 'sessionless-node';
import superagent from 'superagent';
import config from '../../config/local.js';

global.currentPrivateKey = '';
global.getKeys = () => {
  return (() => { return {privateKey: currentPrivateKey} })();
};

const register = async (color) => {
  const colors = config.colors;
  const colorURL = colors[color].serverURL;
  const colorSignaturePlacement = colors[color].signature;

  const keys = await sessionless.generateKeys((generatedKeys) => {
    currentPrivateKey = generatedKeys.privateKey;
  }, getKeys);

  const message = {
    pubKey: keys.pubKey,
    enteredText: "Foo",
    timestamp: new Date().getTime() + ''
  };

<<<<<<< HEAD
console.log(JSON.stringify(message));

  let signature = await sessionless.sign(JSON.stringify(message));

  signature = signature.length % 2 === 1 ? '0' + signature : signature;

console.log(signature.length);
console.log(signature);

console.log('message is: ' + JSON.stringify(message));

=======
>>>>>>> e2378953 (tests and csharp server example)
  const signature = await sessionless.sign(JSON.stringify(message));

  let post = superagent.post(colorURL + '/register');

  if(colorSignaturePlacement === 'payload') {
    const payload = {
      ...message,
      signature
    };
    post = post.send(payload)               
  } else {
    post = post.send(message)
               .set('signature', signature);
  }
  return post.set('Content-Type', 'application/json')
	     .set('Accept', 'application/json')
         .then(res => {
           res.body.color = color;
           return res;
         });

};

export default register;
