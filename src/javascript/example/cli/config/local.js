const config = {
  colors: {
    red: {
      serverURL: "http://127.0.0.1:3000",
      signature: "header",
      language: "rust",
    },
    orange: {
      serverURL: 'http://127.0.0.1:8080',
      signature: 'payload',
      language: 'java'
    },
    yellow: {
      serverURL: "http://127.0.0.1:3002",
      signature: "payload",
      language: "typescript",
    },
    green: {
      serverURL: "http://127.0.0.1:5000",
      signature: "payload",
      language: "python",
    },
    blue: {
      serverURL: "http://127.0.0.1:3001",
      signature: "payload",
      language: "javascript",
    },
    indigo: {
      serverURL: "http://127.0.0.1:5139",
      signature: "header",
      language: "csharp",
    },
    violet: {
      serverURL: "http://localhost:8082",
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
