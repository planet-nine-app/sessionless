//
//  Sessionless.swift
//  SessionlessExample
//
//  Created by Zach Babb on 2/9/24.
//

import Foundation


/*
 generateKeys(saveKeys?: keys => void, getKeys?: () => Keys): generates a private/public keypair and stores it in the platform's secure storage. Takes an optional save keys function for platforms that don't have clear cut secure storage.

 getKeys(): gets keys from secure storage

 sign(message: String): signs a message with the user's private key

 verifySignature(message: String, signature: String, publicKey: String): verifies a given signature with a public key

 generateUUID(): creates a unique UUID for a user
 */

struct Keys {
    let publicKey: String
    let privateKey: String
}

enum InternalErrors: Error {
    case internalError
}

class Sessionless {
    
    init() {

    }
    
    func generateKeys(_ saveKeys: ((_ keys: Keys) -> Void)?, _ getKeys: (() -> Void)?) throws -> String {
        //let a = secp256k1_int(32)
        //print(a)
        let ctx = secp256k1_context_create(UInt32(SECP256K1_CONTEXT_VERIFY))
        //let foo = secp256k1_context_create(0)
        
        /*guard var rand = [UInt8].secureRandom(count: 2)?.bigEndianUInt else {
            throw Inte_secprnalErrors.internalError
        }
        rand += 55
        
        guard let bytes = [UInt8].secureRandom(count: Int(rand)) else {
            throw InternalErrors.internalError
        }
        let bytesHash = SHA3(variant: .keccak256).calculate(for: bytes)
        
        return String(bytes: bytesHash, encoding: .utf8) ?? ""*/
        return "foo"
    }
}



extension Array where Element == UInt8 {

    static func secureRandom(count: Int) -> [UInt8]? {
        var array = [UInt8](repeating: 0, count: count)

        let fd = open("/dev/urandom", O_RDONLY)
        guard fd != -1 else {
            return nil
        }
        defer {
            close(fd)
        }

        let ret = read(fd, &array, MemoryLayout<UInt8>.size * array.count)
        guard ret > 0 else {
            return nil
        }

        return array
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
