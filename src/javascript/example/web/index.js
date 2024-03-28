window.register = () => {
  const payload = {
    enteredText: "foo",
    timestamp: new Date().getTime() + ''
  };
  fetch('http://localhost:3000/register', {
    method: 'PUT',
    headers: {
      Accept: 'application/json',
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload)
  }) 
  .then((resp) => resp.json())
  .then(json => {
    console.log(json.uuid);
    window.uuid = json.uuid;
    document.getElementById('welcomeMessage').innerHTML = json.welcomeMessage;
  })
  .catch(err => console.warn(err) );
};

window.doCoolStuff = () => {
  const payload = {
    coolness: 'max',
    timestamp: 'right now'
  };

  fetch('http://localhost:3000/cool-stuff', {
      method: 'PUT',
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
