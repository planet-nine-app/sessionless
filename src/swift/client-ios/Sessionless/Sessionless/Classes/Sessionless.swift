//
//  Sessionless.swift
//  SessionlessExample
//
//  Created by Zach Babb on 2/14/24.
//

import Foundation
import JavaScriptCore

public class Sessionless {
    public struct Keys {
        public let publicKey: String
        public let privateKey: String
        
        public init(publicKey: String, privateKey: String) {
            self.publicKey = publicKey
            self.privateKey = privateKey
        }
        
        public func toData() -> Data {
            let data = Data(base64Encoded: "\(publicKey):\(privateKey)", options: [])
            print("toData")
            print(data)
            print("\(publicKey):\(privateKey)".data(using: .utf8))
            
            return "\(publicKey):\(privateKey)".data(using: .utf8) ?? Data()
        }
        
        public func toString() -> String {
            return """
            {"publicKey":"\(publicKey)","privateKey":"\(privateKey)"}
            """
        }
        
    }
    private var jsContext: JSContext?
    private var generateKeysJS: JSValue?
    private var signMessageJS: JSValue?
    private var verifySignatureJS: JSValue?
    
    private let keyService = "SessionlessKeyStore"
    private let keyAccount = "Sessionless"
    
    public init() {
        jsContext = getJSContext()
    }
    
    func getPathToCrypto() -> URL? {
        let sessionlessBundle = Bundle(for: Sessionless.self)
        guard let resourceURL = sessionlessBundle.resourceURL?.appending(path: "Sessionless.bundle"),
              let resourceBundle = Bundle(url: resourceURL),
              let cryptoPathURL = resourceBundle.url(forResource: "crypto", withExtension: "js") else {
            print("no dice")
            return nil
        }
        print(cryptoPathURL.absoluteString)
        return cryptoPathURL
    }
    
    func getJSContext() -> JSContext? {
        var jsSourceContents: String = ""
        if let jsSourcePath = getPathToCrypto() {
            do {
                print(jsSourcePath)
                jsSourceContents = try String(contentsOf: jsSourcePath)
            } catch {
                print(error.localizedDescription)
            }
        }
        let logFunction : @convention(block) (String) -> Void =
        {
            (msg: String) in

            NSLog("Console: %@", msg)
        }
        let context = JSContext()
        let console = context?.objectForKeyedSubscript("console")
        context?.objectForKeyedSubscript("console").setObject(unsafeBitCast(logFunction, to: AnyObject.self), forKeyedSubscript: "log")
        
        context?.evaluateScript(jsSourceContents)
        
        let sessionless = context?.objectForKeyedSubscript("globalThis")?.objectForKeyedSubscript("sessionless")
        generateKeysJS = sessionless?.objectForKeyedSubscript("generateKeys")
        signMessageJS = sessionless?.objectForKeyedSubscript("sign")
        verifySignatureJS = sessionless?.objectForKeyedSubscript("verifySignature")
        
        return context
    }
    
    public func saveKeys(data: Data) -> Bool {
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: keyService,
            kSecAttrAccount as String: keyAccount,
            kSecValueData as String: data
        ]
        let status = SecItemAdd(query as CFDictionary, nil)
        
        return status == errSecSuccess
    }
    
    public func getKeys() -> Keys? {
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: keyService,
            kSecAttrAccount as String: keyAccount,
            kSecReturnData as String: true
        ]
        var result: CFTypeRef?
        let status = SecItemCopyMatching(query as CFDictionary, &result)
        if result == nil {
            return nil
        }
        if let data = result.unsafelyUnwrapped as? Data,
           let keyString = String(data: data, encoding: .utf8) {
            let keyStringSplit = keyString.split(separator: ":")
            let keys = Keys(publicKey: String(keyStringSplit[0]), privateKey: String(keyStringSplit[1]))
            return keys
        }
        return nil
    }
    
    public func generateKeys() -> Keys? {
        var bytes = [UInt8](repeating: 0, count: 32)
        let status = SecRandomCopyBytes(kSecRandomDefault, bytes.count, &bytes)

        if status == errSecSuccess { 
            print(bytes)
            let data = Data(bytes: bytes)
            let hex = data.hexEncodedString()
            let keys = generateKeysJS?.call(withArguments: [hex])
            print(keys)
            print(keys?.objectForKeyedSubscript("privateKey"))
            let pubKeyData = Data(bytes: keys?.objectForKeyedSubscript("publicKey").toArray() as [UInt8])
            let pubKeyHex = pubKeyData.hexEncodedString()
            print(pubKeyHex)
            
            let keysToSave = Keys(publicKey: pubKeyHex, privateKey: keys?.objectForKeyedSubscript("privateKey").toString() ?? "")
            self.saveKeys(data: keysToSave.toData())
            return keysToSave
        }
        return nil
    }
    
    public func sign(message: String) -> String? {
        guard let keys = getKeys(),
              let signaturejs = signMessageJS?.call(withArguments: [message, keys.privateKey]) else {
            return nil
        }
        let signature = signaturejs.toString()
        return signature
    }
    
    public func verifySignature(signature: String, message: String, publicKey: String) -> Bool {
        return verifySignatureJS?.call(withArguments: [signature, message, publicKey]).toBool() ?? false
    }
    
    public func generateUUID() -> String {
        return UUID().uuidString
    }
}

extension Array where Element == UInt8 {

    public var bigEndianUInt: UInt? {
        guard self.count <= MemoryLayout<UInt>.size else {
            return nil
        }
        var number: UInt = 0
        for i in (0 ..< self.count).reversed() {
            number = number | (UInt(self[self.count - i - 1]) << (i * 8))
        }

        return number
    }
}

extension Data {
    struct HexEncodingOptions: OptionSet {
        let rawValue: Int
        static let upperCase = HexEncodingOptions(rawValue: 1 << 0)
    }

    func hexEncodedString(options: HexEncodingOptions = []) -> String {
        let format = options.contains(.upperCase) ? "%02hhX" : "%02hhx"
        return self.map { String(format: format, $0) }.joined()
    }
}
