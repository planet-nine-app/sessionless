const config = {
  colors: {
    blue: {
      serverURL: 'http://127.0.0.1:3001',
      signature: 'payload'
    },
    green: {
      serverURL: 'http://127.0.0.1:3002',
      signature: 'payload'
    },
    red: {
      serverURL: 'http://127.0.0.1:3000', 
      signature: 'header'
    },
    bgWhite: {
      serverURL: 'http://127.0.0.1:8080',
      signature: 'payload'
    },
    magenta: {
      serverURL: 'http://127.0.0.1:5139',
      signature: 'header'
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
