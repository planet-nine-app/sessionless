import { useState } from 'react';
import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View, TextInput, Pressable } from 'react-native';
import sessionless from '@zachbabb/sessionless-expo';

export default function App() {
  const [uuid, setUUID] = useState(null);
  const [welcomeMessage, setWelcomeMessage] = useState(null);
  const [input, setInput] = useState('');

  const onChange = (text) => {
    setInput(text);
  };

  const register = async () => {
    await sessionless.generateKeys();
console.log('keys should be generated');
    const keys = await sessionless.getKeys();
console.log('got keys: ' + JSON.stringify(keys));
console.log(input);
    const payload = {
      enteredText: input,
      timestamp: 'right now'
    };
console.log("payload: " + JSON.stringify(payload));
    payload.signature = await sessionless.sign(JSON.stringify(payload));
console.log(JSON.stringify(payload));

    await fetch('http://localhost:3000/register', {
      method: 'PUT',
      headers: {
	Accept: 'application/json',
	'Content-Type': 'application/json',
      },
      body: JSON.stringify(payload)
    })
    .then((resp) => resp.json())
    .then(json => {
      console.log('resp: ' + json.uuid);
      setUUID(json.uuid);
      setWelcomeMessage(json.welcomeMessage);
    })
    .catch(err => console.warn(err) );    
  };

  return (
    <View style={styles.container}>
      <Text style={{padding: 8}}>Welcome to the Expo example app. This will connect to locally run servers on ports 3000
            (primary) and 3001 (secondary).
      </Text>
      <Text style={{padding: 8}}>To start, enter some text here, and push the button to register</Text>
      <TextInput placeholder="Enter Text" onChangeText={onChange} style={{padding: 8}}/>
      <Pressable style={{
        padding: 8,
        backgroundColor: 'green'
      }} onPress={register}>
        <Text>Register</Text>
      </Pressable>
      { uuid && welcomeMessage ? 
        <Text>{welcomeMessage}</Text> : null }
      <StatusBar style="auto" />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
