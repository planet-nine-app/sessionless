window.register = () => {
  const payload = {
    enteredText: "foo",
    timestamp: new Date().getTime() + ''
  };
  fetch('http://localhost:3000/register', {
    method: 'POST',
    headers: {
      Accept: 'application/json',
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload)
  }) 
  .then((resp) => resp.json())
  .then(json => {
    console.log(json.userUUID);
    window.userUUID = json.userUUID;
    document.getElementById('welcomeMessage').innerHTML = json.welcomeMessage;
  })
  .catch(err => console.warn(err) );
};

window.doCoolStuff = () => {
  const payload = {
    userUUID: window.userUUID,
    coolness: 'max',
    timestamp: 'right now'
  };

  fetch('http://localhost:3000/cool-stuff', {
      method: 'POST',
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(payload)
    })
    .then((resp) => resp.json())
    .then(json => {
      window.alert(`The server thinks you\'re ${json.doubleCool}!`);
    })
    .catch(console.warn);
};
