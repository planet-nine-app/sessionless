from setuptools import setup, find_packages

def read_markdown_file(file_path):
    with open(file_path, 'r', encoding='utf-8') as file:
        markdown_content = file.read()
    return markdown_content

VERSION = '0.0.4' 
DESCRIPTION = 'Sessionless is an attempt to make authentication handling easier for developers without traditional sessions.'
LONG_DESCRIPTION = read_markdown_file("./README.md")

# Setting up
setup(
        name="sessionless", 
        version=VERSION,
        author="Sessionless Team",
        author_email="zach@planetnine.app",
        description=DESCRIPTION,
        long_description=LONG_DESCRIPTION,
        long_description_content_type="text/markdown",
        packages=find_packages(),
        install_requires=["secp256k1", "uuid"],
        keywords=['authentication', 'cryptography', 'authenticate'],
        classifiers= [
            "Development Status :: 3 - Alpha",
            "Intended Audience :: Developers",
            "Programming Language :: Python :: 3",
            "Operating System :: MacOS :: MacOS X",
            "Operating System :: Microsoft :: Windows",
        ]
)