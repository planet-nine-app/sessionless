import sessionless from 'sessionless-node';
import superagent from 'superagent';
import config from '../../config/local.js';

const doCoolStuff = async (bodyFromRegistration) => {
  const { uuid, welcomeText, color } = bodyFromRegistration;

  const colorURL = config.colors[color].serverURL;
  const colorSignaturePlacement = config.colors[color].signature;

  let message = {
    uuid,
    coolness: 'max',
    timestamp: new Date().getTime() + ''
  };

  const signature = await sessionless.sign(JSON.stringify(message));

  let post = superagent.post(colorURL + '/cool-stuff');

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

export default doCoolStuff;
