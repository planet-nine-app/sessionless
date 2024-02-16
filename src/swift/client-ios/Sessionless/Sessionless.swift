//
//  Sessionless.swift
//  SessionlessExample
//
//  Created by Zach Babb on 2/14/24.
//

import Foundation
import JavaScriptCore

public struct Keys {
    public let publicKey: String
    public let privateKey: String
    
    public func toData() -> Data {
        let data = Data(base64Encoded: "\(publicKey):\(privateKey)", options: [])
        return "\(publicKey):\(privateKey)".data(using: .utf8) ?? Data()
    }
    
    public func toString() -> String {
        return """
        {"publicKey":"\(publicKey)","privateKey":"\(privateKey)"}
        """
    }
    
}

public struct Signature {
    public let r: String
    public let s: String
    public let v: Int32
    
    public func toString() -> String {
        return """
            {"r":"\(r)","s":"\(s)","v":"\(v)"}
            """
    }
}

public class Sessionless {
    private var jsContext: JSContext?
    private var generateKeysJS: JSValue?
    private var signMessageJS: JSValue?
    private var verifySignatureJS: JSValue?
    
    private let keyService = "SessionlessKeyStore"
    private let keyAccount = "Sessionless"
    
    public init() {
        jsContext = getJSContext()
    }
    
    func getPathToJS() -> String? {
        let customBundle = Bundle(for: Sessionless.self)
        guard let resourceURL = customBundle.resourceURL?.appendingPathComponent("Sessionless.bundle") else { return nil }
        guard let resourceBundle = Bundle(url: resourceURL) else { return nil }
        guard let jsFileURL = resourceBundle.url(
        forResource: "crypto",
        withExtension: "js"
        ) else { return nil }
        print("url: \(jsFileURL)")
        return jsFileURL.absoluteString
    }
    
    func getJSContext() -> JSContext? {
        var jsSourceContents: String = ""
        if let jsSourcePath = getPathToJS() {
            do {
                jsSourceContents = try String(contentsOfFile: jsSourcePath)
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
        if let data = result.unsafelyUnwrapped as? Data,
           let keyString = String(data: data, encoding: .utf8) {
            let keyStringSplit = keyString.split(separator: ":")
            let keys = Keys(publicKey: String(keyStringSplit[0]), privateKey: String(keyStringSplit[1]))
            return keys
        }
        return nil
    }
    
    public func generateKeys() {
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
        }
    }
    
    public func sign(message: String) -> Signature? {
        guard let keys = getKeys(),
              let signaturejs = signMessageJS?.call(withArguments: [message, keys.privateKey]) else {
            return nil
        }
        print("signaturejs \(signaturejs)")
        let signature = Signature(r: signaturejs.objectForKeyedSubscript("r").toString(), s: signaturejs.objectForKeyedSubscript("s").toString(), v: signaturejs.objectForKeyedSubscript("v").toInt32())
        print("signature \(signature)")
        print(signature.r)
        return signature
    }
    
    public func verifySignature(signature: Signature, message: String, publicKey: String) -> Bool {
        return verifySignatureJS?.call(withArguments: [signature.toString(), message, publicKey]).toBool() ?? false
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
