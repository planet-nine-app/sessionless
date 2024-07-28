const config = {
  colors: {
    blue: {
      serverURL: "http://127.0.0.1:3001",
      signature: "payload",
      language: "javascript",
    },
    green: {
      serverURL: "http://127.0.0.1:5000",
      signature: "payload",
      language: "python",
    },
    yellow: {
      serverURL: "http://127.0.0.1:3002",
      signature: "payload",
      language: "typescript",
    },
    red: {
      serverURL: "http://127.0.0.1:3000",
      signature: "header",
      language: "rust",
    },
    /*    cyan: {
      serverURL: 'http://127.0.0.1:8080',
      signature: 'payload',
      language: 'java'
    },*/
    magenta: {
      serverURL: "http://127.0.0.1:5139",
      signature: "header",
      language: "csharp",
    },
    purple: {
      serverURL: "http://localhost:8080",
      signature: "payload",
      language: "kotlin",
    },
  },
  languages: {
    kotlin: "purple",
    javascript: "blue",
    typescript: "yellow",
    rust: "red",
    java: "bgWhite",
    csharp: "magenta",
    python: "green",
  },
};

export default config;
