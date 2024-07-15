from flask import Flask
from sessionless import SessionlessSecp256k1
from src.user.user import create_user
from src.user.user import get_user
app = Flask(__name__)

@app.errorhandler(403)
def resource_not_found(e):
    return jsonify(error=str(e)), 403

@app.route('/register', methods=('POST'))
def create_user:
    pubKey = request.args.get('pubKey')
    enteredText = request.args.get('enteredText')
    timestamp = request.args.get('timestamp')
    signature = request.args.get('signature')
    message = f'{{"pubKey":"{public_key}","enteredText":"Foo","timestamp":"{timestamp}"}}'

    if !sessionless.verifySignature(signature, message, pubKey): 
        abort(403, description="Auth error")

    uuid = create_user(pubKey)
    resp = { 
        "uuid" = uuid,
        "welcomeMessage" = "Welcome to Sessionless"
    }
    return resp

@app.route('/cool-stuff')
def do_cool_stuff():
    // get user
    // auth
    // return cool stuff
    return // python dictionary of coolness = doubleCool
