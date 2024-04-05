//
//  Network.swift
//  SessionlessExample
//
//  Created by Zach Babb on 4/4/24.
//

import Foundation
import Sessionless

struct AssociateKey {
    let uuid: String
    let timestamp: String
    let pubKey: String
    let signature: String
}

class Network {
    class func post(urlString: String, payload: Data, callback: @escaping (Error?, Data?) -> Void) async {
        guard let url = URL(string: urlString) else { return }
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Accept")
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        do {
            let (data, _) = try await URLSession.shared.upload(for: request, from: payload)
            callback(nil, data)
        } catch {
            callback(error, nil)
        }
    }
    
    class func register(enteredText: String, callback: @escaping (Error?, Data?) -> Void) async {
        let sessionless = Sessionless()
        sessionless.generateKeys()
        guard let publicKey = sessionless.getKeys()?.publicKey else { return }
        let message = """
            {"publicKey":"\(publicKey)","enteredText":"\(enteredText)","timestamp":"right now"}
            """
        guard let signature = sessionless.sign(message: message) else { return }
        
        let payload = """
            {"publicKey":"\(publicKey)","enteredText":"\(enteredText)","timestamp":"right now","signature":\(signature.toString())}
            """
        guard let data = payload.data(using: .utf8) else { return }
        await Network.post(urlString: "http://localhost:3000/register", payload: data) { err, data in
            callback(err, data)
        }
    }
    
    class func doCoolStuff(callback: @escaping (Error?, Data?) -> Void) async {
        let sessionless = Sessionless()
        let message = """
            {"coolness":"max","timestamp":"right now"}
            """
        guard let signature = sessionless.sign(message: message) else { return }
        let payload = """
            {"coolness":"max","timestamp":"right now","signature":\(signature.toString())}
            """
        guard let data = payload.data(using: .utf8) else { return }
        await Network.post(urlString: "http://localhost:3000/cool-stuff", payload: data, callback: {err, data in
            callback(err, data)
        })
    }
    
    class func associate(associateKey: AssociateKey, callback: @escaping (Error?, Data?) -> Void) async {
        let sessionless = Sessionless()
    }
    
    class func getValue(callback: @escaping (Error?, Data?) -> Void) async {
        let sessionless = Sessionless()
    }
    
    class func setValue(callback: @escaping (Error?, Data?) -> Void) async {
        let sessionless = Sessionless()
    }
}

extension String {
    func getTime() -> String {
        let currentDate = NSDate()
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
        dateFormatter.timeZone = TimeZone(identifier: "UTC")
        let date = dateFormatter.date(from: dateFormatter.string(from: currentDate as Date))
        let nowDouble = date!.timeIntervalSince1970
        return String(Int(nowDouble * 1000.0))
    }
}
