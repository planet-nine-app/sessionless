from setuptools import setup, find_packages

VERSION = '0.0.1' 
DESCRIPTION = 'Sessionless is an attempt to make authentication handling easier for developers without traditional sessions.'
LONG_DESCRIPTION = 'Sessionless is an authentication protocol that uses the cryptography employed by Bitcoin and Ethereum to authenticate messages sent between a client and a server. Within this protocol, you create and store a private key on the client and then use that key to sign messages; those messages are then verified by the server via the public key associated with the client. When you verify a message you also certify its provenance. Because no other secret need be shared between client and server, sessions are wholly unnecessary.'

# Setting up
setup(
        name="sessionless", 
        version=VERSION,
        author="Sessionless Team",
        author_email="zach@planetnine.app",
        description=DESCRIPTION,
        long_description=LONG_DESCRIPTION,
        packages=find_packages(),
        install_requires=[],
        keywords=['authentication', 'cryptography', 'authenticate'],
        classifiers= [
            "Development Status :: 3 - Alpha",
            "Intended Audience :: Developers",
            "Programming Language :: Python :: 3",
            "Operating System :: MacOS :: MacOS X",
            "Operating System :: Microsoft :: Windows",
        ]
)