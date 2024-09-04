
export declare const AsyncFunction: (func: () => {}) => void;

export declare const generateKeys: (getKey: () => {}, saveKey: () => {}) => Promise<{privateKey: string, publicKey: string}>;

// did not know
export declare let getKeysFromDisk: () => void;
export declare const getKeys: () => void;

export declare const sign: (message: string) => Promise<string>;
export declare const verifySignature: (sig: {r: string, s:string}, message: string, pubKey: string) => boolean;
export declare const generateUUID: () => string ;
export declare const associate: (primarySignature: {r: string, s:string}, primaryMessage: string, primaryPublicKey: string, secondarySignature: {r: string, s:string}, secondaryMessage: string, secondaryPublicKey: string) => boolean;
  


