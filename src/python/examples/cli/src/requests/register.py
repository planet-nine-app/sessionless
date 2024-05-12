from sessionless import SessionlessSecp256k1
import requests

def saveKey(keyPair):
    print('I think we don\'t do this for now...')

async def register(color, language):
    print('register!')
    print('Gonna just do blue for now')
    
    sessionless = SessionlessSecp256k1()

    private_key, public_key = sessionless.generateKeys(saveKey)

    timestamp = 'right now' #we'll deal with time later

    def get_key():
        return private_key

    signature = await sessionless.sign(f'{{"pubKey":"{public_key}","enteredText":"Foo","timestamp":"{timestamp}"}}', get_key)

    message = {
        "pubKey": public_key,
        "enteredText": "Foo",
        "timestamp": timestamp,
        "signature": signature
    }

    response = requests.post('http://127.0.0.1:3001/register', json=message) # update this to be dynamic
    
    print(f'response: {response}')

    return response, private_key, public_key
