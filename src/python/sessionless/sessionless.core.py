import uuid

class SessionlessCore():
    def __init__(self) -> None:
        pass

    def generateUUID(self):
        return uuid.uuid4().hex

    