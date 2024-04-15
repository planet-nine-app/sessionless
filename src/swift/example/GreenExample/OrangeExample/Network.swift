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

enum NetworkError: Error {
    case networkError
}

class Network {
    class func get(urlString: String, headers: [String: String]? = nil, callback: @escaping (Error?, Data?) -> Void) async {
        guard let url = URL(string: urlString) else { return }
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("application/json", forHTTPHeaderField: "Accept")
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        if let headers = headers {
            for var header in headers {
                request.setValue(header.value, forHTTPHeaderField: header.key)
            }
        }
        do {
            let (data, response) = try await URLSession.shared.data(for: request)
            guard let httpResponse = response as? HTTPURLResponse else { return }
            if httpResponse.statusCode > 300 {
                callback(NetworkError.networkError, nil)
                return
            }
            callback(nil, data)
        } catch {
            callback(error, nil)
        }
    }
    
    class func post(urlString: String, headers: [String: String]? = nil, payload: Data, callback: @escaping (Error?, Data?) -> Void) async {
        guard let url = URL(string: urlString) else { return }
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Accept")
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        if let headers = headers {
            for var header in headers {
                request.setValue(header.value, forHTTPHeaderField: header.key)
            }
        }
        print("about to post \(String(data: payload, encoding: .utf8))")
        print("to: \(urlString)")
        print("with headers: \(request.allHTTPHeaderFields)")
        do {
            let (data, _) = try await URLSession.shared.upload(for: request, from: payload)
            print(String(data: data, encoding: .utf8))
            print("foo{")
            callback(nil, data)
        } catch {
            print(error)
            callback(error, nil)
        }
    }
    
    class func register(baseURL: String, enteredText: String, callback: @escaping (Error?, Data?) -> Void) async {
        let sessionless = Sessionless()
        sessionless.generateKeys()
        guard let publicKey = sessionless.getKeys()?.publicKey else { return }
        let timestamp = "".getTime()
        let message = """
            {"pubKey":"\(publicKey)","enteredText":"\(enteredText)","timestamp":"\(timestamp)"}
            """
        guard let signature = sessionless.sign(message: message) else { return }
        
        let payload = """
            {"pubKey":"\(publicKey)","enteredText":"\(enteredText)","timestamp":"\(timestamp)"}
            """
        guard let data = payload.data(using: .utf8) else { return }
        
        print("signature: \(signature)")
        await Network.post(urlString: "\(baseURL)/register", headers: ["signature": signature], payload: data) { err, data in
            callback(err, data)
        }
    }
    
    class func doCoolStuff(baseURL: String, callback: @escaping (Error?, Data?) -> Void) async {
        let sessionless = Sessionless()
        let message = """
            {"coolness":"max","timestamp":"right now"}
            """
        guard let signature = sessionless.sign(message: message) else { return }
        let payload = """
            {"coolness":"max","timestamp":"right now","signature":"\(signature)"}
            """
        guard let data = payload.data(using: .utf8) else { return }
        await Network.post(urlString: "\(baseURL)/cool-stuff", payload: data, callback: {err, data in
            callback(err, data)
        })
    }
    
    class func associate(baseURL: String, associateKey: AssociateKey, callback: @escaping (Error?, Data?) -> Void) async {
        let sessionless = Sessionless()
        let uuid = Persistence.getUUID()
        let timestamp = "".getTime()
        
        let message = """
            {"uuid":"\(uuid)","timestamp":"\(timestamp)"}
        """
        let base64Message1 = message.toBase64()
        
        guard let signature = sessionless.sign(message: base64Message1) else { return }
        
        let message2 = """
            {"uuid":"\(associateKey.uuid)","timestamp":"\(associateKey.timestamp),"pubKey":"\(associateKey.pubKey)"}
        """
        let base64Message2 = message2.toBase64()
        
        let payload = """
            {"message1":"\(base64Message1)","signature1":"\(signature)","message2":"\(base64Message2)","pub_key":"\(associateKey.pubKey)"}
        """
        
        guard let data = payload.data(using: .utf8) else { return }
        await Network.post(urlString: "\(baseURL)/associate", payload: data, callback: {err, data in
            callback(err, data)
        })
    }
    
    class func getValue(baseURL: String, callback: @escaping (Error?, Data?) -> Void) async {
        let sessionless = Sessionless()
        let timestamp = "".getTime()
        let uuid = Persistence.getUUID()
        let message = """
        {"uuid":"\(uuid)","timestamp":"\(timestamp)"}
        """
        let base64Message = message.toBase64()
        
        guard let signature = sessionless.sign(message: base64Message) else { return }
        
        let urlString = "message=\(base64Message)"
        guard let urlEncodedString = urlString.addingPercentEncoding(withAllowedCharacters: .urlHostAllowed) else { return }
        
        await Network.get(urlString: "\(baseURL)/value?\(urlEncodedString)", headers: ["signature": signature], callback: callback)
    }
    
    class func setValue(value: String, baseURL: String, callback: @escaping (Error?, Data?) -> Void) async {
        let sessionless = Sessionless()
        let message = """
        {"uuid":"\(Persistence.getUUID())","timestamp":"\("".getTime())","value":"\(value)"}
        """
        
        guard let signature = sessionless.sign(message: message) else { return }
        
        guard let data = message.data(using: .utf8) else { return }
        await Network.post(urlString: "\(baseURL)/value", headers: ["signature": signature], payload: data, callback: callback)
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
    
    func toBase64() -> String {
        return Data(self.utf8).base64EncodedString()
    }
}
