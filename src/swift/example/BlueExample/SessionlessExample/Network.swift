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
    class func get(urlString: String, callback: @escaping (Error?, Data?) -> Void) async {
        guard let url = URL(string: urlString) else { return }
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("application/json", forHTTPHeaderField: "Accept")
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
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
    
    class func register(baseURL: String, enteredText: String, callback: @escaping (Error?, Data?) -> Void) async {
        let sessionless = Sessionless()
        sessionless.generateKeys()
        guard let publicKey = sessionless.getKeys()?.publicKey else { return }
        let message = """
            {"pubKey":"\(publicKey)","enteredText":"\(enteredText)","timestamp":"right now"}
            """
        guard let signature = sessionless.sign(message: message) else { return }
        
        let payload = """
            {"pubKey":"\(publicKey)","enteredText":"\(enteredText)","timestamp":"right now","signature":"\(signature)"}
            """
        guard let data = payload.data(using: .utf8) else { return }
        await Network.post(urlString: "\(baseURL)/register", payload: data) { err, data in
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
        
        guard let signature = sessionless.sign(message: message) else { return }
        
        let payload = """
            {"uuid1":"\(uuid)","timestamp1":"\(timestamp)","signature1":"\(signature)","uuid2":"\(associateKey.uuid)","timestamp2":"\(associateKey.timestamp)","pubKey":"\(associateKey.pubKey)","signature2":"\(associateKey.signature)"}
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
        {"timestamp":"\(timestamp)","uuid":"\(uuid)"}
        """
        
        guard let signature = sessionless.sign(message: message) else { return }
        
        let urlString = "timestamp=\(timestamp)&uuid=\(uuid)&signature=\(signature)"
        guard let urlEncodedString = urlString.addingPercentEncoding(withAllowedCharacters: .urlHostAllowed) else { return }
        
        await Network.get(urlString: "\(baseURL)/value?\(urlEncodedString)", callback: callback)
    }
    
    class func setValue(value: String, baseURL: String, callback: @escaping (Error?, Data?) -> Void) async {
        let sessionless = Sessionless()
        let message = """
        {"timestamp":"\("".getTime())","uuid":"\(Persistence.getUUID())","value":"\(value)"}
        """
        
        guard let signature = sessionless.sign(message: message) else { return }
        
        let payload = """
        {"timestamp":"\("".getTime())","uuid":"\(Persistence.getUUID())","value":"\(value)","signature":"\(signature)"}
        """
        
        guard let data = payload.data(using: .utf8) else { return }
        await Network.post(urlString: "\(baseURL)/value", payload: data, callback: callback)
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
