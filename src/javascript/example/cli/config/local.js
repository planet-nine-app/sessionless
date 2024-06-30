const config = {
  colors: {
    blue: {
      serverURL: 'http://127.0.0.1:3001',
      signature: 'payload',
      language: 'javascript'
    },
    green: {
      serverURL: 'http://127.0.0.1:3002',
      signature: 'payload',
      language: 'typescript'
    },
    red: {
      serverURL: 'http://127.0.0.1:3000', 
      signature: 'header',
      language: 'rust'
    },
/*    cyan: {
      serverURL: 'http://127.0.0.1:8080',
      signature: 'payload',
      language: 'java'
    },*/
    magenta: {
      serverURL: 'http://127.0.0.1:5139',
      signature: 'header',
      language: 'csharp'
    }
  },
  languages: {
    javascript: 'blue',
    typescript: 'green',
    rust: 'red',
    java: 'bgWhite',
    csharp: 'magenta'
  }
};

export default config;
