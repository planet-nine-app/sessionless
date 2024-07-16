from flask import Flask, request
from sessionless import SessionlessSecp256k1
from src.user.user import create_user
from src.user.user import get_user
app = Flask(__name__)

import logging

@app.errorhandler(403)
def resource_not_found(e):
    return jsonify(error=str(e)), 403

@app.route('/register', methods=['POST'])
def register():
    json = request.json
    pubKey = json['pubKey']
    enteredText = json['enteredText']
    timestamp = json['timestamp']
    signature = json['signature']
    message = f'{{"pubKey":"{pubKey}","enteredText":"Foo","timestamp":"{timestamp}"}}'

    sessionless = SessionlessSecp256k1()
    verified = sessionless.verifySignature(signature, message, pubKey)
    if not verified:
        abort(403, description="Auth error")

    uuid = create_user(pubKey)
    resp = { 
        "uuid": uuid,
        "welcomeMessage": "Welcome to Sessionless"
    }
    return resp

@app.route('/cool-stuff', methods=['POST'])
def do_cool_stuff():
    json = request.json
    uuid = json['uuid']
    coolness = json['coolness']
    timestamp = json['timestamp']
    signature = json['signature']
    message = f'{{"uuid":"{uuid}","coolness":"max","timestamp":"{timestamp}"}}'
    user = get_user(uuid)

    sessionless = SessionlessSecp256k1()
    verified = sessionless.verifySignature(signature, message, user['pubKey'])
    if not verified:
        abort(403, description="Auth error")

    resp = {
        "doubleCool": "double cool"
    }
    return resp
