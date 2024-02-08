import sessionless from './sessionless.js';

let myKeys;

sessionless.generateKeys((keys) => {
  console.log(keys);
  myKeys = keys;
}, () => {
  return myKeys;
});

const signature = sessionless.sign(JSON.stringify({
  foo: 'foo',
  bar: 'bar'
}));

console.log(signature);

const uuid = sessionless.createUUID();

const bool = sessionless.verifySignature(signature, JSON.stringify({foo: 'foo', bar: 'bar'}));

console.log(bool);
