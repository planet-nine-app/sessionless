from setuptools import setup, find_packages

VERSION = '0.0.1' 
DESCRIPTION = 'Sessionless is an attempt to make authentication handling easier for developers and users, while also enabling interesting behavior not possible with traditional sessions.'
LONG_DESCRIPTION = 'Sessionless is an authentication protocol that uses the cryptography employed by Bitcoin and Ethereum to authenticate messages sent between a client and a server. Within this protocol, you create and store a private key on the client and then use that key to sign messages; those messages are then verified by the server via the public key associated with the client. When you verify a message you also certify its provenance. Because no other secret need be shared between client and server, sessions are wholly unnecessary.'

# Setting up
setup(
       # the name must match the folder name 'verysimplemodule'
        name="sessionless", 
        version=VERSION,
        author="Sessionless Team",
        author_email="zach@planetnine.app",
        description=DESCRIPTION,
        long_description=LONG_DESCRIPTION,
        packages=find_packages(),
        install_requires=[], # add any additional packages that 
        # needs to be installed along with your package. Eg: 'caer'
        
        keywords=['authentication', 'cryptography', 'authenticate'],
        classifiers= [
            "Development Status :: 3 - Alpha",
            "Intended Audience :: Education",
            "Programming Language :: Python :: 3",
            "Operating System :: MacOS :: MacOS X",
            "Operating System :: Microsoft :: Windows",
        ]
)