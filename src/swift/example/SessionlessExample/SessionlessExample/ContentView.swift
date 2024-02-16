//
//  ContentView.swift
//  SessionlessExample
//
//  Created by Zach Babb on 2/9/24.
//

import SwiftUI
import Sessionless


struct User: Codable {
    let uuid: String
    let welcomeMessage: String
}

struct Coolness: Codable {
    let doubleCool: String
}

struct ContentView: View {
    
    let sessionless = Sessionless()
    @State var enteredText = ""
    @State var welcomeMessage = ""
    @State var uuid = ""
    @State private var showingAlert = false
    @State private var coolness = Coolness(doubleCool: "foo")
    
    func put(urlString: String, payload: Data, callback: @escaping (Error?, Data?) -> Void) async {
        guard let url = URL(string: urlString) else { return }
        var request = URLRequest(url: url)
        request.httpMethod = "PUT"
        request.setValue("application/json", forHTTPHeaderField: "Accept")
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        do {
            let (data, _) = try await URLSession.shared.upload(for: request, from: payload)
            callback(nil, data)
        } catch {
            callback(error, nil)
        }
    }
    
    func register(enteredText: String, callback: @escaping (Error?, Data?) -> Void) async {
        sessionless.generateKeys()
        guard let publicKey = sessionless.getKeys()?.publicKey else { return }
        let message = """
            {"publicKey":"\(publicKey)","enteredText":"\(enteredText)","timestamp":"right now"}
            """
        guard let signature = sessionless.sign(message: message) else { return }
        
        let payload = """
            {"publicKey":"\(publicKey)","enteredText":"\(enteredText)","timestamp":"right now","signature":\(signature.toString())}
            """
        print(payload)
        guard let data = payload.data(using: .utf8) else { return }
        await put(urlString: "http://localhost:3000/register", payload: data) { err, data in
            callback(err, data)
        }
    }
    
    func doCoolStuff(callback: @escaping (Error?, Data?) -> Void) async {
        let message = """
            {"coolness":"max","timestamp":"right now"}
            """
        guard let signature = sessionless.sign(message: message) else { return }
        let payload = """
            {"coolness":"max","timestamp":"right now","signature":\(signature.toString())}
            """
        print(payload)
        guard let data = payload.data(using: .utf8) else { return }
        await put(urlString: "http://localhost:3000/cool-stuff", payload: data, callback: {err, data in
            callback(err, data)
        })
    }
    
    struct ExampleTextField: View {
        @Binding var enteredText: String
        
        var body: some View {
            TextField("Enter Text", text: $enteredText)
        }
    }
    
    var body: some View {
        
        VStack {
            Text("""
                Welcome to the iOS Swift example app. This will connect to locally run servers on ports 3000
                 (primary) and 3001 (secondary).
                """
            ).padding(.all, 8)
                .alert("The server thinks your \(coolness.doubleCool)", isPresented: $showingAlert) {
                Button("OK") { }
            }
            Text("Enter some text here.")
                .padding(.all, 8)
            ExampleTextField(enteredText: $enteredText)
                .padding(.all, 8)
            Button("Register") {
                Task {
                    await register(enteredText: $enteredText.wrappedValue, callback: { err, data in
                        if let err = err {
                            print("error")
                            print(err)
                            return
                        }
                        guard let data = data else { return }
                        do {
                            let user = try JSONDecoder().decode(User.self, from: data)
                            uuid = user.uuid
                            welcomeMessage = user.welcomeMessage
                        } catch {
                            return
                        }
                    })
                }
            }.padding(.all, 8)
            if uuid.count > 3 && welcomeMessage.count > 3 {
                Text("\(welcomeMessage) now you can do cool stuff")
                    .padding(.all, 8)
                Button("Do Cool Stuff") {
                    Task {
                        await doCoolStuff { err, data in
                            if let err = err {
                                print("error")
                                print(err)
                                return
                            }
                            guard let data = data else { return }
                            do {
                                let resp = try JSONDecoder().decode(Coolness.self, from: data)
                                coolness = resp
                                showingAlert = true
                            } catch {
                                print(err)
                            }
                        }
                    }
                }
            }
        }
        .padding()
    }
}

