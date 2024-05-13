from sessionless import SessionlessSecp256k1
import requests

async def do_cool_stuff(registration_tuple):
    print('registered!')
    print('Gonna just do blue for now')

    json = registration_tuple[0].json()

    print(f'json looks like: {json}')
    print(f'uuid: {json['uuid']}')

    uuid = json['uuid']
    private_key = registration_tuple[1]
    public_key = registration_tuple[2]
    
    sessionless = SessionlessSecp256k1()

    timestamp = 'right now' #we'll deal with time later

    def get_key():
        return private_key

    message_to_sign = f'{{"uuid":"{uuid}","coolness":"max","timestamp":"{timestamp}"}}'

    signature = await sessionless.sign(message_to_sign.encode('ascii'), get_key)

    message = {
        "uuid": uuid,
        "coolness": "max",
        "timestamp": timestamp,
        "signature": signature
    }

    headers = {'content-type': 'application/json'}
    response = requests.post('http://127.0.0.1:3001/cool-stuff', headers=headers, json=message) # update this to be dynamic
    
    print(f'response: {response}')

    return response
