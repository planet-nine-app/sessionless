from sessionless import SessionlessSecp256k1

def create_user(public_key):
    sessionless = SessionlessSecp256k1()
    uuid = sessionless.generateUUID()
    saved_user_tuple = f'{uuid}|{public_key}'
    
    with open('users.txt', 'w') as file:
        file.write(saved_user_tuple)
    
    return uuid

def get_user(uuid):
    with open('users.txt') as file:
        datafile = file.readlines()

    for line in datafile:
        if uuid in line:
            user_split = line.split('|')
            return {
                "uuid": user_split[0],
                "pubKey": user_split[1]
            }

    return {
        "uuid": "",
        "pubKey": ""
    }
