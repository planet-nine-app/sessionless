from sessionless import SessionlessSecp256k1
# from Crypto.Hash import keccak
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

    message_to_sign = f'{{"pubKey":"{public_key}","enteredText":"Foo","timestamp":"{timestamp}"}}'

    print(message_to_sign)

    signature = await sessionless.sign(message_to_sign.encode('ascii'), get_key)

    message = {
        "pubKey": public_key,
        "enteredText": "Foo",
        "timestamp": timestamp,
        "signature": signature
    }

    headers = {'content-type': 'application/json'}
    response = requests.post('http://127.0.0.1:3001/register', headers=headers, json=message) # update this to be dynamic
    
    print(f'response: {response}')

    return response, private_key, public_key
