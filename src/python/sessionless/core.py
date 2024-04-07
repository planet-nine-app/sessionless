import uuid

class SessionlessCore():
    def generateUUID(self):
        return uuid.uuid4().hex

    