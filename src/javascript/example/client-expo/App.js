import React, { useState } from 'react';
import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View, TextInput, Pressable, Alert } from 'react-native';
import sessionless from '@zachbabb/sessionless-expo';

const baseURL = 'https://rare-robin-97.deno.dev';
//const baseURL = 'http://localhost:3000';

export default function App() {
  const [userUUID, setUserUUID] = useState();
  const [welcomeMessage, setWelcomeMessage] = useState();
  const [input, setInput] = useState('');

  const onChangeText = (text) => {
    setInput(text);
  };

  const register = async () => {
    await sessionless.generateKeys();
    const keys = await sessionless.getKeys();
    const payload = {
      publicKey: keys.publicKey,
      enteredText: input,
      timestamp: 'right now'
    };
    payload.signature = await sessionless.sign(JSON.stringify(payload));

    await fetch(`${baseURL}/register`, {
      method: 'PUT',
      headers: {
	Accept: 'application/json',
	'Content-Type': 'application/json',
      },
      body: JSON.stringify(payload)
    })
    .then((resp) => resp.json())
    .then(json => {
      setUserUUID(json.userUUID);
      setWelcomeMessage(json.welcomeMessage);
    })
    .catch(err => console.warn(err) );    

  };

  const doCoolStuff = async () => {
    const payload = {
      userUUID,
      coolness: 'max',
      timestamp: 'right now'
    };
    payload.signature = await sessionless.sign(JSON.stringify(payload));
    
    await fetch(`${baseURL}/cool-stuff`, {
      method: 'PUT',
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(payload)
    })
    .then((resp) => resp.json())
    .then(json => {
      Alert.alert(`The server thinks you\'re ${json.doubleCool}!`);
    })
    .catch(console.warn);
  };
  return (
    <View style={styles.container}>
      <Text style={{padding: 8}}>Welcome to the Expo example app. This will connect to locally run servers on ports 3000
            (primary) and 3001 (secondary).
      </Text>
      <Text style={{padding: 8}}>To start, enter some text here, and push the button to register</Text>
      <TextInput placeholder="Enter Text" 
        onChange={() => {}}
        onChangeText={onChangeText} 
        style={{padding: 8}}/>
      <Pressable style={{
        padding: 8,
        backgroundColor: 'green'
      }} onPress={register}>
        <Text>Register</Text>
      </Pressable>
      { userUUID && welcomeMessage ? 
        <>
	  <Text>{`${welcomeMessage} now you can do cool stuff.`}</Text>
	  <Pressable style={{
	    padding: 8,
	    backgroundColor: 'green'
	  }} onPress={doCoolStuff}>
	    <Text>Do Cool Stuff</Text>
	  </Pressable>
        </>
	  : null }
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
