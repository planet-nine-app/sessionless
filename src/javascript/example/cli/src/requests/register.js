import sessionless from 'sessionless-node';
import superagent from 'superagent';
import config from '../../config/local.js';

global.currentPrivateKey = '';
global.getKeys = () => {
console.log(currentPrivateKey);
  return (() => { return {privateKey: currentPrivateKey} })();
};

const register = async (color) => {
  console.log('color is: ' + color);
  const colors = config.colors;
console.log('config is: ' + JSON.stringify(colors[color]));
  const colorURL = colors[color].serverURL;
  const colorSignaturePlacement = colors[color].signature;

  const keys = await sessionless.generateKeys((generatedKeys) => {
console.log("generatedKeys: " + JSON.stringify(generatedKeys));
    currentPrivateKey = generatedKeys.privateKey;
  }, getKeys);

  const message = {
    pubKey: keys.pubKey,
    enteredText: "Foo",
    timestamp: new Date().getTime() + ''
  };

console.log('message is: ' + JSON.stringify(message));

  const signature = await sessionless.sign(JSON.stringify(message));

console.log('how \'bout local? ' + (await sessionless.verifySignature(signature, JSON.stringify(message), keys.pubKey)));

console.log('posting to: ' + colorURL + '/register');
  let post = superagent.post(colorURL + '/register');

  if(colorSignaturePlacement === 'payload') {
    const payload = {
      ...message,
      signature
    };
console.log('payload is: ' + JSON.stringify(payload));
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
